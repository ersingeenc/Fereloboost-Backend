package com.fereloboost.api.pricing.api.response;

import com.fereloboost.api.pricing.domain.model.ServicePriceRule;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceRuleResponse(UUID id, String serviceCode, String fromRankCode, String toRankCode,
                                BigDecimal basePriceAmount, String currency, boolean active) {
    public static PriceRuleResponse from(ServicePriceRule rule) {
        return new PriceRuleResponse(rule.getId(), rule.getService().getCode(), rule.getFromRankCode(),
                rule.getToRankCode(), rule.getBasePriceAmount(), rule.getCurrency(), rule.isActive());
    }
}
