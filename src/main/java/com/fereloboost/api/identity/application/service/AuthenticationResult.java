package com.fereloboost.api.identity.application.service;

import com.fereloboost.api.identity.domain.model.User;

import java.time.Instant;

public record AuthenticationResult(
        User user,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken
) {
}
