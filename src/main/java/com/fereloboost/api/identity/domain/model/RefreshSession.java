package com.fereloboost.api.identity.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "refresh_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshSession {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "device_name", length = 150)
    private String deviceName;

    @Column(name = "ip_hash", length = 128)
    private String ipHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private RefreshSession(User user, String tokenHash, String deviceName, String ipHash, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.tokenHash = tokenHash;
        this.deviceName = deviceName;
        this.ipHash = ipHash;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    public static RefreshSession create(User user, String tokenHash, String deviceName, String ipHash, Instant expiresAt) {
        return new RefreshSession(user, tokenHash, deviceName, ipHash, expiresAt);
    }

    public void revoke() {
        if (revokedAt == null) {
            revokedAt = Instant.now();
        }
    }
}
