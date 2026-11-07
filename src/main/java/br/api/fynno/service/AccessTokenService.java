package br.api.fynno.service;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public class AccessTokenService {

    private static final SecureRandom  RANDOM = new SecureRandom();
    private final SecretKeySpec secret;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public AccessTokenService(@Value("${fynno.auth.token-secret}") String secretToken) {
        byte[] key = Base64.getDecoder().decode(secretToken);
        if (key.length < 32) {
            throw new IllegalArgumentException(
                    "FYNNO_TOKEN_SECRET deve conter pelo menos 32 bytes em Base64"
            );
        }
        secret = new SecretKeySpec(key, HMAC_ALGORITHM);
    }

    public String generateToken () {
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

    public String sha256Encode (String token) {
        try {
            // captura os bytes do token gerado com UTF-8
            // e calcula o hmac sha-256 destes bytes.
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secret);
            // Como o retorno da linha acima é um array, convertemos
            // os bytes em formato hexadecimal para poder gravar
            // na tabela do banco de dados.
            // O hash gerado acima foi criado a partir de uma secret,
            // ou seja, se algum usuário tentar burlar o token de alguma
            // maneira, existe uma chave secreta que impede esta criação.
            byte[] hash = mac
                    .doFinal(token.getBytes(StandardCharsets.UTF_8));
            // converte em string e formata em hexadecimal para gravar
            // no banco.
            return HexFormat
                    .of()
                    .formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken (String token, String tokenHash) {
        // obriga o usuário a informar os dois hashes
        if(token == null || tokenHash == null) {
            return false;
        }
        // codifica o token que veio do usuário
        String hash = sha256Encode(token);
        // compara com o token já codificado que está
        // no banco de dados.
        return hash.equals(tokenHash);
    }
}
