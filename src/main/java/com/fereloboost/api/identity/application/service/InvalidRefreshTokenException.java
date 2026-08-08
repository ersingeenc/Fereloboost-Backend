package com.fereloboost.api.identity.application.service;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super("Oturum yenileme anahtarı geçersiz veya süresi dolmuş.");
    }
}
