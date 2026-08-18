package com.fereloboost.api.customer.api.controller;

import com.fereloboost.api.customer.api.response.CurrentUserResponse;
import com.fereloboost.api.identity.application.service.IdentityProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class CurrentUserController {

    private final IdentityProfileService identityProfileService;

    @GetMapping
    public CurrentUserResponse getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return CurrentUserResponse.from(identityProfileService.getProfile(UUID.fromString(jwt.getSubject())));
    }
}
