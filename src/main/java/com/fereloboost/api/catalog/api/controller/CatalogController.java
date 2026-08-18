package com.fereloboost.api.catalog.api.controller;

import com.fereloboost.api.catalog.api.response.CatalogServiceResponse;
import com.fereloboost.api.catalog.api.response.GameResponse;
import com.fereloboost.api.catalog.application.service.CatalogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogQueryService catalogQueryService;

    @GetMapping("/games")
    public List<GameResponse> listGames() {
        return catalogQueryService.listGames();
    }

    @GetMapping("/games/{gameCode}/services")
    public List<CatalogServiceResponse> listServices(@PathVariable String gameCode) {
        return catalogQueryService.listServices(gameCode);
    }

    @GetMapping("/services/{serviceCode}")
    public CatalogServiceResponse getService(@PathVariable String serviceCode) {
        return catalogQueryService.getService(serviceCode);
    }
}
