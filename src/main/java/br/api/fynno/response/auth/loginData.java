package br.api.fynno.response.auth;

public record loginData(
        Integer userId,
        String name,
        String email
) {
}
