package com.fereloboost.api.admin.api.controller;

import com.fereloboost.api.pricing.api.request.UpsertPriceRuleRequest;
import com.fereloboost.api.pricing.api.response.PriceRuleResponse;
import com.fereloboost.api.pricing.application.service.PricingAdministrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/pricing")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS_MANAGER')")
public class PricingAdministrationController {

    private final PricingAdministrationService pricingAdministrationService;

    @PostMapping("/rank-path-rules")
    public PriceRuleResponse upsertRankPathRule(@Valid @RequestBody UpsertPriceRuleRequest request) {
        return pricingAdministrationService.upsertRankPathRule(request);
    }
}
