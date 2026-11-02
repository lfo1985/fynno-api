package br.api.fynno.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Nome obrigatório.")
        @Size(max = 255, message = "O nome deve ter no máximo 255 chars.")
        String name,

        @NotBlank(message = "E-mail obrigatório.")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 chars.")
        @Email(message = "E-mail inválido.")
        String email,

        boolean active
) {
}
