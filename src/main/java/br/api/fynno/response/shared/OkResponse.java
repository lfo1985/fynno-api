package br.api.fynno.response.shared;

public record OkResponse(
        String message,
        boolean success,
        Object data
) {
    // quando houver um parâmetro opcional
    // cria os construtores onde os métodos
    // não obrigatórios não são implementados
    // e aplica o null onde deveria atribuir o
    // valor.
    // -------------------------------------------------
    // essa classe tem o parâmetro data que não
    // é obrigatório ter, portanto implementamos
    // desta maneira:
    public OkResponse(String message) {
        this(message, true, null);
    }
    // método construtor que chama ele mesmo atribuindo
    // naturalmente o valor de true, sem a necessidade
    // de informar no nível do controller.
    public OkResponse(String message, Object data) {
        this(message, true, data);
    }
}
