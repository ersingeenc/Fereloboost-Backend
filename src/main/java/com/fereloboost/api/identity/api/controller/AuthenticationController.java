package com.fereloboost.api.identity.api.controller;

import com.fereloboost.api.identity.api.request.RegisterCustomerRequest;
import com.fereloboost.api.identity.api.request.LoginRequest;
import com.fereloboost.api.identity.api.response.AccessTokenResponse;
import com.fereloboost.api.identity.api.response.RegisteredUserResponse;
import com.fereloboost.api.identity.application.command.RegisterCustomerCommand;
import com.fereloboost.api.identity.application.command.LoginCommand;
import com.fereloboost.api.identity.application.service.AuthenticationResult;
import com.fereloboost.api.identity.application.service.AuthenticationService;
import com.fereloboost.api.identity.application.service.IdentityService;
import com.fereloboost.api.config.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IdentityService identityService;
    private final AuthenticationService authenticationService;
    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisteredUserResponse register(@Valid @RequestBody RegisterCustomerRequest request) {
        return RegisteredUserResponse.from(identityService.registerCustomer(
                new RegisterCustomerCommand(request.email(), request.displayName(), request.phone(), request.password())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        AuthenticationResult authentication = authenticationService.login(new LoginCommand(
                request.email(), request.password(), deviceName(httpRequest), clientIpAddress(httpRequest)
        ));
        return responseWithRefreshCookie(authentication);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            HttpServletRequest httpRequest
    ) {
        String refreshToken = refreshToken(httpRequest);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthenticationResult authentication = authenticationService.refresh(
                refreshToken, deviceName(httpRequest), clientIpAddress(httpRequest)
        );
        return responseWithRefreshCookie(authentication);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        String refreshToken = refreshToken(httpRequest);
        if (refreshToken != null && !refreshToken.isBlank()) {
            authenticationService.logout(refreshToken);
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(null, 0).toString())
                .build();
    }

    private ResponseEntity<AccessTokenResponse> responseWithRefreshCookie(AuthenticationResult authentication) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(authentication.refreshToken(), jwtProperties.refreshTokenTtl().toSeconds()).toString())
                .body(AccessTokenResponse.bearer(authentication.accessToken(), authentication.accessTokenExpiresAt()));
    }

    private ResponseCookie refreshCookie(String value, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(jwtProperties.refreshCookieName(), value == null ? "" : value)
                .httpOnly(true)
                .secure(jwtProperties.refreshCookieSecure())
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(maxAgeSeconds);
        return builder.build();
    }

    private String deviceName(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    private String clientIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private String refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (jwtProperties.refreshCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
