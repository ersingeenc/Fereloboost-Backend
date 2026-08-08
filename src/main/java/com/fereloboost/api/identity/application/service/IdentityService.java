package com.fereloboost.api.identity.application.service;

import com.fereloboost.api.identity.application.command.RegisterCustomerCommand;
import com.fereloboost.api.identity.domain.model.RoleCode;
import com.fereloboost.api.identity.domain.model.User;
import com.fereloboost.api.identity.domain.repository.RoleRepository;
import com.fereloboost.api.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerCustomer(RegisterCustomerCommand command) {
        String normalizedEmail = command.email().strip().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException();
        }

        var customerRole = roleRepository.findByCode(RoleCode.CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException(RoleCode.CUSTOMER));

        User user = User.registerCustomer(
                normalizedEmail,
                command.displayName().strip(),
                command.phone() == null || command.phone().isBlank() ? null : command.phone().strip(),
                passwordEncoder.encode(command.rawPassword()),
                customerRole
        );

        return userRepository.save(user);
    }
}
