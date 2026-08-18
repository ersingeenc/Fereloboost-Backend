package com.fereloboost.api.catalog.api.response;

import com.fereloboost.api.pricing.domain.model.ServiceAddon;

import java.math.BigDecimal;

public record ServiceAddonResponse(String code, String name, String description, BigDecimal priceAmount, String currency) {
    public static ServiceAddonResponse from(ServiceAddon addon) {
        return new ServiceAddonResponse(addon.getCode(), addon.getName(), addon.getDescription(), addon.getPriceAmount(), addon.getCurrency());
    }
}
