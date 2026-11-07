package br.api.fynno.contracts;

import br.api.fynno.response.auth.UserLoginDataResponse;

public interface IAuthService {
    public UserLoginDataResponse doLogin (String email, String password, String tokenHash);
}
