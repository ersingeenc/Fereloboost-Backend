package com.fereloboost.api.identity.domain.repository;

import com.fereloboost.api.identity.domain.model.Role;
import com.fereloboost.api.identity.domain.model.RoleCode;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByCode(RoleCode code);
}
