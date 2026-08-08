package com.fereloboost.api.identity.infrastructure.persistence;

import com.fereloboost.api.identity.domain.model.RefreshSession;
import com.fereloboost.api.identity.domain.repository.RefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class JpaRefreshSessionRepository implements RefreshSessionRepository {

    private final SpringDataRefreshSessionJpaRepository springDataRepository;

    @Override
    public RefreshSession save(RefreshSession refreshSession) {
        return springDataRepository.save(refreshSession);
    }

    @Override
    public Optional<RefreshSession> findActiveByTokenHash(String tokenHash, Instant currentTime) {
        return springDataRepository.findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(tokenHash, currentTime);
    }
}
