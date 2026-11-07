package br.api.fynno.response.auth;

public record UserLoginDataResponse(
        Integer userId,
        String name,
        String email
) {
}
