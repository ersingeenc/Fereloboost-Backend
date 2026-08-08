package com.fereloboost.api.identity.domain.repository;

import com.fereloboost.api.identity.domain.model.RefreshSession;

import java.time.Instant;
import java.util.Optional;

public interface RefreshSessionRepository {

    RefreshSession save(RefreshSession refreshSession);

    Optional<RefreshSession> findActiveByTokenHash(String tokenHash, Instant currentTime);
}
