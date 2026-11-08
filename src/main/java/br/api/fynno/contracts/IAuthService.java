package br.api.fynno.contracts;

import br.api.fynno.model.entity.User;
import br.api.fynno.response.auth.UserLoginDataResponse;

public interface IAuthService {
    public UserLoginDataResponse doLogin (String email, String password, String tokenHash);
    public void RevokedToken(String token);
}
