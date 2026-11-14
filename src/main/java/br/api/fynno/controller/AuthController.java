package br.api.fynno.controller;

import br.api.fynno.contracts.IAuthService;
import br.api.fynno.request.AuthRequest;
import br.api.fynno.response.auth.AuthResponse;
import br.api.fynno.response.auth.CheckUserLoginResponse;
import br.api.fynno.response.shared.OkResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController extends Controller {

    private final IAuthService authService;

    public AuthController(
            IAuthService authService
    ) {
        super();
        this.authService = authService;
    }

    @PostMapping
    public OkResponse login(@RequestBody @Valid AuthRequest  authRequest) {
        var userDataLogin = this.authService.doLogin(authRequest.email(), authRequest.password());
        // Se tudo der certo, retorna o login.
        return new OkResponse("login successful.", true, new AuthResponse(userDataLogin));
    }

    @PostMapping("logout")
    public OkResponse logout() {
        // revogar o mesmo, ou seja, logout (inválidar) o token.
        this.authService.doTokenRevoke(this.getToken());
        return new OkResponse("logout successful!", true);
    }

    @GetMapping("check")
    public CheckUserLoginResponse check () {
        return this.authService.doTokenCheck(this.getToken());
    }
}
