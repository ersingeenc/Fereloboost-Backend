package com.fereloboost.api.identity.infrastructure.persistence;

import com.fereloboost.api.identity.domain.model.Role;
import com.fereloboost.api.identity.domain.model.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataRoleJpaRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByCode(RoleCode code);
}
