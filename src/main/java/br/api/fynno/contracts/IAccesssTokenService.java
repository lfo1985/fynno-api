package br.api.fynno.contracts;

public interface IAccesssTokenService {
    public String generateToken ();
    public String sha256Encode (String token);
    public boolean validateToken (String token, String tokenHash);
}
