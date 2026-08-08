package com.fereloboost.api.identity.infrastructure.persistence;

import com.fereloboost.api.identity.domain.model.Role;
import com.fereloboost.api.identity.domain.model.RoleCode;
import com.fereloboost.api.identity.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class JpaRoleRepository implements RoleRepository {

    private final SpringDataRoleJpaRepository springDataRepository;

    @Override
    public Optional<Role> findByCode(RoleCode code) {
        return springDataRepository.findByCode(code);
    }
}
