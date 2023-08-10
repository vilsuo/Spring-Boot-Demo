
package com.example.demo.integration.service;

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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccountFinderServiceTest {
	
	private final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	
	private final String username1 = VALID_USERNAMES.get(0);
	private final String username2 = VALID_USERNAMES.get(1);
	
	private final String password1 = VALID_PASSWORDS.get(0);
	private final String password2 = VALID_PASSWORDS.get(1);
	
	private final long id = 1l;
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Test
	public void findByIdNullThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountFinderService.findById(null),
			"Serching Account by null id does not throw "
			+ "IllegalArgumentException"
		);
	}
	
	@Test
	public void findByIdWhenIdIsNotFoundThrowsTest() {
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountFinderService.findById(id),
			"Searching Account by id that is not found from the database "
			+ "does not throw ResourceNotFoundException"
		);	
	}
	
	@Test
	public void findByIdDoesNotThrowWhenIdExists() {
		Account account = accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertDoesNotThrow(
			() -> accountFinderService.findById(account.getId()),
			"When finding the created Account by id the method throws"
		);
	}
	
	@Test
	public void findByIdAfterCreatingAccountFindsTheCorrectOneTest() {
		Account account1 = accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		Account account2 = accountCreatorService.create(
			new AccountCreationDto(username2, password2), Role.ADMIN
		).get();
		
		Account accountFound1 = accountFinderService.findById(
			account1.getId()
		);
		Account accountFound2 = accountFinderService.findById(
			account2.getId()
		);
		
		assertNotEquals(
			accountFound1, accountFound2, "Different ids find the same Account"
		);
		
		assertEquals(
			account1, accountFound1,
			"The first created Account differs from the found one"
		);
		
		assertEquals(
			account2, accountFound2,
			"The second created Account differs from the found one"
		);
	}
	
	@Test
	public void findByUsernameNullThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountFinderService.findById(null),
			"Serching Account by null username does not throw "
			+ "IllegalArgumentException"
		);
	}
	
	@Test
	public void findByUsernameWhenUsernameIsNotFoundThrowsTest() {
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountFinderService.findByUsername(username1),
			"Searching Account by username that is not found from the database "
			+ "does not throw ResourceNotFoundException"
		);	
	}
	
	@Test
	public void findByUsernameDoesNotThrowWhenUsernameExists() {
		Account account = accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertDoesNotThrow(
			() -> accountFinderService.findByUsername(account.getUsername()),
			"When finding the created Account by username the method throws"
		);
	}
	
	@Test
	public void findByUsernameAfterCreatingAccountFindsTheCorrectOneTest() {
		Account account1 = accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		Account account2 = accountCreatorService.create(
			new AccountCreationDto(username2, password2), Role.ADMIN
		).get();
		
		Account accountFound1 = accountFinderService.findByUsername(
			account1.getUsername()
		);
		Account accountFound2 = accountFinderService.findByUsername(
			account2.getUsername()
		);
		
		assertNotEquals(
			accountFound1, accountFound2, 
			"Different usernames find the same Account"
		);
		
		assertEquals(
			account1, accountFound1,
			"The first created Account differs from the found one"
		);
		
		assertEquals(
			account2, accountFound2,
			"The second created Account differs from the found one"
		);
	}
	
	@Test
	public void existsByUsernameNullThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountFinderService.existsByUsername(null),
			"Searching if Account exists by null username does not throw "
			+ "IllegalArgumentException"
		);
	}
	
	@Test
	public void existsByUsernameDoesNotFindUsernameThatDoesNotExistTest() {
		assertFalse(
			accountFinderService.existsByUsername(username1),
			"Username exists even when no Accounts are created"
		);
	}
	
	@Test
	public void existsByUsernameTakenUsernameIsFoundTest() {
		final String username = username1;
		accountCreatorService.create(
			new AccountCreationDto(username, password1), Role.USER
		);
		
		assertTrue(
			accountFinderService.existsByUsername(username),
			"Username is not taken when an Account is created with the username"
		);
	}
	
	@Test
	public void listIsEmptyTest() {
		assertTrue(
			accountFinderService.list().isEmpty(),
			"Account list is not empty before creating any Accounts"
		);
	}
	
	@Test
	public void listCreatingAccountsWithUniqueUsernamesIncrementTheListSizeTest() {
		accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		);
		
		assertEquals(
			1, accountFinderService.list().size(),
			"After creating the first Account, the list size is not incremented"
		);
		
		accountCreatorService.create(
			new AccountCreationDto(username2, password2), Role.ADMIN
		);
		
		assertEquals(
			2, accountFinderService.list().size(),
			"After creating the second Account, the list size is not incremented"
		);
	}
	
	@Test
	public void listCreatingAccountsWithDublicateUsernamesDoesNotIncrementTheListSizeTest() {
		final String commonUsername = username1;
		accountCreatorService.create(
			new AccountCreationDto(commonUsername, password1), Role.USER
		);
		
		assertEquals(
			1, accountFinderService.list().size(),
			"After creating the first Account, the list size is not incremented"
		);
		
		accountCreatorService.create(
			new AccountCreationDto(commonUsername, password2), Role.ADMIN
		);
		
		assertEquals(
			1, accountFinderService.list().size(),
			"After creating the second Account with the same username as the "
			+ "first one, the list size changes"
		);
	}
	
	@Test
	public void listCreatedAccountsCanBeFoundInTheListTest() {
		Account account1 = accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertTrue(
			accountFinderService.list().contains(account1),
			"The first created Account can not be found in the list"
		);
		
		Account account2 = accountCreatorService.create(
			new AccountCreationDto(username2, password2), Role.ADMIN
		).get();
		
		assertTrue(
			accountFinderService.list().contains(account2),
			"The second created Account can not be found in the list"
		);
	}
	
	@Test
	public void listNotCreatedAccountCanNotBeFoundFromTheListTest() {
		Account account = new Account(
			username1, password1, Role.USER,
			new HashSet<>(), new HashSet<>(), new HashSet<>()
		);
		
		assertFalse(
			accountFinderService.list().contains(account),
			"Account that has not saved to the database can be found from "
			+ " Account list"
		);
	}
}
