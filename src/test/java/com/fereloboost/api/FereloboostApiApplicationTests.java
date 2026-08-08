package com.fereloboost.api;

import com.fereloboost.api.identity.application.command.RegisterCustomerCommand;
import com.fereloboost.api.identity.application.command.LoginCommand;
import com.fereloboost.api.identity.application.service.AuthenticationService;
import com.fereloboost.api.identity.application.service.IdentityService;
import com.fereloboost.api.identity.application.service.InvalidRefreshTokenException;
import com.fereloboost.api.identity.domain.model.RoleCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FereloboostApiApplicationTests {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private JwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

	@Test
	void registersCustomerWithHashedPasswordAndCustomerRole() {
		var user = identityService.registerCustomer(new RegisterCustomerCommand(
				"customer@example.com",
				"Test Customer",
				null,
				"SecurePassword123!"
		));

		Assertions.assertEquals("customer@example.com", user.getEmail());
		Assertions.assertTrue(passwordEncoder.matches("SecurePassword123!", user.getPasswordHash()));
		Assertions.assertTrue(user.getRoles().stream().anyMatch(role -> role.getCode() == RoleCode.CUSTOMER));
	}

	@Test
	void logsInAndRotatesRefreshToken() {
		var user = identityService.registerCustomer(new RegisterCustomerCommand(
				"login@example.com",
				"Login Customer",
				null,
				"SecurePassword123!"
		));
		var firstLogin = authenticationService.login(new LoginCommand(
				"login@example.com", "SecurePassword123!", "JUnit", "127.0.0.1"
		));

		Assertions.assertEquals(user.getId().toString(), jwtDecoder.decode(firstLogin.accessToken()).getSubject());
		Assertions.assertNotNull(firstLogin.refreshToken());

		var refreshedLogin = authenticationService.refresh(firstLogin.refreshToken(), "JUnit", "127.0.0.1");
		Assertions.assertNotEquals(firstLogin.refreshToken(), refreshedLogin.refreshToken());
		Assertions.assertThrows(InvalidRefreshTokenException.class,
				() -> authenticationService.refresh(firstLogin.refreshToken(), "JUnit", "127.0.0.1"));
	}

}
