package com.fereloboost.api.pricing.api.controller;

import com.fereloboost.api.pricing.api.request.PriceQuoteRequest;
import com.fereloboost.api.pricing.api.response.PriceQuoteResponse;
import com.fereloboost.api.pricing.application.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/quotes")
    public PriceQuoteResponse quote(@Valid @RequestBody PriceQuoteRequest request) {
        return pricingService.quote(request);
    }
}
