package com.fereloboost.api.identity.domain.repository;

import com.fereloboost.api.identity.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);
}
