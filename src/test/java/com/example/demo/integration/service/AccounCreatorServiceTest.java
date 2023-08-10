package com.example.demo.integration.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.AccountFinderService;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO
make parametrized test with enumsource Role

make class for AccountCreatorDtoService
- test
	- createAndGetDto

	- encodePassword
*/

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccounCreatorServiceTest {

	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	private final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> INVALID_USERNAMES = UsernameValidatorTest.INVALID_USERNAMES;
	
	private final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	private final List<String> INVALID_PASSWORDS = PasswordValidatorTest.INVALID_PASSWORDS;
	
	private final int MIN_LENGTH = Math.min(VALID_USERNAMES.size(), VALID_PASSWORDS.size());
	
	private final String validUsername1 = VALID_USERNAMES.get(0);
	
	private final String validPassword1 = VALID_PASSWORDS.get(0);
	private final String validPassword2 = VALID_PASSWORDS.get(1);
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithNullThrowsTest(final Role role) {
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(), role
			),
			"Creating account with uninitialized AccountCreationDto does not "
			+ "throw a ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(null, null), role
			),
			"Creating account with null parameters does not throw a "
			+ "ConstraintViolationException"
		);
		
		assertThrows(
			IllegalArgumentException.class,
			() -> accountCreatorService.create(null, role),
			"Creating account with null AccountCreationDto does not throw a "
			+ "IllegalArgumentException"
		);
		
		assertThrows(
			IllegalArgumentException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(validUsername1, validPassword1), null
			),
			"Creating account with null Role does not throw a "
			+ "IllegalArgumentException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(null, validPassword1), role
			),
			"Creating account with null username does not throw a "
			+ "ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(validUsername1, null), role
			),
			"Creating account with null password does not throw a "
			+ "ConstraintViolationException"
		);
	}
	
	@Test
	public void createWithInvalidUsernameThrowsTest() {
		for (final String username : INVALID_USERNAMES) {
			for (final String password : VALID_PASSWORDS) {
				assertThrows(
					ConstraintViolationException.class,
					() -> accountCreatorService.create(
						new AccountCreationDto(username, password), Role.USER
					),
					"Creating an account with invalid username '" + username
					+ "' does not throw a ConstraintViolationException"
				);
			}
		}
	}
	
	@Test
	public void createWithInvalidPasswordThrowsTest() {
		for (final String username : VALID_USERNAMES) {
			for (final String password : INVALID_PASSWORDS) {
				assertThrows(
					ConstraintViolationException.class,
					() -> accountCreatorService.create(
						new AccountCreationDto(username, password), Role.USER
					),
					"Creating an account with invalid password '" + password
					+ "' does not throw a ConstraintViolationException"
				);
			}
		}
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithValidUsernameAndPasswordAndRoleDoesNotThrowTest(final Role role) {
		for (final String username : VALID_USERNAMES) {
			for (final String password : VALID_PASSWORDS) {
				assertDoesNotThrow(
					() -> accountCreatorService.create(
						new AccountCreationDto(username, password), role
					),
					"Creating an account with valid username '" + username
					+ "', valid password '" + password + "' and valid role '"
					+ role.getName() + "' throws an exception"
				);
			}
		}
	}
	
	@CartesianTest
	public void createWithTakenUsernameAndWithoutTakenPasswordIsNotPresentTest(
			@CartesianTest.Enum Role role1,	@CartesianTest.Enum Role role2) {
		
		int looped = 0;
		for (int i = 2; i < MIN_LENGTH; i += 2) {
			final String commonUsername = VALID_USERNAMES.get(i / 2 - 1);
			final String password1 = VALID_PASSWORDS.get(i - 2);
			final String password2 = VALID_PASSWORDS.get(i - 1);
			
			assertOptionalAccountsWithParameters(
				commonUsername, password1, role1,
				commonUsername, password2, role2
			);
			
			++looped;
		}
		
		assertTrue(looped > 0, "no tests were run!");
	}
	
	@CartesianTest
	public void createWithoutTakenUsernameAndWithTakenPasswordIsPresentTest(
			@CartesianTest.Enum Role role1,	@CartesianTest.Enum Role role2) {
		
		int looped = 0;
		for (int i = 2; i < MIN_LENGTH; i += 2) {
			final String username1 = VALID_USERNAMES.get(i - 2);
			final String username2 = VALID_USERNAMES.get(i - 1);
			final String commonPassword = VALID_PASSWORDS.get(i / 2 - 1);
			
			assertOptionalAccountsWithParameters(
				username1, commonPassword, role1,
				username2, commonPassword, role2
			);
			
			++looped;
		}
		
		assertTrue(looped > 0, "no tests were run!");
	}
	
	@CartesianTest
	public void createWithoutTakenUsernameAndPasswordIsPresentTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2) {
		
		int looped = 0;
		for (int i = 2; i < MIN_LENGTH; i += 2) {
			final String username1 = VALID_USERNAMES.get(i - 2);
			final String username2 = VALID_USERNAMES.get(i - 1);
			final String password1 = VALID_PASSWORDS.get(i - 2);
			final String password2 = VALID_PASSWORDS.get(i - 1);
			
			assertOptionalAccountsWithParameters(
				username1, password1, role1,
				username2, password2, role2
			);
			
			++looped;
		}
		
		assertTrue(looped > 0, "no tests were run!");
	}
	
	private void assertOptionalAccountsWithParameters(
		String username1, String password1, Role role1,
		String username2, String password2, Role role2) {
		
		final String account1Info = 
			"Account with valid username " + username1 + ", valid "
			+ "password " + password1 + " and Role " + role1.getName();
		
		Optional<Account> opt1 = accountCreatorService.create(
			new AccountCreationDto(username1, password1), role1
		);
		
		//opt1.ifPresentOrElse(
		//	(account) -> System.out.println("YES 1: " + account1Info), 
		//	() -> System.out.println("NO 1: " + account1Info)
		//);
		
		assertTrue(
			opt1.isPresent(), 
			"First Optional is not present when creating an " + account1Info
		);
		
		final String account2Info = 
			"Account with valid username " + username2 + ", valid "
			+ "password " + password2 + " and Role " + role2.getName();

		Optional<Account> opt2 = accountCreatorService.create(
			new AccountCreationDto(username2, password2), role2
		);
		
		//opt2.ifPresentOrElse(
		//	(account) -> System.out.println("YES 2: " + account2Info), 
		//	() -> System.out.println("NO 2: " + account2Info)
		//);
		
		assertEquals(
			username1.equals(username2), opt2.isEmpty(),
			"Second Optional is " + (opt2.isEmpty() ? "not" : "")
			+ " present when creating an " + account2Info + ", after "
			+ "creating an " + account1Info
		);
	}
	
	@Test
	public void createWithTakenUsernameDoesNotChangeTheOrginalTest() {
		final String commonUsername = validUsername1;
		Optional<Account> opt = accountCreatorService.create(
			new AccountCreationDto(commonUsername, validPassword1), Role.USER
		);
		
		accountCreatorService.create(
			new AccountCreationDto(commonUsername, validPassword2), Role.ADMIN
		);
		
		assertEquals(
			opt.get(), accountFinderService.findByUsername(commonUsername),
			"After attempting to create an Account with taken username, "
			+ "the orignal created Account with than username is changed"
		);
	}
	
	@Test
	public void createReturnedOptionalHasTheCreatedUsernameTest() {
		final String username = validUsername1;
		Optional<Account> opt = accountCreatorService.create(
			new AccountCreationDto(username, validPassword1), Role.USER
		);
		
		assertEquals(username, opt.get().getUsername());
	}
	
	@Test
	public void createReturnedOptionalHasEncodedPasswordTest() {
		final String password = validPassword1;
		
		Optional<Account> opt = accountCreatorService.create(
			new AccountCreationDto(validUsername1, password), Role.USER
		);
		
		final String returnedPassword = opt.get().getPassword();
		
		assertNotEquals(
			password, returnedPassword,
			"The returned Optional Account has password " + password
			+ " saved in plain text"
		);
		
		assertTrue(
			accountCreatorService.getPasswordEncoder().matches(
				password, returnedPassword
			),
			"The returned Optional Accounts password " + password
			+ " is not encoded correctly"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createReturnedOptionalHasTheCreatedRoleTest(Role role) {
		Optional<Account> opt = accountCreatorService.create(
			new AccountCreationDto(validUsername1, validPassword1), role
		);
		
		assertTrue(
			opt.get().getRole() == role,
			"The returned Optional Account has role "
			+ opt.get().getRole().getName() + " when it is was created with "
			+ "role " + role.getName()
		);
	}
}