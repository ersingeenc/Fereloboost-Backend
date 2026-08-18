package com.fereloboost.api.catalog.infrastructure.persistence;

import com.fereloboost.api.catalog.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findAllByActiveTrueOrderBySortOrderAsc();
}
