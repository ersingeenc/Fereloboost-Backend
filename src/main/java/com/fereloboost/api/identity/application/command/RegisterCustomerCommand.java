package com.fereloboost.api.identity.application.command;

public record RegisterCustomerCommand(
        String email,
        String displayName,
        String phone,
        String rawPassword
) {
}
