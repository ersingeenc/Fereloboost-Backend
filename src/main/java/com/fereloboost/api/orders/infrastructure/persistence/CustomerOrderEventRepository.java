package com.fereloboost.api.orders.infrastructure.persistence;

import com.fereloboost.api.orders.domain.model.CustomerOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerOrderEventRepository extends JpaRepository<CustomerOrderEvent, UUID> {
}
