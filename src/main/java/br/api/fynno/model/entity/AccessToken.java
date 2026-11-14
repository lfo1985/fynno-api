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
    private Integer id;

    @Setter
    @Column(name="token_hash")
    private String tokenHash;

    @Getter
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Getter
    @Setter
    @Column(name = "expires_at")
    private LocalDateTime expires_at;

    @Getter
    @Setter
    @Column(name = "revoked_at")
    private LocalDateTime revoked_at;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void beforeInsert() {
        this.created_at = LocalDateTime.now();
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
