package com.fereloboost.api.orders.api.response;

import com.fereloboost.api.orders.domain.model.CustomerOrder;
import com.fereloboost.api.orders.domain.model.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record OrderDraftResponse(
        UUID id,
        String orderNumber,
        String gameCode,
        String serviceCode,
        String serviceName,
        OrderStatus status,
        Map<String, Object> configuration,
        List<OrderAddonResponse> addons,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrderDraftResponse from(CustomerOrder order, List<OrderAddonResponse> addons) {
        return new OrderDraftResponse(order.getId(), order.getOrderNumber(), order.getGameCode(), order.getServiceCode(),
                order.getService().getName(), order.getStatus(), order.getConfiguration(), addons,
                order.getCreatedAt(), order.getUpdatedAt());
    }
}
