
package com.example.demo.integration.service;

import com.codepoetics.protonpack.StreamUtils;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.error.validation.ResourceNotFoundException;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.AccountFinderService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreateInfo;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.uniqueAccountCreationDtoStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationDtoStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationDtoPairStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationDtoPairStream;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccountFinderServiceTest {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	private final Long initiallyNotTakenId = 1l;
	
	@Test
	public void findingAccountByNullIdThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountFinderService.findById(null),
			"Searching Account by null id does not throw"
		);
	}
	
	@Test
	public void findingAccountByIdThatDoesNotExistThrowsTest() {
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountFinderService.findById(initiallyNotTakenId),
			"Searching Account by id that has not been taken in a empty "
			+ "database does not throw"
		);	
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsIdDoesNotThrowTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role).get();

				assertDoesNotThrow(
					() -> accountFinderService.findById(account.getId()),
					"When finding the created " + account + " by id, the "
					+ "method throws"
				);
			});
	}
	
	@CartesianTest
	public void findingAccountsByDifferentIdsFindUnEqualAccountsTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		validAndUniqueAccountCreationDtoPairStream(false, setSamePasswordToPair)
			.forEach(pair -> {
				final Account account1 = accountCreatorService
					.create(pair.getFirst(), role1).get();
				
				final Account account2 = accountCreatorService
					.create(pair.getSecond(), role2).get();
				
				final Account accountFound1 = accountFinderService
					.findById(account1.getId());
				
				final Account accountFound2 = accountFinderService
					.findById(account2.getId());
				
				assertNotEquals(
					accountFound1, accountFound2, 
					"Finding Accounts by the ids of Accounts " + account1
					+ " and " + account2 + " results in finding equal Accounts "
					+ accountFound1 + " and " + accountFound2
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsIdFindsTheCreatedAccountTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account accountCreated = accountCreatorService
					.create(accountCreationDto, role).get();

				final Account accountFound = accountFinderService
					.findById(accountCreated.getId());

				assertEquals(
					accountCreated, accountFound,
					"The created " + accountCreated + " does not equal to the "
					+ "found " + accountFound
				);
			});
	}
	
	@Test
	public void findingAccountByNullUsernameThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountFinderService.findByUsername(null),
			"Finding Account by null username does not throw"
		);
	}
	
	// with or without pair?
	@Test
	public void findingAccountByUsernameThatDoesNotExistThrows() {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final String username = accountCreationDto.getUsername();
				assertThrows(
					ResourceNotFoundException.class,
					() -> accountFinderService.findByUsername(username),
					"Finding Account by username '" + username + "' does not "
					+ "throw when no Accounts are created with that username"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsUsernameDoesNotThrowTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role).get();

				final String username = account.getUsername();
				assertDoesNotThrow(
					() -> accountFinderService.findByUsername(username),
					"The method throws when finding " + account
					+ " by username '" + username + "'"
				);
			});
	}
	
	@CartesianTest
	public void findingAccountsByDifferentUsernamesFindUnEqualAccountsTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		validAndUniqueAccountCreationDtoPairStream(false, setSamePasswordToPair)
			.forEach(pair -> {
				final Account account1 = accountCreatorService
					.create(pair.getFirst(), role1).get();
				
				final Account account2 = accountCreatorService
					.create(pair.getSecond(), role2).get();
				
				final Account accountFound1 = accountFinderService
					.findByUsername(account1.getUsername());
				
				final Account accountFound2 = accountFinderService
					.findByUsername(account2.getUsername());
				
				assertNotEquals(
					accountFound1, accountFound2, 
					"Finding Accounts by the usernames of "
					+ account1 + " and " + account2
					+ " results in finding equal Accounts "
					+ accountFound1 + " and " + accountFound2
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsUsernameFindsTheCreatedAccountTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account accountCreated = accountCreatorService
					.create(accountCreationDto, role).get();

				final Account accountFound = accountFinderService
					.findByUsername(accountCreated.getUsername());

				assertEquals(
					accountCreated, accountFound,
					"The created " + accountCreated + " does not equal to the "
					+ "found " + accountFound
				);
			});
	}
	
	@Test
	public void findingIfNullUsernameExistsThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountFinderService.existsByUsername(null),
			"Searching if Account exists by null username does not throw"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void ifThereNoAccountsCreatedWithGivenUsernameThenThatUsernameDoesNotExistTest(final Role role) {
		validAndUniqueAccountCreationDtoPairStream(false, true)
			.forEach(pair -> {
				accountCreatorService.create(pair.getFirst(), role);
				
				final String notTakenUsername = pair.getSecond().getUsername();
				assertFalse(
					accountFinderService.existsByUsername(notTakenUsername),
					"Account with the username '" + notTakenUsername
					+ "' exists even when no Accounts are created with that "
					+ "username"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void ifAccountIsCreatedWithGivenUsernameThenThatUsernameExistsTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role).get();

				assertTrue(
					accountFinderService.existsByUsername(account.getUsername()),
					"Username of the created " + account + " does not exist"
				);
			});
	}
	
	@Test
	public void accountListIsEmptyIfNoAccountsAreCreatedTest() {
		assertTrue(
			accountFinderService.list().isEmpty(),
			"Account list is not empty before creating any Accounts"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void creatingAccountsWithUsernamesThatAreNotTakenIncrementsTheAccountListSizeTest(final Role role) {
		StreamUtils
			.zipWithIndex(validAndUniqueAccountCreationDtoStream())
			.forEach(indexed -> {
				final Account account = accountCreatorService
					.create(indexed.getValue(), role).get();
				
				final Long created = indexed.getIndex() + 1;
				assertEquals(
					created, accountFinderService.list().size(),
					"After creating " + account + " the list size was not "
					+ "incremented"
				);
			});
	}
	
	@CartesianTest
	public void creatingAccountsWithUsernamesThatAreAlreadyTakenDoesNotIncrementTheAccountListSizeTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		StreamUtils
			.zipWithIndex(validAndUniqueAccountCreationDtoPairStream(true, setSamePasswordToPair))
			.forEach(indexed -> {
				final Account account1 = accountCreatorService
					.create(indexed.getValue().getFirst(), role1).get();

				final Long created = indexed.getIndex() + 1;
				assertEquals(
					created, accountFinderService.list().size(),
					"After creating the first " + account1 + " the list size "
					+ "was not incremented"
				);
				
				final AccountCreationDto accountCreationDto2
					= indexed.getValue().getSecond();
				accountCreatorService.create(accountCreationDto2, role2);

				assertEquals(
					created, accountFinderService.list().size(),
					"The list size should not change after creating the "
					+ "second Account with the same name. The first Account "
					+ "was " + account1 + " and the second Account was created "
					+ "from " + accountCreateInfo(accountCreationDto2, role2)
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void afterCreatingAccountThatAccountCanBeFoundFromTheAccountsListTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = accountCreatorService
					.create(accountCreationDto, role).get();

				assertTrue(
					accountFinderService.list().contains(account),
					"After creating " + account + ", it can not be found in "
					+ "the list"
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void ifAccountIsNotCreatedThenThatAccountCanNotBeFoundFromTheAccountListTest(final Role role) {
		validAndUniqueAccountCreationDtoStream()
			.forEach(accountCreationDto -> {
				final Account account = new Account(
					accountCreationDto.getUsername(),
					accountCreatorService.encodePassword(
						accountCreationDto.getPassword()
					),
					role,
					new HashSet<>(), new HashSet<>(), new HashSet<>()
				);

				assertFalse(
					accountFinderService.list().contains(account),
					account + " that has not been saved to the database can be "
					+ "found in the list"
				);
			});
	}
}