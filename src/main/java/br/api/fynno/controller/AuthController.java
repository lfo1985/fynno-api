package br.api.fynno.controller;

import br.api.fynno.contracts.IAccesssTokenService;
import br.api.fynno.contracts.IAuthService;
import br.api.fynno.request.AuthRequest;
import br.api.fynno.response.auth.AuthResponse;
import br.api.fynno.response.shared.OkResponse;
import br.api.fynno.service.AccessTokenService;
import br.api.fynno.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
