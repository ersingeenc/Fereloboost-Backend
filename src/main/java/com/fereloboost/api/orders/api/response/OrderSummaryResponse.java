package com.fereloboost.api.orders.api.response;

import com.fereloboost.api.orders.domain.model.CustomerOrder;
import com.fereloboost.api.orders.domain.model.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderSummaryResponse(UUID id, String orderNumber, String gameCode, String serviceCode,
                                   String serviceName, OrderStatus status, Instant updatedAt) {
    public static OrderSummaryResponse from(CustomerOrder order) {
        return new OrderSummaryResponse(order.getId(), order.getOrderNumber(), order.getGameCode(), order.getServiceCode(),
                order.getService().getName(), order.getStatus(), order.getUpdatedAt());
    }
}
