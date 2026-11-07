package br.api.fynno.response.auth;

public record AuthResponse(
        UserLoginDataResponse user,
        String token
) {
}
