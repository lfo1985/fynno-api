package br.api.fynno.controller;

import br.api.fynno.exception.UserNotFoundException;
import br.api.fynno.helper.HashPassword;
import br.api.fynno.mapper.UserMapper;
import br.api.fynno.model.entity.User;
import br.api.fynno.repository.UserRepository;
import br.api.fynno.request.CreateUserRequest;
import br.api.fynno.request.UpdateUserRequest;
import br.api.fynno.response.shared.OkResponse;
import br.api.fynno.response.user.UserDetailResponse;
import br.api.fynno.response.user.UserSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Sempre que usar a anotação RestController
// significa que a classe é uma controladora
// dentro da arquitetura API Rest.
// --------------------------------------------------
// Ao utilizar o RequestMapping, combinando com
// as notações de mapeamento sobre os métodos desta
// classe, cada método será mapeado com uma rota
// para acessar via URL, seja com GET, POST, DELETE
// ou PUT.
// --------------------------------------------------
// O parâmetro do RequestMapping é a base url da rota.
@RestController
@RequestMapping("users")
public class UserController {

    // final: a referência é atribuída no construtor e não será trocada.
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Como já sabemos, temos a abstração da classe
    // UserRepository e injetando atribuindo a instância
    // no controller através do construtor, para implementar
    // na propriedade privada que declaramos neste controller.
    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // Rota de leitura de dados.
    // ----------------------------------------------------
    // Neste caso todos os dados que estão informados no banco
    // serão retornados via API para o usuário; é um list da entidade
    // User.
    @GetMapping
    public List<UserSummaryResponse> findAll() {
        // estamos convertendo os dados de retorno na dto
        // response quando se trata de leitura de todos os
        // dados.
        return this
                .userRepository
                // seleciona todos os usuários
                .findAll()
                // permite a leitura destes dados encontrados
                .stream()
                // mapeia criando preparando uma lista de itens
                // com os dados da instância do response criado.
                .map(this.userMapper::toUserSummaryResponse)
                .toList();
    }

    @GetMapping("search")
    public List<UserSummaryResponse> search(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "active", required = false) boolean active
    ) {
        // um fator curioso do spring boot:
        // as regras de negócios são montadas
        // conforme a assinatura dos métodos
        // é onde a escrita já diz o que a consulta
        // irá fazer.
        // ------------------------------------------
        // esta método então tem como regra de negócio:
        // find by name containing (funciona como o like)
        // find by email containing (funciona como o like)
        // find by active
        return this
                .userRepository
                .findByNameContainingIgnoreCaseAndEmailContainingAndActive(
                        name,
                        email,
                        active
                )
                .stream()
                .map(this.userMapper::toUserSummaryResponse)
                .toList();
    }

    // Rota para pegar um registro (por ID) da entidade.
    // ----------------------------------------------------
    // Neste caso o usuário informa o ID via rota para obter o usuário
    // específico através da sua identificação única no banco de dados.
    // A anotação GetMapping é utilizada para definir a rota como GET e
    // ao lado do parâmetro ID temos a anotação PathVariable que configura
    // o parâmetro id como receptor da identificação informada na rota.
    // Neste cenário podemos ver também que o GetMapping precisa indicar a
    // a posição deste ID na rota. Naturalmente dentro do API Rest, neste
    // caso, seria user/id, por exemplo, user/1.
    @GetMapping("{id}")
    public UserDetailResponse findById(@PathVariable("id") Integer id) {
        // O usuário é procurado pelo sistema através do ID informado
        // caso o mesmo não seja encontrado, ele retorna uma exceção
        // de resposta de requisição onde seta para o erro 404 e emite
        // a mensagem de "user not found".
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        // Retorna o objeto response definindo precisamente
        // apenas alguns dados para exibição.
        return this.userMapper.toUserDetailResponse(user);
    }

    // Definição do verbo POST para este método
    // ao acessar via URL deverá ser uma requisição
    // POST /userr
    // ----------------------------------------------------
    // Ao usar a anotação RequestBody você determina
    // que os dados qeu são passados via body na requisição
    // http seja recebido pelo objeto em questão no
    // parâmetro do método. No exemplo deste método
    // os dados enviados na camada BODY da requisição
    // serão atribuídos ao objeto user da entidade User.
    @PostMapping
    public ResponseEntity<OkResponse> create(@RequestBody @Valid CreateUserRequest request) {
        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(HashPassword.encode(request.password()));

        this.userRepository.save(user);

        return ResponseEntity.ok(
                new OkResponse(
                        "User created!",
                        this.userMapper.toUserDetailResponse(user)
                )
        );
    }

    @PutMapping("{id}")
    public ResponseEntity<OkResponse> update(
            @RequestBody @Valid UpdateUserRequest request,
            @PathVariable("id") Integer id
    ) {
        User user = this
                .userRepository
                .findById(id)
                .orElseThrow(() ->  new UserNotFoundException("User not found."));

        user.setName(request.name());
        user.setEmail(request.email());
        user.setActive(request.active());

        this.userRepository.save(user);

        return ResponseEntity.ok(
                new OkResponse(
                        "User updated!",
                        this.userMapper.toUserDetailResponse(user)
                )
        );
    }

    @DeleteMapping("{id}")
    // utilizamos o ResponseEntity e configuramos ele para
    // receber com resposta uma instância do OkResponse.
    public ResponseEntity<OkResponse> delete(@PathVariable("id") Integer id) {
        // valida se o usuário ainda existe no sistema.
        if(!this.userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found.");
        }
        // realia a remoção.
        this.userRepository.deleteById(id);
        // retorna sucesso
        return ResponseEntity.ok(
                new OkResponse("User deleted!")
        );
    }
}
