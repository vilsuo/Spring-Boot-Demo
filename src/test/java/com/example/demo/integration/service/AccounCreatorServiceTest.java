package com.example.demo.integration.service;

import static com.example.demo.testhelpers.AccountCreationHelpers.accountCreationDtoPairStream;
import static com.example.demo.testhelpers.AccountCreationHelpers.accountCreationDtoStream;
import static com.example.demo.testhelpers.AccountCreationHelpers.accountInfo;
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
implement method 'assertOptionalAccountsWithParameters' better

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
	private final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	
	private final String validUsername = VALID_USERNAMES.get(0);
	
	private final String validPassword = VALID_PASSWORDS.get(0);
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithNullThrowsTest(final Role role) {
		/*
		final String validUsername = ;
		final String validPassword = ;
		*/
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(), role
			),
			"Creating account with uninitialized AccountCreationDto does not "
			+ "throw"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(null, null), role
			),
			"Creating account with null parameters does not throw"
		);
		
		assertThrows(
			IllegalArgumentException.class,
			() -> accountCreatorService.create(null, role),
			"Creating account with null AccountCreationDto does not throw"
		);
		
		assertThrows(
			IllegalArgumentException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(validUsername, validPassword), null
			),
			"Creating account with null Role does not throw"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(null, validPassword), role
			),
			"Creating account with null username does not throw"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountCreatorService.create(
				new AccountCreationDto(validUsername, null), role
			),
			"Creating account with null password does not throw"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithInvalidUsernameThrowsTest(final Role role) {
		accountCreationDtoStream(false, true).forEach(accountCreationDto -> {
			assertThrows(
				ConstraintViolationException.class,
				() -> accountCreatorService.create(
					accountCreationDto, role
				),
				"Creating an " + accountInfo(accountCreationDto, role)
				+ " does not throw"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithInvalidPasswordThrowsTest(final Role role) {
		accountCreationDtoStream(true, false).forEach(accountCreationDto -> {
			assertThrows(
				ConstraintViolationException.class,
				() -> accountCreatorService.create(
					accountCreationDto, role
				),
				"Creating an " + accountInfo(accountCreationDto, role)
				+ " does not throw"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createWithValidUsernameAndPasswordAndRoleDoesNotThrowTest(final Role role) {
		accountCreationDtoStream().forEach(accountCreationDto -> {
			assertDoesNotThrow(
				() -> accountCreatorService.create(
					accountCreationDto, role
				),
				"Creating " + accountInfo(accountCreationDto, role) + " throws"
			);
		});
	}
	
	@CartesianTest
	public void createWithTakenUsernameAndWithoutTakenPasswordIsNotPresentTest(
			@CartesianTest.Enum Role role1,	@CartesianTest.Enum Role role2) {
		
		accountCreationDtoPairStream(true, false).forEach(pair -> {
			assertOptionalAccountsWithParameters(
				pair.getFirst(), role1,
				pair.getSecond(), role2
			);
		});
	}
	
	@CartesianTest
	public void createWithoutTakenUsernameAndWithTakenPasswordIsPresentTest(
			@CartesianTest.Enum Role role1,	@CartesianTest.Enum Role role2) {
		
		accountCreationDtoPairStream(false, true).forEach(pair -> {
			assertOptionalAccountsWithParameters(
				pair.getFirst(), role1,
				pair.getSecond(), role2
			);
		});
	}
	
	@CartesianTest
	public void createWithoutTakenUsernameAndPasswordIsPresentTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2) {
		
		accountCreationDtoPairStream(false, false).forEach(pair -> {
			assertOptionalAccountsWithParameters(
				pair.getFirst(), role1,
				pair.getSecond(), role2
			);
		});
	}
	
	private void assertOptionalAccountsWithParameters(
		AccountCreationDto accountCreationDto1, Role role1,
		AccountCreationDto accountCreationDto2, Role role2) {
		
		Optional<Account> opt1 = accountCreatorService.create(
			accountCreationDto1, role1
		);
		
		assertTrue(
			opt1.isPresent(), 
			"First Optional is not present when creating an "
				+ accountInfo(accountCreationDto1, role1)
		);
		
		Optional<Account> opt2 = accountCreatorService.create(
			accountCreationDto2, role2
		);
		
		assertEquals(
			accountCreationDto1.getUsername().equals(accountCreationDto2.getUsername()),
			opt2.isEmpty(),
			"Second Optional is " + (opt2.isEmpty() ? "not" : "")
			+ " present when creating an "
			+ accountInfo(accountCreationDto2, role2) + ", after "
			+ "creating an " + accountInfo(accountCreationDto1, role1)
		);
	}
	
	@Test
	public void createWithTakenUsernameDoesNotChangeTheOrginalTest() {
		final Role roleFirst = Role.USER;
		final Role roleSecond = Role.ADMIN;
		accountCreationDtoPairStream(true, false).forEach(pair -> {
			final Account original = accountCreatorService.create(
				pair.getFirst(), roleFirst
			).get();
			
			accountCreatorService.create(pair.getSecond(), roleSecond);
			
			assertEquals(
				original,
				accountFinderService.findByUsername(original.getUsername()),
				"After attempting to create an Account with taken username, "
				+ "the original Account with than username is changed"
			);
		});
	}
	
	@Test
	public void createReturnedOptionalHasTheCreatedUsernameTest() {
		final Role role = Role.USER;
		accountCreationDtoStream().forEach(accountCreationDto -> {
			Optional<Account> opt = accountCreatorService.create(
				accountCreationDto, role
			);

			assertEquals(
				accountCreationDto.getUsername(), opt.get().getUsername()
			);
		});
	}
	
	@Test
	public void createReturnedOptionalHasEncodedPasswordTest() {
		final Role role = Role.USER;
		accountCreationDtoStream().forEach(accountCreationDto -> {
			Optional<Account> opt = accountCreatorService.create(
				accountCreationDto, role
			);

			final String rawPassword = accountCreationDto.getPassword();
			final String returnedPassword = opt.get().getPassword();

			assertNotEquals(
				rawPassword, returnedPassword,
				"The returned Optional Account has password '" + rawPassword
				+ "' saved in plain text"
			);

			assertTrue(
				accountCreatorService.getPasswordEncoder().matches(
					rawPassword, returnedPassword
				),
				"The returned Optional Accounts raw password '" + rawPassword
				+ "' is not encoded correctly: '" + returnedPassword + "'"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void createReturnedOptionalHasTheCreatedRoleTest(Role role) {
		accountCreationDtoStream().forEach(accountCreationDto -> {
			Optional<Account> opt = accountCreatorService.create(
				accountCreationDto, role
			);

			assertTrue(
				opt.get().getRole() == role,
				"The returned Optional Account has role "
				+ opt.get().getRole().getName()
				+ " when it is was created with role " + role.getName()
			);
		});
	}
}