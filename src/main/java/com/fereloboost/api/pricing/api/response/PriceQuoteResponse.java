package com.fereloboost.api.pricing.api.response;

import java.math.BigDecimal;
import java.util.List;

public record PriceQuoteResponse(
        String serviceCode,
        String fromRankCode,
        String toRankCode,
        BigDecimal basePriceAmount,
        BigDecimal addonsPriceAmount,
        BigDecimal totalPriceAmount,
        String currency,
        List<String> appliedAddonCodes
) {
}
