package br.api.fynno.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record UserDetailResponse(
        Integer id,
        String name,
        String email,
        boolean active,

        // Esta é a maneira mais maravilhosa de se formatar
        // uma saída de data/hora. Ao invés de implementação
        // complexa de métodos e mais métodos e helpers para
        // formatar para o formato brasileiro, esta anotação
        // já atende e formata da maneira que quiser a data/hora
        // sem muitas implementações e mais, neste caso este
        // formato é apenas para exibição da API, ou seja,
        // considerando um nível mais baixo, se precisar manipular
        // a data de alguma maneira, o formato de banco é preservado
        // e pode ser utilizado no tipo LocalDateTime.
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime created_at,

        // Idem comentário de cima.
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime updated_at
) {
    // Esta anotação cria uma nova propriedade na resposta
    // onde pode ser implementado qualquer funcionalidade no
    // escopo que poderá retornar na resposta. Uma prop personalizada.
    @JsonProperty("status")
    public String status () {
        return this.active ? "Ativo" : "Inativo";
    }
}
