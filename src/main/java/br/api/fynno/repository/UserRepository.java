package br.api.fynno.repository;

import br.api.fynno.model.entity.User;
import br.api.fynno.response.user.UserSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Interface do repositório de ações de persistência
// da classe User.
// ------------------------------------------------------------
// Nesta interface é estendida a JpaRepository que já possui
// todas os métodos necessários para executar comandos de
// persistência de dados. Como parâmetro na herança indicamos
// qual é o modelo de entidade, neste caso User e qua o tipo de
// dados da identificação única (Id) da entidade.
// ------------------------------------------------------------
// Como é uma interface, quando chamar no controller não ocorre
// a injeção de dependência, sendo assim, neste cenário, temos
// uma abstração do repositório.
// ------------------------------------------------------------
// Um fator muito interessante em spring boot é que as consultas
// são criadas onde sua regra de negócio vai de acordo com a assinatura
// ou seja, de acordo com a forma como assina o método é como ele vai
// funcionar. um exemplo, se eu escrever um método findByActive, automaticamente
// a consulta é selecionada onde o critério por status.
public interface UserRepository extends JpaRepository<User, Integer> {
    // encontra usuários contendo o nome informado em qualquer posição
    // ignorando letras maiúsculas e minusculas.
    List<User> findByNameContainingIgnoreCaseAndEmailContainingAndActive(
            String name,
            String email,
            boolean active
    );
}
