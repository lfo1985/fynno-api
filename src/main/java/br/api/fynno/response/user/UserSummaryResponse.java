package br.api.fynno.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserSummaryResponse(
        Integer id,
        String name,
        String email,
        boolean active
) {
    @JsonProperty("status")
    public String status () {
        return this.active ? "Ativo" : "Inativo";
    }
}
