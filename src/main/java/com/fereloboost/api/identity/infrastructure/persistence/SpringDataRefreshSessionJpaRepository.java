package com.fereloboost.api.identity.infrastructure.persistence;

import com.fereloboost.api.identity.domain.model.RefreshSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface SpringDataRefreshSessionJpaRepository extends JpaRepository<RefreshSession, UUID> {

    Optional<RefreshSession> findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(String tokenHash, Instant currentTime);
}
