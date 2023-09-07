package com.example.demo.integration.service;

import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreateInfo;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.AccountFinderService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.uniqueAccountCreationDtoStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationDtoStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationDtoPairStream;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.springframework.security.crypto.password.PasswordEncoder;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccounCreatorServiceTest {

	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithNullThrowsTest(final Role role) {
		final AccountCreationDto first
			= validAndUniqueAccountCreationDtoStream()
				.findFirst()
				.get();
		
		final String validUsername = first.getUsername();
		final String validPassword = first.getPassword();
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(new AccountCreationDto(), role),
			"Creating an Account from default constructed AccountCreationDto "
			+ "does not throw"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(null, null), role
			),
			"Creating an Account from null constructed AccountCreationDto does "
			+ "not throw"
		);
		
		assertThrows(
			IllegalArgumentException.class,
			() -> accountCreatorService.create(null, role),
			"Creating an Account from null AccountCreationDto does not throw"
		);
		
		assertThrows(
			IllegalArgumentException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(validUsername, validPassword), null
			),
			"Creating an Account from AccountCreationDto with null Role does "
			+ "not throw"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService
				.create(new AccountCreationDto(null, validPassword), role),
			"Creating an Account from AccountCreationDto with null username "
			+ "does not throw"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService
				.create(new AccountCreationDto(validUsername, null), role),
			"Creating an Account from AccountCreationDto with null password "
			+ "does not throw"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithInvalidUsernameThrowsTest(final Role role) {
		uniqueAccountCreationDtoStream(false, true)
			.forEach(accountCreationDto -> {
				assertThrows(
					ConstraintViolationException.class,
					() -> accountCreatorService
						.create(accountCreationDto, role),
					"Creating an Account from "
					+ accountCreateInfo(accountCreationDto, role)
					+ " does not throw"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithInvalidPasswordThrowsTest(final Role role) {
		uniqueAccountCreationDtoStream(true, false)
			.forEach(accountCreationDto -> {
				assertThrows(
					ConstraintViolationException.class,
					() -> accountCreatorService
						.create(accountCreationDto, role),
					"Creating an Account from "
					+ accountCreateInfo(accountCreationDto, role)
					+ " does not throw"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithValidUsernameAndPasswordAndRoleDoesNotThrowTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				assertDoesNotThrow(
					() -> accountCreatorService
						.create(accountCreationDto, role),
					"Creating an Account from "
					+ accountCreateInfo(accountCreationDto, role) + " throws"
				);
			});
	}
	
	@CartesianTest
	public void createWithTakenUsernameAndWithoutTakenPasswordIsNotPresentTest(
			@CartesianTest.Enum Role role1,	@CartesianTest.Enum Role role2) {
		
		validAndUniqueAccountCreationDtoPairStream(true, false)
			.forEach(pair -> {
				createAndAssertOptionalAccountPairsPresenceWithParameters(
					pair.getFirst(), role1, pair.getSecond(), role2
				);
			});
	}
	
	@CartesianTest
	public void createWithoutTakenUsernameAndWithTakenPasswordIsPresentTest(
			@CartesianTest.Enum Role role1,	@CartesianTest.Enum Role role2) {
		
		validAndUniqueAccountCreationDtoPairStream(false, true)
			.forEach(pair -> {
				createAndAssertOptionalAccountPairsPresenceWithParameters(
					pair.getFirst(), role1, pair.getSecond(), role2
				);
			});
	}
	
	@CartesianTest
	public void createWithoutTakenUsernameAndWithoutTakenPasswordIsPresentTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2) {
		
		validAndUniqueAccountCreationDtoPairStream(false, false)
			.forEach(pair -> {
				createAndAssertOptionalAccountPairsPresenceWithParameters(
					pair.getFirst(), role1, pair.getSecond(), role2
				);
			});
	}
	
	private void createAndAssertOptionalAccountPairsPresenceWithParameters(
			final AccountCreationDto accountCreationDto1, final Role role1,
			final AccountCreationDto accountCreationDto2, final Role role2) {
		
		final Optional<Account> opt1 = accountCreatorService
			.create(accountCreationDto1, role1);
		
		assertTrue(
			opt1.isPresent(), 
			"First Optional is not present when creating an Account from "
			+ accountCreateInfo(accountCreationDto1, role1)
		);
		
		final Optional<Account> opt2 = accountCreatorService
			.create(accountCreationDto2, role2);
		
		assertEquals(
			accountCreationDto1.getUsername()
				.equals(accountCreationDto2.getUsername()),
			opt2.isEmpty(),
			"Second Optional is " + (opt2.isEmpty() ? "not" : "")
			+ " present when creating an Account from "
			+ accountCreateInfo(accountCreationDto2, role2) + ", after "
			+ "creating an Account from "
			+ accountCreateInfo(accountCreationDto1, role1)
		);
	}
	
	@CartesianTest
	public void createWithTakenUsernameDoesNotChangeTheOrginalTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2) {
		
		validAndUniqueAccountCreationDtoPairStream(true, false)
			.forEach(pair -> {
				final Account original = accountCreatorService
					.create(pair.getFirst(), role1)
					.get();

				accountCreatorService.create(pair.getSecond(), role2);

				assertEquals(
					original,
					accountFinderService.findByUsername(original.getUsername()),
					"After attempting to create an Account with taken "
					+ "username '" + original.getUsername() + "', the orginal "
					+ "Account with that username is changed"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createReturnedOptionalHasTheCreatedUsernameTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role)
					.get();

				assertEquals(
					accountCreationDto.getUsername(), account.getUsername(),
					"Account was created with the username '"
					+ accountCreationDto.getUsername() + "' but the returned "
					+ "Account has username '" + account.getUsername() + "'"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createReturnedOptionalHasEncodedPasswordTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role)
					.get();

				final String rawPassword = accountCreationDto.getPassword();
				final String returnedPassword = account.getPassword();

				assertNotEquals(
					rawPassword, returnedPassword,
					"The returned Optional Account has password '" + rawPassword
					+ "' saved in plain text"
				);

				assertTrue(
					passwordEncoder
						.matches(rawPassword, returnedPassword),
					"The returned Optional Accounts raw password '"
					+ rawPassword + "' is not encoded correctly: '"
					+ returnedPassword + "'"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createReturnedOptionalHasTheCreatedRoleTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role)
					.get();

				assertTrue(
					account.getRole() == role,
					"The returned Optional Account has role "
					+ account.getRole() + " when it is was created "
					+ "with role " + role
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createdReturnedAccountsAreNotAnonymousTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role)
					.get();

				assertFalse(
					Role.isAnonymous(account),
					accountCreateInfo(accountCreationDto, role)
					+ " is not supposed to be anonymous"
				);
			});
	}
}