package com.fereloboost.api.orders.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public record SaveOrderDraftRequest(
        @NotBlank @Size(max = 80) String serviceCode,
        @NotNull @Size(max = 12) Map<String, Object> configuration,
        @NotNull @Size(max = 20) List<@NotBlank @Size(max = 80) String> addonCodes
) {
}
