package br.api.fynno.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public class AccessTokenService {

    private static final SecureRandom  RANDOM = new SecureRandom();

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
            // e calcula o sha-256 destes bytes.
            byte[] hash = MessageDigest
                    .getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            // como o retorno da linha acima é um array, convertemos
            // os bytes em formato hexadecimal para poder gravar
            // na tabela do banco de dados.
            return HexFormat
                    .of()
                    .formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
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
