package com.fereloboost.api.pricing.infrastructure.persistence;

import com.fereloboost.api.pricing.domain.model.ServicePriceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServicePriceRuleRepository extends JpaRepository<ServicePriceRule, UUID> {
    Optional<ServicePriceRule> findByServiceIdAndFromRankCodeAndToRankCodeAndActiveTrue(UUID serviceId, String fromRankCode, String toRankCode);
    Optional<ServicePriceRule> findByServiceIdAndFromRankCodeAndToRankCode(UUID serviceId, String fromRankCode, String toRankCode);
}
