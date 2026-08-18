package com.fereloboost.api.orders.infrastructure.persistence;

import com.fereloboost.api.orders.domain.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {
    List<CustomerOrder> findAllByCustomerIdOrderByUpdatedAtDesc(UUID customerId);
    Optional<CustomerOrder> findByIdAndCustomerId(UUID id, UUID customerId);
}
