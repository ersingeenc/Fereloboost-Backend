package com.fereloboost.api.pricing.application.service;

import com.fereloboost.api.catalog.application.service.CatalogQueryService;
import com.fereloboost.api.catalog.domain.model.PricingModel;
import com.fereloboost.api.pricing.api.request.PriceQuoteRequest;
import com.fereloboost.api.pricing.api.response.PriceQuoteResponse;
import com.fereloboost.api.pricing.infrastructure.persistence.ServiceAddonRepository;
import com.fereloboost.api.pricing.infrastructure.persistence.ServicePriceRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingService {

    private final CatalogQueryService catalogQueryService;
    private final ServicePriceRuleRepository servicePriceRuleRepository;
    private final ServiceAddonRepository serviceAddonRepository;

    public PriceQuoteResponse quote(PriceQuoteRequest request) {
        var service = catalogQueryService.findActiveService(request.serviceCode());
        if (service.getPricingModel() != PricingModel.RANK_PATH) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Bu hizmet için otomatik rank fiyatlandırması henüz uygulanamaz.");
        }

        String fromRank = normalize(request.fromRankCode());
        String toRank = normalize(request.toRankCode());
        var priceRule = servicePriceRuleRepository
                .findByServiceIdAndFromRankCodeAndToRankCodeAndActiveTrue(service.getId(), fromRank, toRank)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bu rank geçişi için henüz aktif bir fiyat kuralı tanımlanmadı."));

        List<String> addonCodes = request.addonCodes().stream().map(this::normalize).distinct().toList();
        var addons = serviceAddonRepository.findAllByServiceIdAndCodeInAndActiveTrue(service.getId(), addonCodes);
        if (addons.size() != addonCodes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seçilen ek hizmetlerden biri geçersiz.");
        }

        BigDecimal addonsTotal = addons.stream().map(addon -> addon.getPriceAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = priceRule.getBasePriceAmount().add(addonsTotal);
        return new PriceQuoteResponse(service.getCode(), fromRank, toRank, priceRule.getBasePriceAmount(), addonsTotal,
                total, priceRule.getCurrency(), addonCodes);
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
