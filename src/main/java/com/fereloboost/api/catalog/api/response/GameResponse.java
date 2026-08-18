package com.fereloboost.api.catalog.api.response;

import com.fereloboost.api.catalog.domain.model.Game;

public record GameResponse(String code, String name, String slug, String description) {
    public static GameResponse from(Game game) {
        return new GameResponse(game.getCode(), game.getName(), game.getSlug(), game.getDescription());
    }
}
