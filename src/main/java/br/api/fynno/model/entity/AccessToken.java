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

    @Column(name = "user_id")
    Integer user_id;

    @Setter
    @Column(name="token_hash")
    String tokenHash;

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

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public LocalDateTime getExpiresAt() {
        return expires_at;
    }

    public void setExpiresAt(LocalDateTime expires_at) {
        this.expires_at = expires_at;
    }

    public LocalDateTime getRevokedAt() {
        return revoked_at;
    }

    public void setRevokedAt(LocalDateTime revoked_at) { this.revoked_at = revoked_at; }
}
