package com.fereloboost.api.catalog.application.service;

import com.fereloboost.api.catalog.api.response.CatalogServiceResponse;
import com.fereloboost.api.catalog.api.response.GameResponse;
import com.fereloboost.api.catalog.api.response.ServiceAddonResponse;
import com.fereloboost.api.catalog.domain.model.CatalogService;
import com.fereloboost.api.catalog.infrastructure.persistence.CatalogServiceRepository;
import com.fereloboost.api.catalog.infrastructure.persistence.GameRepository;
import com.fereloboost.api.pricing.infrastructure.persistence.ServiceAddonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogQueryService {

    private final GameRepository gameRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final ServiceAddonRepository serviceAddonRepository;

    public List<GameResponse> listGames() {
        return gameRepository.findAllByActiveTrueOrderBySortOrderAsc().stream().map(GameResponse::from).toList();
    }

    public List<CatalogServiceResponse> listServices(String gameCode) {
        return catalogServiceRepository.findAllByGameCodeAndActiveTrueOrderBySortOrderAsc(normalize(gameCode))
                .stream().map(this::toResponse).toList();
    }

    public CatalogServiceResponse getService(String serviceCode) {
        return toResponse(findActiveService(serviceCode));
    }

    public CatalogService findActiveService(String serviceCode) {
        return catalogServiceRepository.findByCodeAndActiveTrue(normalize(serviceCode))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aktif hizmet bulunamadı."));
    }

    private CatalogServiceResponse toResponse(CatalogService service) {
        var addons = serviceAddonRepository.findAllByServiceIdAndActiveTrueOrderBySortOrderAsc(service.getId())
                .stream().map(ServiceAddonResponse::from).toList();
        return CatalogServiceResponse.from(service, addons);
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(java.util.Locale.ROOT);
    }
}
