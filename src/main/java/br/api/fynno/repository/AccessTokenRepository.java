package br.api.fynno.repository;

import br.api.fynno.model.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {

    AccessToken findByTokenHash (String tokenHash);

}
