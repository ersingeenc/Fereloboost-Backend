package com.fereloboost.api.orders.api.controller;

import com.fereloboost.api.orders.api.request.SaveOrderDraftRequest;
import com.fereloboost.api.orders.api.response.OrderDraftResponse;
import com.fereloboost.api.orders.api.response.OrderSummaryResponse;
import com.fereloboost.api.orders.application.service.OrderDraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final OrderDraftService orderDraftService;

    @PostMapping("/drafts")
    public OrderDraftResponse createDraft(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody SaveOrderDraftRequest request) {
        return orderDraftService.createDraft(customerId(jwt), request);
    }

    @PutMapping("/drafts/{orderId}")
    public OrderDraftResponse updateDraft(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID orderId,
                                          @Valid @RequestBody SaveOrderDraftRequest request) {
        return orderDraftService.updateDraft(customerId(jwt), orderId, request);
    }

    @GetMapping
    public List<OrderSummaryResponse> listOrders(@AuthenticationPrincipal Jwt jwt) {
        return orderDraftService.listOrders(customerId(jwt));
    }

    @GetMapping("/{orderId}")
    public OrderDraftResponse getOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID orderId) {
        return orderDraftService.getOrder(customerId(jwt), orderId);
    }

    private UUID customerId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
