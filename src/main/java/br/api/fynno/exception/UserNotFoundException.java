package br.api.fynno.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends RuntimeException {
    // caso não seja encontrado o usuário cadastrado, dispara esta exception.
    public UserNotFoundException(String message) {
        super(message);
    }
}
