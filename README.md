# Fynno API

API de gerenciamento de usuários desenvolvida com Java e Spring Boot.

## Rotas

Os caminhos abaixo são relativos à URL base da aplicação. Todas as operações bem-sucedidas retornam **HTTP 200 OK**. Para POST e PUT, envie `Content-Type: application/json`.

| Método | Rota | Descrição | Corpo da resposta |
| --- | --- | --- | --- |
| GET | `/users` | Lista todos os usuários | Lista de usuários resumidos |
| GET | `/users/search` | Busca usuários por filtros | Lista de usuários resumidos |
| GET | `/users/{id}` | Consulta um usuário | Usuário detalhado |
| POST | `/users` | Cria um usuário | Mensagem de sucesso e usuário detalhado em `data` |
| PUT | `/users/{id}` | Atualiza nome, email e estado | Mensagem de sucesso e usuário detalhado em `data` |
| DELETE | `/users/{id}` | Exclui um usuário | Mensagem de sucesso |

`id` é um parâmetro inteiro obrigatório no caminho. Listagens sem resultados retornam `[]`.

## Filtros de busca

Exemplo: `GET /users/search?name=Fernando&email=example.com&active=true`

| Parâmetro | Tipo | Obrigatório | Comportamento |
| --- | --- | --- | --- |
| `name` | string | Não | Nome contém o texto, ignorando maiúsculas e minúsculas. Padrão: `""` |
| `email` | string | Não | Email contém o texto. Padrão: `""` |
| `active` | boolean | Não | Filtra por `true` ou `false`. Quando omitido, assume `false` no controller atual |

Os filtros são combinados com **AND**. A sensibilidade a maiúsculas no filtro de email depende da configuração do banco.

> No código atual, omitir `active` não remove o filtro: a busca retorna apenas usuários com `active = false`. Para permitir ausência real do filtro, use `Boolean` e adapte a consulta para tratar `null`.

## Corpo das requisições

### Criar usuário — `POST /users`

```json
{
  "name": "Fernando",
  "email": "fernando@example.com",
  "password": "senha-exemplo"
}
```

### Atualizar usuário — `PUT /users/1`

```json
{
  "name": "Fernando",
  "email": "fernando@example.com",
  "active": true
}
```

As regras de obrigatoriedade e validação dos campos são definidas em `CreateUserRequest` e `UpdateUserRequest`. A senha não é alterada por esse PUT.

## Respostas

Exemplos ilustrativos conforme os DTOs e o mapper apresentados.

### Listagem e busca

```json
[
  {
    "id": 1,
    "name": "Fernando",
    "email": "fernando@example.com",
    "active": true
  }
]
```

### Consulta por ID

```json
{
  "id": 1,
  "name": "Fernando",
  "email": "fernando@example.com",
  "active": true,
  "createdAt": "2026-11-02T10:00:00",
  "updatedAt": "2026-11-02T11:00:00"
}
```

O formato das datas depende dos tipos dos campos e da configuração de serialização.

### Criação e atualização

```json
{
  "message": "User created!",
  "success": true,
  "data": {
    "id": 1,
    "name": "Fernando",
    "email": "fernando@example.com",
    "active": true,
    "createdAt": "2026-11-02T10:00:00",
    "updatedAt": "2026-11-02T10:00:00"
  }
}
```

Na atualização, a mensagem é `"User updated!"`.

### Exclusão

```json
{
  "message": "User deleted!",
  "success": true
}
```

O campo `data` pode aparecer como `null` se o `OkResponse` não estiver configurado para omitir campos nulos.

## Erros

- Consulta, atualização ou exclusão de ID inexistente lança `UserNotFoundException` com a mensagem `"User not found."`.
- POST e PUT validam o corpo com `@Valid`; JSON ou parâmetros com formato inválido também podem gerar erro.
- O status e o corpo das exceções personalizadas dependem do tratamento configurado na aplicação; o controller apresentado não define esse formato.
