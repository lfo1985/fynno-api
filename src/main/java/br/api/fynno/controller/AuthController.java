package br.api.fynno.controller;

import br.api.fynno.contracts.IAccesssTokenService;
import br.api.fynno.contracts.IAuthService;
import br.api.fynno.request.AuthRequest;
import br.api.fynno.response.auth.AuthResponse;
import br.api.fynno.response.shared.OkResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final IAuthService authService;
    private final IAccesssTokenService accessTokenService;

    public AuthController(
            IAuthService authService,
            IAccesssTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.authService = authService;
    }

    @PostMapping
    public OkResponse login(@RequestBody @Valid AuthRequest  authRequest) {
        // Gera o token para o usuário
        String token = this.accessTokenService.generateToken();
        // Aplica o hash no token e grava na tabela access_token
        String tokenHash = this.accessTokenService.sha256Encode(token);
        // Realiza o login
        var userDataLogin = this.authService.doLogin(
                authRequest.email(),
                authRequest.password(),
                tokenHash
        );
        // Se tudo der certo, retorna o login!
        return new OkResponse(
                "login successful.",
                true,
                new AuthResponse(userDataLogin, token)
        );
    }

    @PostMapping("logout")
    public OkResponse logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        // realiza a conversão do token do usuário que está vindo do header
        // para sha256 e buscar no access_token a condição deste registro.
        String token = this.resolveToken(authorization);
        String tokenHash = this.accessTokenService.sha256Encode(token);
        // revogar o mesmo, ou seja, logout (inválidar) o token.
        this.authService.RevokedToken(tokenHash);
        return new OkResponse(
                "logout successful!",
                true
        );
    }

    private String resolveToken (String token){
        String prefixo = "Bearer ";
        // está comparando se existe a palavra Bearer escrito no inicio do token.
        if (token == null || !token.regionMatches(true, 0, prefixo, 0, prefixo.length())) {
            throw new RuntimeException();
        }
        // começa a ler a string depois do bearer para poder
        // enviar o token puro para validação.
        token = token.substring(prefixo.length()).trim();

        // se estiver vazio, emite um erro.
        if (token.isEmpty()) {
            throw new  RuntimeException("Token is empty");
        }

        return token;
    }

}
