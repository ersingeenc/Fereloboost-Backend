package com.fereloboost.api.identity.application.command;

public record LoginCommand(String email, String rawPassword, String deviceName, String ipAddress) {
}
