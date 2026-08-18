package com.fereloboost.api.orders.domain.model;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "customer_order_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerOrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private User actor;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 40)
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 40)
    private OrderStatus newStatus;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    private CustomerOrderEvent(CustomerOrder order, User actor, String eventType, OrderStatus previousStatus,
                               OrderStatus newStatus, Map<String, Object> metadata) {
        this.order = order;
        this.actor = actor;
        this.eventType = eventType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.metadata = Map.copyOf(metadata);
    }

    public static CustomerOrderEvent draftCreated(CustomerOrder order, User actor) {
        return new CustomerOrderEvent(order, actor, "DRAFT_CREATED", null, OrderStatus.DRAFT, Map.of());
    }

    public static CustomerOrderEvent draftUpdated(CustomerOrder order, User actor) {
        return new CustomerOrderEvent(order, actor, "DRAFT_UPDATED", OrderStatus.DRAFT, OrderStatus.DRAFT, Map.of());
    }

    @PrePersist
    void prePersist() {
        occurredAt = Instant.now();
    }
}
