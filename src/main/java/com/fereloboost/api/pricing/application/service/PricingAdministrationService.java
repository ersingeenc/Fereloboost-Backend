package com.fereloboost.api.pricing.application.service;

import com.fereloboost.api.catalog.application.service.CatalogQueryService;
import com.fereloboost.api.catalog.domain.model.PricingModel;
import com.fereloboost.api.pricing.api.request.UpsertPriceRuleRequest;
import com.fereloboost.api.pricing.api.response.PriceRuleResponse;
import com.fereloboost.api.pricing.domain.model.ServicePriceRule;
import com.fereloboost.api.pricing.infrastructure.persistence.ServicePriceRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PricingAdministrationService {

    private final CatalogQueryService catalogQueryService;
    private final ServicePriceRuleRepository servicePriceRuleRepository;

    @Transactional
    public PriceRuleResponse upsertRankPathRule(UpsertPriceRuleRequest request) {
        var service = catalogQueryService.findActiveService(request.serviceCode());
        if (service.getPricingModel() != PricingModel.RANK_PATH) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Fiyat geçiş kuralı yalnızca RANK_PATH hizmetler için tanımlanabilir.");
        }
        String fromRank = normalize(request.fromRankCode());
        String toRank = normalize(request.toRankCode());
        if (fromRank.equals(toRank)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Başlangıç ve hedef rank aynı olamaz.");
        }
        String currency = normalize(request.currency());
        var rule = servicePriceRuleRepository.findByServiceIdAndFromRankCodeAndToRankCode(service.getId(), fromRank, toRank)
                .map(existing -> {
                    existing.updatePrice(request.basePriceAmount(), currency);
                    return existing;
                })
                .orElseGet(() -> ServicePriceRule.create(service, fromRank, toRank, request.basePriceAmount(), currency));
        return PriceRuleResponse.from(servicePriceRuleRepository.save(rule));
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
