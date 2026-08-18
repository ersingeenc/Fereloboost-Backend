package com.fereloboost.api.orders.domain.model;

import com.fereloboost.api.catalog.domain.model.CatalogService;
import com.fereloboost.api.identity.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@Table(name = "customer_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true, length = 32, updatable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, updatable = false)
    private CatalogService service;

    @Column(name = "service_code", nullable = false, length = 80, updatable = false)
    private String serviceCode;

    @Column(name = "game_code", nullable = false, length = 40, updatable = false)
    private String gameCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> configuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private CustomerOrder(User customer, CatalogService service, Map<String, Object> configuration) {
        this.orderNumber = "FBR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        this.customer = customer;
        this.service = service;
        this.serviceCode = service.getCode();
        this.gameCode = service.getGame().getCode();
        this.configuration = Map.copyOf(configuration);
        this.status = OrderStatus.DRAFT;
    }

    public static CustomerOrder createDraft(User customer, CatalogService service, Map<String, Object> configuration) {
        return new CustomerOrder(customer, service, configuration);
    }

    public void updateDraft(Map<String, Object> configuration) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Yalnızca taslak sipariş güncellenebilir.");
        }
        this.configuration = Map.copyOf(configuration);
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
