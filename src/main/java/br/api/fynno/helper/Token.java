package br.api.fynno.helper;

import br.api.fynno.exception.TokenProviderException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;

public class Token {
    public static String resolve(String token) {
        String prefixo = "Bearer ";
        // está comparando se existe a palavra Bearer escrito no início do token.
        if (token == null || !token.regionMatches(true, 0, prefixo, 0, prefixo.length())) {
            throw new TokenProviderException("Token is empty");
        }
        // começa a ler a string depois do bearer para poder
        // enviar o token puro para validação.
        token = token.substring(prefixo.length()).trim();
        // se estiver vazio, emite um erro.
        if (token.isEmpty()) {
            throw new  TokenProviderException("Token is empty");
        }

        return token;
    }
}
