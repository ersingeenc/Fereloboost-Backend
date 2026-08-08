package com.fereloboost.api.identity.api.response;

import java.time.Instant;

public record AccessTokenResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt
) {
    public static AccessTokenResponse bearer(String accessToken, Instant expiresAt) {
        return new AccessTokenResponse(accessToken, "Bearer", expiresAt);
    }
}
