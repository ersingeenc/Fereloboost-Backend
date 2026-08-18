package com.fereloboost.api.identity.application.service;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Kullanıcı bulunamadı.");
    }
}
