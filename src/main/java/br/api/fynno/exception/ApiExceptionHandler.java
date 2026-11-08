package br.api.fynno.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

// Esta anotação serve para indicar ao controller
// que as exceções dos controladores serão disparados
// nesta classe exception.
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    // obtêm um logger associado à exception guardando a referência em LOGGER.
    // esta instância é lida apenas dentro desta classe e não pode ser substituída.
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // a anotação ExceptionHandler encaminha a exceção para este método
    // através do parâmetro exception.
    // quando ocorrer qualquer exceção que seja do tipo Exception,
    // automaticamente será capturada e tratada neste método.
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception exception) {
        // registra o log criando o arquivo .log com o que veio na exceção
        LOGGER.error(exception.getMessage(), exception);

        // retorna o problema com a definição de status code e message..
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Occurrence error in application."
        );
    }

    // exceção que é disparada quando não encontra usuários.
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // exceção que é disparada quando não encontra usuários.
    @ExceptionHandler(AuthException.class)
    public ProblemDetail handleAuthException(AuthException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
    }

    // exceção das validações que vem nas requests e são
    // configuradas via anotação nas dtos.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        // captura os erros lançados no List da exceção
        // e os mapeia criando uma lista de erros em error.
        var errors = exception
                // lê o resultado da exceção
                .getBindingResult()
                // deste resultado procura o field errors
                .getFieldErrors()
                // permite a leitura destes errors
                .stream()
                // mapeia os erros preparando uma lista
                // requireNonNull faz validação se o objeto
                // está nulo ou não.
                .map(erro -> Objects.requireNonNull(
                        erro.getDefaultMessage(),
                        "field invalid: " + erro.getField())
                )
                // converte em lista
                .toList();
        // cria o problema com o status que já vem nativamente
        // da exceção em questão.
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                "invalid data."
        );

        // seta alguns dados e define a listagem dos erros
        // numa prop nova chamada "errors".
        problem.setTitle("Invalid Data");
        problem.setProperty("errors", errors);

        // realiza o retorno do handle que já existe, porém
        // aplicamos o polimorfismo nela onde modificamos o
        // comportamento da mesma.
        return handleExceptionInternal(
                exception,
                problem,
                headers,
                status,
                request
        );
    }
}
