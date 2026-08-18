package com.fereloboost.api.customer.api.response;

import com.fereloboost.api.identity.application.query.IdentityProfile;
import com.fereloboost.api.identity.domain.model.RoleCode;
import com.fereloboost.api.identity.domain.model.UserStatus;

import java.util.Set;
import java.util.UUID;

public record CurrentUserResponse(
        UUID id,
        String email,
        String displayName,
        String phone,
        UserStatus status,
        Set<RoleCode> roles
) {
    public static CurrentUserResponse from(IdentityProfile profile) {
        return new CurrentUserResponse(
                profile.id(), profile.email(), profile.displayName(), profile.phone(), profile.status(), profile.roles()
        );
    }
}
