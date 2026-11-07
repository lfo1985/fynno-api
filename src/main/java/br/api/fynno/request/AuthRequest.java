package br.api.fynno.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotNull(message = "Informe o e-mail")
        @Email(message = "E-mail inválido..")
        String email,

        @NotNull(message = "Informe a senha.")
        @Size(min = 6, message = "A senha deve ter mais de 6 dígitos.")
        String password
) {}
