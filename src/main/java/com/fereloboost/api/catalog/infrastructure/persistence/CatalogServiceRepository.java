package com.fereloboost.api.catalog.infrastructure.persistence;

import com.fereloboost.api.catalog.domain.model.CatalogService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogServiceRepository extends JpaRepository<CatalogService, UUID> {
    List<CatalogService> findAllByGameCodeAndActiveTrueOrderBySortOrderAsc(String gameCode);
    Optional<CatalogService> findByCodeAndActiveTrue(String code);
}
