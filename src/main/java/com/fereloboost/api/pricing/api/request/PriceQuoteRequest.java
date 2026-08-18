package com.fereloboost.api.pricing.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PriceQuoteRequest(
        @NotBlank @Size(max = 80) String serviceCode,
        @NotBlank @Size(max = 80) String fromRankCode,
        @NotBlank @Size(max = 80) String toRankCode,
        @NotNull List<@NotBlank @Size(max = 80) String> addonCodes
) {
}
