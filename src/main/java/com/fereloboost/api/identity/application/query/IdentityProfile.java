package com.fereloboost.api.identity.application.query;

import com.fereloboost.api.identity.domain.model.RoleCode;
import com.fereloboost.api.identity.domain.model.UserStatus;

import java.util.Set;
import java.util.UUID;

public record IdentityProfile(
        UUID id,
        String email,
        String displayName,
        String phone,
        UserStatus status,
        Set<RoleCode> roles
) {
}
