
package com.example.demo.integration.service;

import com.codepoetics.protonpack.StreamUtils;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.error.validation.ResourceNotFoundException;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.AccountFinderService;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO
- move helper functions to a helper class
	- no more username/password lists in this class
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccountFinderServiceTest {
	
	private final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	
	private final Long initiallyNotTakenId = 1l;
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
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
			"Searching Account by id that is not found from the database "
			+ "does not throw"
		);	
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsIdDoesNotThrowTest(final Role role) {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			Account account = accountCreatorService.create(
				accountCreationDto, role
			).get();
			
			assertDoesNotThrow(
				() -> accountFinderService.findById(account.getId()),
				"When finding the created "
				+ accountInfo(accountCreationDto, role)
				+ " by id the method throws"
			);
		});
	}
	
	@CartesianTest
	public void findingAccountsByDifferentIdsFindUnEqualAccountsTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2) {
		
		final AccountCreationDto accountCreationDto1
			= accountCreationDtosWithUniqueUsernamesStream().findFirst().get();
		
		final Account account1 = accountCreatorService.create(
			accountCreationDto1, role1
		).get();
		
		final Account accountFound1 = accountFinderService.findById(
			account1.getId()
		);
		
		accountCreationDtosWithUniqueUsernamesStream()
			.skip(1) // skip the first one
			.forEach(accountCreationDto2 -> {
				Account account2 = accountCreatorService.create(
					accountCreationDto2, role2
				).get();

				Account accountFound2 = accountFinderService.findById(
					account2.getId()
				);

				assertNotEquals(
					accountFound1, accountFound2, 
					"Finding Accounts by the ids of Accounts created from "
					+ accountInfo(accountCreationDto1, role1) + " and "
					+ accountInfo(accountCreationDto2, role2)
					+ " results in finding equal Accounts "
					+ accountInfo(account1) + " and " + accountInfo(account2)
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsIdFindsTheCreatedAccountTest(final Role role) {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			Account account = accountCreatorService.create(
				accountCreationDto, role
			).get();
			
			Account accountFound = accountFinderService.findById(
				account.getId()
			);

			assertEquals(
				account, accountFound,
				"The first created " + accountInfo(account)
				+ " differs from the found one " + accountInfo(accountFound)
			);
		});
	}
	
	@Test
	public void findingAccountByUsernameThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountFinderService.findByUsername(null),
			"Finding Account by null username does not throw"
		);
	}
	
	@Test
	public void findingAccountByUsernameThatDoesNotExistThrows() {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			final String username = accountCreationDto.getUsername();
			assertThrows(
				ResourceNotFoundException.class,
				() -> accountFinderService.findByUsername(username),
				"Finding Account by username '" + username + "' that is not "
				+ "created does not throw"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsUsernameDoesNotThrowTest(final Role role) {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			accountCreatorService.create(accountCreationDto, role);
			
			assertDoesNotThrow(
				() -> accountFinderService.findByUsername(
					accountCreationDto.getUsername()
				),
				"When finding " + accountInfo(accountCreationDto, role)
				+ "by username, the method throws"
			);
		});
	}
	
	@CartesianTest
	public void findingAccountsByDifferentUsernamesFindUnEqualAccountsTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2) {
		
		final AccountCreationDto accountCreationDto1
			= accountCreationDtosWithUniqueUsernamesStream().findFirst().get();
		
		final Account account1 = accountCreatorService.create(
			accountCreationDto1, role1
		).get();
		
		final Account accountFound1 = accountFinderService.findByUsername(
			accountCreationDto1.getUsername()
		);
		
		accountCreationDtosWithUniqueUsernamesStream()
			.skip(1) // skip the first one
			.forEach(accountCreationDto2 -> {
				Account account2 = accountCreatorService.create(
					accountCreationDto2, role2
				).get();

				Account accountFound2 = accountFinderService.findByUsername(
					accountCreationDto2.getUsername()
				);

				assertNotEquals(
					accountFound1, accountFound2, 
					"Finding Accounts by the usernames of Accounts created "
					+ "from " + accountInfo(accountCreationDto1, role1)
					+ " and " + accountInfo(accountCreationDto2, role2)
					+ " results in finding equal Accounts "
					+ accountInfo(account1) + " and " + accountInfo(account2)
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void findingAccountByCreatedAccountsUsernameFindsTheCreatedAccountTest(final Role role) {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			Account account = accountCreatorService.create(
				accountCreationDto, role
			).get();
			
			Account accountFound = accountFinderService.findByUsername(
				account.getUsername()
			);
			
			assertEquals(
				account, accountFound,
				"The first created " + accountInfo(account)
				+ " differs from the found one " + accountInfo(accountFound)
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
	
	@Test
	public void ifThereNoAccountsCreatedWithGivenUsernameThenThatUsernameDoesNotExistTest() {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			final String username = accountCreationDto.getUsername();
			assertFalse(
				accountFinderService.existsByUsername(username),
				"Username '" + username + "' exists even when no Accounts are "
				+ "created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void ifAccountIsCreatedWithGivenUsernameThenThatUsernameExistsTest(final Role role) {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			accountCreatorService.create(accountCreationDto, role);

			assertTrue(
				accountFinderService.existsByUsername(
					accountCreationDto.getUsername()
				),
				accountInfo(accountCreationDto, role) + " does not exist"
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
		StreamUtils.zipWithIndex(accountCreationDtosWithUniqueUsernamesStream())
			.forEach(
				indexed -> {
					AccountCreationDto accountCreationDto = indexed.getValue();
					accountCreatorService.create(accountCreationDto, role);
					
					final Long created = indexed.getIndex() + 1;
					assertEquals(
						created, accountFinderService.list().size(),
						"After creating " + created + " Accounts, the list "
						+ "size was not incremented. The last created was "
						+ accountInfo(accountCreationDto, role)
					);
				}
			);
	}
	
	/*
	This method uses the same accountcreationdto twice in each iteration, thus
	the username and PASSWORD are the same. The test is run with different Roles
	*/
	@CartesianTest
	public void creatingAccountsWithUsernamesThatAreAlreadyTakenDoesNotIncrementTheAccountListSizeTest(
			@CartesianTest.Enum Role role1, @CartesianTest.Enum Role role2) {
		
		StreamUtils.zipWithIndex(accountCreationDtosWithUniqueUsernamesStream())
			.forEach(
				indexed -> {
					AccountCreationDto accountCreationDto = indexed.getValue();
					accountCreatorService.create(accountCreationDto, role1);
					
					final Long created = indexed.getIndex() + 1;
					assertEquals(
						created, accountFinderService.list().size(),
						"After creating the first "
						+ accountInfo(accountCreationDto, role1)
						+ ", the list size was not incremented"
					);
					
					accountCreatorService.create(accountCreationDto, role2);

					assertEquals(
						created, accountFinderService.list().size(),
						"After creating the second Account from the same "
						+ "AccountCreationDto, the list size was incremented. "
						+ "The last was created from "
						+ accountInfo(accountCreationDto, role2)
					);
				}
			);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void afterCreatingAccountThatAccountCanBeFoundFromTheAccountsListTest(final Role role) {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			Account account = accountCreatorService.create(
				accountCreationDto, role
			).get();
			
			assertTrue(
				accountFinderService.list().contains(account),
				accountInfo(accountCreationDto, role) + " can not be found "
				+ "in the list"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void ifAccountIsNotCreatedThenThatAccountCanNotBeFoundFromTheAccountListTest(final Role role) {
		accountCreationDtosWithUniqueUsernamesStream().forEach(accountCreationDto -> {
			Account account = new Account(
				accountCreationDto.getUsername(),
				accountCreatorService.encodePassword(
					accountCreationDto.getPassword()
				),
				role,
				new HashSet<>(), new HashSet<>(), new HashSet<>()
			);

			assertFalse(
				accountFinderService.list().contains(account),
				accountInfo(accountCreationDto, role)
				+ " that has not saved to the database can be found in the list"
			);
		});
	}
	
	/**
	 * 
	 * @return Stream of AccountCreationDto objects.
	 * 
	 * The ALl AccountCreationDto objects have unique username chosen from
	 * VALID_USERNAMEs list. There are as many objects in this stream as there
	 * are usernames in the list. Passwords are chosen from the VALID_PASSWORDs 
	 * list. All passwords are not guaranteed to be unique.
	 */
	private Stream<AccountCreationDto> accountCreationDtosWithUniqueUsernamesStream() {
		return IntStream.range(0, VALID_USERNAMES.size())
			.mapToObj(i -> new AccountCreationDto(
				VALID_USERNAMES.get(i), getValidPasswordModuloSize(i)
			)
		);
	}
	
	private String getValidPasswordModuloSize(int i) {
		return VALID_PASSWORDS.get(i % VALID_PASSWORDS.size());
	}
	
	private String accountInfo(AccountCreationDto accountCreationDto, Role role) {
		return "AccountCreationDto with username '"
			+ accountCreationDto.getUsername()
			+ "', raw password '" + accountCreationDto.getPassword()
			+ "' and role '" + role.getName() + "'";
	}
	
	private String accountInfo(Account account) {
		return "Account with username '" + account.getUsername()
			+ "', password '" + account.getPassword()
			+ "' and role '" + account.getRole().getName() + "'";
	}
}
