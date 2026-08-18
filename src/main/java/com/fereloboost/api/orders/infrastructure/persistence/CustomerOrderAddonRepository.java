package com.fereloboost.api.orders.infrastructure.persistence;

import com.fereloboost.api.orders.domain.model.CustomerOrderAddon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerOrderAddonRepository extends JpaRepository<CustomerOrderAddon, UUID> {
    List<CustomerOrderAddon> findAllByOrderIdOrderByCreatedAtAsc(UUID orderId);
    void deleteAllByOrderId(UUID orderId);
}
