package com.fereloboost.api.orders.domain.model;

public enum OrderStatus {
    DRAFT,
    AWAITING_PRICING,
    AWAITING_PAYMENT,
    PAID,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
