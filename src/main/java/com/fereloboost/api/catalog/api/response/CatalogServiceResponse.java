package com.fereloboost.api.catalog.api.response;

import com.fereloboost.api.catalog.domain.model.CatalogService;
import com.fereloboost.api.catalog.domain.model.PricingModel;

import java.util.List;
import java.util.Map;

public record CatalogServiceResponse(
        String code,
        String name,
        String slug,
        String description,
        PricingModel pricingModel,
        Map<String, Object> configurationSchema,
        List<ServiceAddonResponse> addons
) {
    public static CatalogServiceResponse from(CatalogService service, List<ServiceAddonResponse> addons) {
        return new CatalogServiceResponse(
                service.getCode(), service.getName(), service.getSlug(), service.getDescription(),
                service.getPricingModel(), service.getConfigurationSchema(), addons
        );
    }
}
