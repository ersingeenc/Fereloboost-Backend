package com.fereloboost.api.identity.application.service;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("E-posta veya parola hatalı.");
    }
}
