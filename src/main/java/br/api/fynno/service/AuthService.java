package br.api.fynno.service;

import br.api.fynno.contracts.IAuthService;
import br.api.fynno.exception.AuthException;
import br.api.fynno.helper.HashPassword;
import br.api.fynno.model.entity.AccessToken;
import br.api.fynno.model.entity.User;
import br.api.fynno.repository.AccessTokenRepository;
import br.api.fynno.repository.UserRepository;
import br.api.fynno.response.auth.UserLoginDataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final Integer minutes;

    public AuthService(
            UserRepository userRepository,
            AccessTokenRepository accessTokenRepository,
            @Value("${fynno.auth.token-expires-time}") Integer minutes
    ) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.minutes = minutes;
    }

    public UserLoginDataResponse doLogin (String email, String password, String tokenHash) {
        try {
            User user = checkUser(email, password);
            this.registryToken(user, tokenHash);
            return new UserLoginDataResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        } catch (AuthException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void RevokedToken(String tokenHash) {
        AccessToken accessToken = this
                .accessTokenRepository
                .findByTokenHash(tokenHash);

        // Validar se o token existe.
        if(accessToken == null) {
            throw new AuthException("User not logged.");
        }

        // se o token já foi revogado, emite exceção.
        if(accessToken.getRevokedAt() != null) {
            throw new AuthException("Token is already revoked.");
        }

        // Esta validação consiste em certificar se o now() (data de hoje)
        // é menor que a data de expiração. o usAfter significa "é depois",
        // ou seja, é depois dessa data?
        if(LocalDateTime.now().isAfter(accessToken.getExpiresAt())) {
            throw new AuthException("Token is expired.");
        }

        accessToken.setRevokedAt(LocalDateTime.now());
        this.accessTokenRepository.save(accessToken);
    }

    private User checkUser (String email, String password) {
        if(email == null || password == null) {
            throw new AuthException("Invalid e-mail or password");
        }

        User user = getUserByEmail(email);
        boolean verifyPassword = HashPassword.check(password, user.getPasswordHash());

        if(!user.getEmail().equals(email) || !verifyPassword) {
            throw new AuthException("E-mail or password incorrect.");
        }

        return user;
    }

    private void registryToken(User user, String tokenHash) {
        AccessToken accessToken = new AccessToken();
        accessToken.setUserId(user.getId());
        accessToken.setTokenHash(tokenHash);
        LocalDateTime expiresTime = LocalDateTime.now().plusMinutes(this.minutes);
        accessToken.setExpiresAt(expiresTime);

        this.accessTokenRepository.save(accessToken);
    }

    private User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
