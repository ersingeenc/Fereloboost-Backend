package com.fereloboost.api.identity.application.service;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
        super("Bu e-posta adresi zaten kayıtlı.");
    }
}
