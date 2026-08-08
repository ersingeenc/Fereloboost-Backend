package com.fereloboost.api.identity.application.service;

import com.fereloboost.api.identity.domain.model.RoleCode;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(RoleCode roleCode) {
        super("Sistem rolü bulunamadı: " + roleCode);
    }
}
