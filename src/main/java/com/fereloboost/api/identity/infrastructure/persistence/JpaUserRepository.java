package com.fereloboost.api.identity.infrastructure.persistence;

import com.fereloboost.api.identity.domain.model.User;
import com.fereloboost.api.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class JpaUserRepository implements UserRepository {

    private final SpringDataUserJpaRepository springDataRepository;

    @Override
    public User save(User user) {
        return springDataRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataRepository.findById(id);
    }
}
