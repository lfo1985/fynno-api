package br.api.fynno.service;

import br.api.fynno.contracts.IAuthService;
import br.api.fynno.contracts.ITokenProvider;
import br.api.fynno.exception.AuthException;
import br.api.fynno.exception.TokenProviderException;
import br.api.fynno.helper.HashPassword;
import br.api.fynno.model.entity.User;
import br.api.fynno.repository.UserRepository;
import br.api.fynno.response.auth.CheckUserLoginResponse;
import br.api.fynno.response.auth.UserLoginDataResponse;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final ITokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            ITokenProvider tokenProvider,
            @Value("${fynno.auth.token-expires-time}") Integer minutes,
            @Value("${fynno.auth.token-secret}") String tokenSecret
    ) {
        this.tokenProvider = tokenProvider;
        this.tokenProvider.setTokenSecret(tokenSecret);
        this.tokenProvider.setMinutes(minutes);
        this.userRepository = userRepository;
    }

    public UserLoginDataResponse doLogin (String email, String password) {
        try {
            User user = _checkUser(email, password);
            // Gera e registra o token do usuário.
            String accessToken = this.tokenProvider.registryToken(user);
            // Retorna para o usuário os dados de login.
            return new UserLoginDataResponse(user.getId(), user.getName(), user.getEmail(), accessToken);
        } catch (TokenProviderException e) {
            throw new AuthException(e.getMessage());
        }
    }

    public void doTokenRevoke (String token) {
        try {
            this.tokenProvider.revokedToken(token);
        } catch (TokenProviderException e) {
            throw new AuthException(e.getMessage());
        }
    }

    public CheckUserLoginResponse doTokenCheck (String token) {
        try {
            return this.tokenProvider.checkToken(token);
        } catch (TokenProviderException e) {
            throw new AuthException(e.getMessage());
        }
    }

    private User _checkUser (
            @Nullable  String email,
            @Nullable String password
    ) {
        if(email == null || password == null) {
            throw new AuthException("Invalid e-mail or password");
        }

        User user = this.userRepository.findByEmail(email);

        if(user == null) {
            throw new AuthException("Invalid e-mail or password");
        }

        boolean verifyPassword = HashPassword.check(password, user.getPasswordHash());

        if(!user.getEmail().equals(email) || !verifyPassword) {
            throw new AuthException("E-mail or password incorrect.");
        }

        return user;
    }
}
