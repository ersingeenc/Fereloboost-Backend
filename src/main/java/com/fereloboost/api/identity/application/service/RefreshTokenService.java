package com.fereloboost.api.identity.application.service;

import com.fereloboost.api.config.properties.JwtProperties;
import com.fereloboost.api.identity.domain.model.RefreshSession;
import com.fereloboost.api.identity.domain.model.User;
import com.fereloboost.api.identity.domain.repository.RefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshSessionRepository refreshSessionRepository;
    private final JwtProperties properties;

    @Transactional
    String create(User user, String deviceName, String ipAddress) {
        String token = generateToken();
        RefreshSession refreshSession = RefreshSession.create(
                user,
                hash(token),
                truncate(deviceName, 150),
                ipAddress == null ? null : hash(ipAddress),
                Instant.now().plus(properties.refreshTokenTtl())
        );
        refreshSessionRepository.save(refreshSession);
        return token;
    }

    @Transactional
    RefreshSession consume(String token) {
        RefreshSession refreshSession = refreshSessionRepository.findActiveByTokenHash(hash(token), Instant.now())
                .orElseThrow(InvalidRefreshTokenException::new);
        refreshSession.revoke();
        return refreshSession;
    }

    @Transactional
    void revokeIfPresent(String token) {
        refreshSessionRepository.findActiveByTokenHash(hash(token), Instant.now())
                .ifPresent(RefreshSession::revoke);
    }

    private String generateToken() {
        byte[] bytes = new byte[64];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algoritması bulunamadı.", exception);
        }
    }

    private String truncate(String value, int maximumLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.substring(0, Math.min(value.length(), maximumLength));
    }
}
