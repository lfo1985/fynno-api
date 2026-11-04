package br.api.fynno.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_token")
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    Integer id;

    @Getter
    @Setter
    @Column(name = "user_id")
    Integer user_id;

    @Getter
    @Setter
    @Column(name="token_hash")
    String token_hash;

    @Getter
    @Column(name = "created_at")
    LocalDateTime created_at;

    @Getter
    @Setter
    @Column(name = "expires_at")
    LocalDateTime expires_at;

    @Getter
    @Setter
    @Column(name = "revoked_at")
    LocalDateTime revoked_at;

    @PrePersist
    public void beforeInsert() {
        this.created_at = LocalDateTime.now();
    }
}
