package com.fereloboost.api.pricing.infrastructure.persistence;

import com.fereloboost.api.pricing.domain.model.ServiceAddon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceAddonRepository extends JpaRepository<ServiceAddon, UUID> {
    List<ServiceAddon> findAllByServiceIdAndActiveTrueOrderBySortOrderAsc(UUID serviceId);
    List<ServiceAddon> findAllByServiceIdAndCodeInAndActiveTrue(UUID serviceId, List<String> codes);
}
