package br.api.fynno.repository;

import br.api.fynno.model.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {

    @Query("SELECT at FROM AccessToken AS at INNER JOIN FETCH at.user WHERE at.tokenHash = :tokenHash")
    AccessToken findByTokenHash (@Param("tokenHash") String  tokenHash);

}
