package com.fereloboost.api.identity.api.response;

import com.fereloboost.api.identity.domain.model.User;
import com.fereloboost.api.identity.domain.model.UserStatus;

import java.util.UUID;

public record RegisteredUserResponse(
        UUID id,
        String email,
        String displayName,
        UserStatus status
) {
    public static RegisteredUserResponse from(User user) {
        return new RegisteredUserResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getStatus());
    }
}
