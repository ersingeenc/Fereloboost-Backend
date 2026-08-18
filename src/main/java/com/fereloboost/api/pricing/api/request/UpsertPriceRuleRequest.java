package com.fereloboost.api.pricing.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpsertPriceRuleRequest(
        @NotBlank @Size(max = 80) String serviceCode,
        @NotBlank @Size(max = 80) String fromRankCode,
        @NotBlank @Size(max = 80) String toRankCode,
        @NotNull @DecimalMin("0.00") BigDecimal basePriceAmount,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency
) {
}
