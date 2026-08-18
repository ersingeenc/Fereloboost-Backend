package com.fereloboost.api.orders.domain.model;

import com.fereloboost.api.pricing.domain.model.ServiceAddon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "customer_order_addons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerOrderAddon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @Column(name = "addon_code", nullable = false, length = 80)
    private String addonCode;

    @Column(name = "addon_name", nullable = false, length = 120)
    private String addonName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private CustomerOrderAddon(CustomerOrder order, ServiceAddon addon) {
        this.order = order;
        this.addonCode = addon.getCode();
        this.addonName = addon.getName();
    }

    public static CustomerOrderAddon snapshot(CustomerOrder order, ServiceAddon addon) {
        return new CustomerOrderAddon(order, addon);
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
