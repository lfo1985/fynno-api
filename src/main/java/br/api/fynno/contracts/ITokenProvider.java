package br.api.fynno.contracts;

import br.api.fynno.model.entity.AccessToken;
import br.api.fynno.model.entity.User;
import br.api.fynno.response.auth.CheckUserLoginResponse;

public interface ITokenProvider {
    void revokedToken(String tokenHash);
    String registryToken(User user);
    CheckUserLoginResponse checkToken (String token);
    void setTokenSecret(String secretToken);
    void setMinutes(Integer minutes);
}
