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
    // Recebe apenas a mensagem e chama o construtor
    // de três parâmetros, usando success=true e data=null.
    public OkResponse(String message) {
        this(message, true, null);
    }
    // Recebe mensagem e dados e chama o construtor
    // de três parâmetros, usando success=true.
    public OkResponse(String message, Object data) {
        this(message, true, data);
    }
}
