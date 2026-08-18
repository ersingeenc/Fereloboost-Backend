package com.fereloboost.api;

import com.fereloboost.api.identity.application.command.RegisterCustomerCommand;
import com.fereloboost.api.identity.application.command.LoginCommand;
import com.fereloboost.api.identity.application.service.AuthenticationService;
import com.fereloboost.api.identity.application.service.IdentityService;
import com.fereloboost.api.identity.application.service.InvalidRefreshTokenException;
import com.fereloboost.api.identity.domain.model.RoleCode;
import com.fereloboost.api.pricing.api.request.PriceQuoteRequest;
import com.fereloboost.api.pricing.api.request.UpsertPriceRuleRequest;
import com.fereloboost.api.pricing.application.service.PricingAdministrationService;
import com.fereloboost.api.pricing.application.service.PricingService;
import com.fereloboost.api.orders.api.request.SaveOrderDraftRequest;
import com.fereloboost.api.orders.application.service.OrderDraftService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class FereloboostApiApplicationTests {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private JwtDecoder jwtDecoder;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PricingAdministrationService pricingAdministrationService;

	@Autowired
	private PricingService pricingService;

	@Autowired
	private OrderDraftService orderDraftService;

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

	@Test
	void returnsCurrentUserOnlyForValidAccessToken() throws Exception {
		identityService.registerCustomer(new RegisterCustomerCommand(
				"profile@example.com",
				"Profile Customer",
				"05550000000",
				"SecurePassword123!"
		));
		var authentication = authenticationService.login(new LoginCommand(
				"profile@example.com", "SecurePassword123!", "JUnit", "127.0.0.1"
		));

		mockMvc.perform(get("/api/v1/me")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + authentication.accessToken()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("profile@example.com"))
				.andExpect(jsonPath("$.roles[0]").value("CUSTOMER"));

		mockMvc.perform(get("/api/v1/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void exposesSeededCatalogWithoutRequiringLogin() throws Exception {
		mockMvc.perform(get("/api/v1/catalog/games"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].code").value("LEAGUE_OF_LEGENDS"))
				.andExpect(jsonPath("$[2].code").value("TEAMFIGHT_TACTICS"));

		mockMvc.perform(get("/api/v1/catalog/games/league_of_legends/services"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].code").value("LOL_ELO_BOOST"))
				.andExpect(jsonPath("$[0].addons[2].code").value("OFFLINE_MODE"));
	}

	@Test
	void rejectsQuoteWhenPriceRuleIsNotConfiguredInsteadOfAllowingUnauthorizedAccess() throws Exception {
		mockMvc.perform(post("/api/v1/pricing/quotes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"serviceCode":"LOL_ELO_BOOST","fromRankCode":"IRON_IV","toRankCode":"BRONZE_IV","addonCodes":[]}
						"""))
				.andExpect(status().isNotFound());
	}

	@Test
	void calculatesRankPathQuoteAfterAdminAddsPriceRule() {
		pricingAdministrationService.upsertRankPathRule(new UpsertPriceRuleRequest(
				"LOL_ELO_BOOST", "IRON_IV", "BRONZE_IV", new java.math.BigDecimal("500.00"), "try"
		));

		var quote = pricingService.quote(new PriceQuoteRequest(
				"LOL_ELO_BOOST", "IRON_IV", "BRONZE_IV", java.util.List.of("OFFLINE_MODE")
		));

		Assertions.assertEquals(new java.math.BigDecimal("500.00"), quote.basePriceAmount());
		Assertions.assertEquals(new java.math.BigDecimal("50.00"), quote.addonsPriceAmount());
		Assertions.assertEquals(new java.math.BigDecimal("550.00"), quote.totalPriceAmount());
		Assertions.assertEquals("TRY", quote.currency());
	}

	@Test
	void customerCanCreateAndUpdateOwnOrderDraftWithoutStoringCredentials() {
		var customer = identityService.registerCustomer(new RegisterCustomerCommand(
				"order@example.com", "Order Customer", null, "SecurePassword123!"
		));
		var initialConfiguration = java.util.Map.<String, Object>of(
				"currentRank", "IRON_IV", "currentLp", 25, "targetRank", "BRONZE_IV",
				"queueType", "SOLO", "role", "MID", "server", "TR"
		);

		var draft = orderDraftService.createDraft(customer.getId(), new SaveOrderDraftRequest(
				"LOL_ELO_BOOST", initialConfiguration, java.util.List.of("OFFLINE_MODE")
		));
		var updatedConfiguration = new java.util.LinkedHashMap<>(initialConfiguration);
		updatedConfiguration.put("currentLp", 50);
		var updated = orderDraftService.updateDraft(customer.getId(), draft.id(), new SaveOrderDraftRequest(
				"LOL_ELO_BOOST", updatedConfiguration, java.util.List.of("LIVE_STREAM")
		));

		Assertions.assertEquals("DRAFT", updated.status().name());
		Assertions.assertEquals(50, updated.configuration().get("currentLp"));
		Assertions.assertEquals("LIVE_STREAM", updated.addons().getFirst().code());
		Assertions.assertFalse(updated.configuration().containsKey("password"));
		Assertions.assertEquals(1, orderDraftService.listOrders(customer.getId()).size());
	}

}
