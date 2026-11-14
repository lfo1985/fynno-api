package br.api.fynno.controller;

import br.api.fynno.helper.Token;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Console;

public abstract class Controller {
    protected HttpServletRequest getRequest () {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // Afirma que a linha abaixo sempre vai ser diferente
        // de nulo.
        assert requestAttributes != null;
        // Retorna os métodos para trabalhar com requisição.
        return requestAttributes.getRequest();
    }

    protected String getToken () {
        String token = this.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        return Token.resolve(token);
    }
}
