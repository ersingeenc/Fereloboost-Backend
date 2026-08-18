package com.fereloboost.api.orders.api.response;

import com.fereloboost.api.orders.domain.model.CustomerOrderAddon;

public record OrderAddonResponse(String code, String name) {
    public static OrderAddonResponse from(CustomerOrderAddon addon) {
        return new OrderAddonResponse(addon.getAddonCode(), addon.getAddonName());
    }
}
