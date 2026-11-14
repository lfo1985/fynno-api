package br.api.fynno.contracts;

import br.api.fynno.model.entity.AccessToken;
import br.api.fynno.model.entity.User;
import br.api.fynno.response.auth.CheckUserLoginResponse;
import br.api.fynno.response.auth.UserLoginDataResponse;

public interface IAuthService {
    UserLoginDataResponse doLogin (String email, String password);
    void doTokenRevoke (String token);
    CheckUserLoginResponse doTokenCheck (String token);
}
