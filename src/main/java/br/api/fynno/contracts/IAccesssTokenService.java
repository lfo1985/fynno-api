package br.api.fynno.contracts;

public interface IAccesssTokenService {
    String generateToken ();
    String sha256Encode (String token);
    boolean validateToken (String token, String tokenHash);
}
