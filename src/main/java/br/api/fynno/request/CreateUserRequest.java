package br.api.fynno.request;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// dto para receber os dados via http
// -------------------------------------------------
// utilizando dto podemos usar quais campos forem
// necessários para enviar os dados e não precisa
// combinar com os nomes dos campos, basta
// dentro do controlador informar ao setter a informação
// que deseja salvar como, por exemplo, o "senha"; no banco
// o campo se chama password_hash, mas esta coluna fica
// oculta para o http, então passa o valor
// é passado pelo atributo password e depois definido
// para a entidade user através do setter setPasswordHash().
public record CreateUserRequest(
        // Utilização de notações de validação.
        // -----------------------------------------------------
        // Essa notações são bem sugestivas, então não preciso
        // descrevê-las.
        @NotBlank(message = "Nome obrigatório.")
        @Size(max = 255, message = "O nome deve ter no máximo 255 chars.")
        String name,

        @NotBlank(message = "E-mail obrigatório.")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 chars.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Senha obrigatória.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 dígitos.")
        String password,

        @NotBlank(message = "Precisa confirmar a senha.")
        String password2
) {
        // Uma maneira de através do request validar se as senhas
        // são iguais. Utiliza uma anotação assertFalse, onde se caso
        // as senhas forem diferentes (retorna falso o equals do return)
        // a mensagem é exibida e trava a requisição.
        @AssertFalse(message = "As senhas não são iguais.")
        public boolean isPasswordEquals() {
                return !this.password.equals(this.password2);
        }

}
