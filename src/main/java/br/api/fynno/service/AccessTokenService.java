package br.api.fynno.service;

import br.api.fynno.contracts.ITokenProvider;
import br.api.fynno.exception.TokenProviderException;
import br.api.fynno.mapper.UserMapper;
import br.api.fynno.model.entity.AccessToken;
import br.api.fynno.model.entity.User;
import br.api.fynno.repository.AccessTokenRepository;
import br.api.fynno.response.auth.CheckUserLoginResponse;
import lombok.Setter;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AccessTokenService implements ITokenProvider {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final AccessTokenRepository accessTokenRepository;
    private final UserMapper userMapper;

    @Setter
    private Integer minutes;

    @Setter
    private String tokenSecret;

    public AccessTokenService(
            AccessTokenRepository accessTokenRepository,
            UserMapper userMapper
    ) {
        this.accessTokenRepository = accessTokenRepository;
        this.userMapper = userMapper;
    }

    public void revokedToken(String token) {
        String hashToken = this._sha256Encode(token);
        AccessToken accessToken = this._validationToken(hashToken);
        accessToken.setRevokedAt(LocalDateTime.now());

        this.accessTokenRepository.save(accessToken);
    }

    public String registryToken(User user) {
        // Gera o token para o usuário
        String token = this._generateToken();
        // Invoca a entidade AccessToken para registrar o token
        // na base de dados.
        AccessToken accessToken = new AccessToken();
        accessToken.setUser(user);
        accessToken.setTokenHash(this._sha256Encode(token));
        // Aplica a expiração do token conforme a configuração.
        LocalDateTime expiresTime = LocalDateTime.now().plusMinutes(this.minutes);
        accessToken.setExpiresAt(expiresTime);
        // Salva o token em access_token
        this.accessTokenRepository.save(accessToken);

        return token;
    }

    public CheckUserLoginResponse checkToken (String token) {
        AccessToken accessToken = this._validationToken(this._sha256Encode(token));
        return this.userMapper.toChekUserLoginResponse(accessToken.getUser());
    }

    private String _generateToken () {
        // declara um array de 32 posições do tipo byte
        byte[] bytes = new byte[32];
        // cria o array com valores aleatórios alocados
        // em cada posição do array.
        RANDOM.nextBytes(bytes);
        // cria a representação textual deste array de
        // bytes preparando a string para poder ser enviada
        // via header do navegador.
        return Base64
                // remove sinais de mais e barra por hífen e underline
                .getUrlEncoder()
                // elimina o sinal de igual adicionado quando sobra espaço.
                .withoutPadding()
                // codifica os bytes.
                .encodeToString(bytes);
    }

    private String _sha256Encode (String token) {
        try {
            // Valida se o token está vazio.
            if(token == null) {
                throw new IllegalArgumentException("Token is empty.");
            }

            // Chave secreta para gerar o token.
            byte[] key = Base64.getDecoder().decode(this.tokenSecret);
            // Valida se a chave é válida.
            if (key.length < 32) {
                throw new IllegalArgumentException(
                        "FYNNO_TOKEN_SECRET deve conter pelo menos 32 bytes em Base64"
                );
            }

            // captura os bytes do token gerado com UTF-8
            // e calcula o hmac sha-256 destes bytes.
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            // Como o retorno da linha acima é um array, convertemos
            // os bytes em formato hexadecimal para poder gravar
            // na tabela do banco de dados.
            // O hash gerado acima foi criado a partir de uma secret,
            // ou seja, se algum usuário tentar burlar o token de alguma
            // maneira, existe uma chave secreta que impede esta criação.
            byte[] hash = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            // converte em string e formata em hexadecimal para gravar
            // no banco.
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private AccessToken _validationToken (String tokenHash) {
        AccessToken accessToken = this.accessTokenRepository.findByTokenHash(tokenHash);

        // Validar se o token existe.
        if(accessToken == null) {
            throw new TokenProviderException("Login is invalid.");
        }

        // se o token já foi revogado, emite exceção.
        if(accessToken.getRevokedAt() != null) {
            throw new TokenProviderException("Token is inválid.");
        }

        // Esta validação consiste em certificar se o now() (data de hoje)
        // é menor que a data de expiração. o usAfter significa "é depois",
        // ou seja, é depois dessa data?
        if(LocalDateTime.now().isAfter(accessToken.getExpiresAt())) {
            throw new TokenProviderException("Token is expired.");
        }

        return accessToken;
    }
}
