package br.api.fynno.response.auth;

public record CheckUserLoginResponse(
        Integer userId,
        String name,
        String email
) {
}
