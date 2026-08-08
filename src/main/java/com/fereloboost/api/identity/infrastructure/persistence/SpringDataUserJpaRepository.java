package com.fereloboost.api.identity.infrastructure.persistence;

import com.fereloboost.api.identity.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataUserJpaRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
