package com.fereloboost.api.identity.application.service;

import com.fereloboost.api.config.properties.JwtProperties;
import com.fereloboost.api.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    IssuedAccessToken issue(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.accessTokenTtl());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .id(UUID.randomUUID().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream()
                        .map(role -> role.getCode().name())
                        .sorted()
                        .toList())
                .build();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
        return new IssuedAccessToken(token, expiresAt);
    }

    record IssuedAccessToken(String value, Instant expiresAt) {
    }
}
