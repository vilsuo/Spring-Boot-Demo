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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
	
	private final String validUsername1 = VALID_USERNAMES.get(0);
	private final String validUsername2 = VALID_USERNAMES.get(1);
	
	private final String validPassword1 = VALID_PASSWORDS.get(0);
	private final String validPassword2 = VALID_PASSWORDS.get(1);
	
	@Test
	public void createWithNullThrowsTest() {
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(), Role.USER
			),
			"Creating account with uninitialized AccountCreationDto does not "
			+ "throw a ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(null, null), Role.USER
			),
			"Creating account with null parameters does not throw a "
			+ "ConstraintViolationException"
		);
		
		assertThrows(
			IllegalArgumentException.class,
			() -> accountCreatorService.create(null, Role.USER),
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
				new AccountCreationDto(null, validPassword1), Role.USER
			),
			"Creating account with null username does not throw a "
			+ "ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(validUsername1, null), Role.USER
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
	
	@Test
	public void createWithValidUsernameAndPasswordAndRoleDoesNotThrowTest() {
		for (final String username : VALID_USERNAMES) {
			for (final String password : VALID_PASSWORDS) {
				assertDoesNotThrow(
					() -> accountCreatorService.create(
						new AccountCreationDto(username, password), Role.USER
					),
					"Creating an account with valid username '" + username
					+ "', valid password '" + password + "' and valid role '"
					+ Role.USER.getName() + "' throws an exception"
				);
			}
		}
	}
	
	@Test
	public void createWithoutTakenUsernameIsPresentTest() {
		Optional<Account> opt1 = accountCreatorService.create(
			new AccountCreationDto(validUsername1, validPassword1), Role.USER
		);
		
		assertTrue(
			opt1.isPresent(), 
			"First Optional is not present when creating an Account with valid "
			+ "parameters in an empty database"
		);
		
		Optional<Account> opt2 = accountCreatorService.create(
			new AccountCreationDto(validUsername2, validPassword1), Role.USER
		);
		
		assertTrue(
			opt2.isPresent(), 
			"Second Optional is not present when creating an Account with "
			+ "valid parameters in an empty database"
		);
	}
	
	@Test
	public void createWithTakenUsernameIsNotPresentTest() {
		final String commonUsername = validUsername1;
		accountCreatorService.create(
			new AccountCreationDto(commonUsername, validPassword1), Role.USER
		);
		
		Optional<Account> opt2 = accountCreatorService.create(
			new AccountCreationDto(commonUsername, validPassword2), Role.USER
		);
		assertTrue(
			opt2.isEmpty(),
			"Optional is present when creating an Account with an username "
			+ "that is already taken"
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
	
	public void createReturnedOptionalHasEncodedPasswordTest() {
		final String password = validPassword1;
		
		Optional<Account> opt = accountCreatorService.create(
			new AccountCreationDto(validUsername1, password), Role.USER
		);
		
		final String returnedPassword = opt.get().getPassword();
		final String encodedPassword 
			= accountCreatorService.encodePassword(password);
		
		assertEquals(
			returnedPassword, encodedPassword,
			"The returned Optional Account has password " + returnedPassword
			+ " but the correct encoded password would be " + encodedPassword
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
	
	/*
	private void assertAccountIsCreatedFromAccountCreationDtoAndRole(
		final Account account, final AccountCreationDto accountCreationDto, 
		final Role role) {
		
		assertEquals(
			accountCreationDto.getUsername(), account.getUsername(), 
			"The Account is given a different username than it was created with"
		);
		
		assertEquals(
			role, account.getRole(),
			"The Account is given different Role than it was created with"
		);
		assertEquals(
			accountCreatorService.encodePassword(
				accountCreationDto.getPassword()
			),
			account.getPassword(),
			"The Accounts password is not encoded correctly"
		);
	}
	*/
}