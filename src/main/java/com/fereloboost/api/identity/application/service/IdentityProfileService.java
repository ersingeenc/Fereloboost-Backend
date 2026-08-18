package com.fereloboost.api.identity.application.service;

import com.fereloboost.api.identity.application.query.IdentityProfile;
import com.fereloboost.api.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentityProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public IdentityProfile getProfile(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        var roles = user.getRoles().stream()
                .map(role -> role.getCode())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return new IdentityProfile(
                user.getId(), user.getEmail(), user.getDisplayName(), user.getPhone(), user.getStatus(), roles
        );
    }
}
