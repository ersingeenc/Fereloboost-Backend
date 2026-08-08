package com.fereloboost.api.identity.application.service;

import com.fereloboost.api.identity.application.command.LoginCommand;
import com.fereloboost.api.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthenticationResult login(LoginCommand command) {
        String normalizedEmail = command.email().strip().toLowerCase(Locale.ROOT);
        var user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        var accessToken = jwtTokenService.issue(user);
        String refreshToken = refreshTokenService.create(user, command.deviceName(), command.ipAddress());
        return new AuthenticationResult(user, accessToken.value(), accessToken.expiresAt(), refreshToken);
    }

    @Transactional
    public AuthenticationResult refresh(String refreshToken, String deviceName, String ipAddress) {
        var session = refreshTokenService.consume(refreshToken);
        var user = session.getUser();
        var accessToken = jwtTokenService.issue(user);
        String rotatedRefreshToken = refreshTokenService.create(user, deviceName, ipAddress);
        return new AuthenticationResult(user, accessToken.value(), accessToken.expiresAt(), rotatedRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeIfPresent(refreshToken);
    }
}
