
package com.example.demo.integration.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.error.validation.ResourceNotFoundException;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.AccountDtoFinderService;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import jakarta.transaction.Transactional;
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

/*
is this test class needed? class account finder service is already tested
this class only maps account to accountdto

*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccountDtoFinderServiceTest {
	
	private final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	
	private final String username1 = VALID_USERNAMES.get(0);
	private final String username2 = VALID_USERNAMES.get(1);
	
	private final String password1 = VALID_PASSWORDS.get(0);
	private final String password2 = VALID_PASSWORDS.get(1);
	
	private final long id = 1l;
	
	@Autowired
	private AccountDtoFinderService accountDtoFinderService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	
	@Test
	public void findDtoByIdNullThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountDtoFinderService.findById(null),
			"Serching AccountDto by null id does not throw "
			+ "IllegalArgumentException"
		);
	}
	
	@Test
	public void findDtoByIdWhenIdIsNotFoundThrowsTest() {
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountDtoFinderService.findById(id),
			"Searching AccountDto by id that is not found from the database "
			+ "does not throw ResourceNotFoundException"
		);	
	}
	
	@Test
	public void findDtoByIdDoesNotThrowWhenIdExists() {
		Account account = accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertDoesNotThrow(
			() -> accountDtoFinderService.findById(account.getId()),
			"When finding the created AccountDto by id the method throws"
		);
	}
	
	@Test
	public void findDtoByIdAfterCreatingAccountFindsTheCorrectOneTest() {
		AccountDto accountDto1 = accountCreatorService.createAndGetDto(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		AccountDto accountDto2 = accountCreatorService.createAndGetDto(
			new AccountCreationDto(username2, password2), Role.ADMIN
		).get();
		
		AccountDto accountDtoFound1 = accountDtoFinderService.findById(
			accountDto1.getId()
		);
		AccountDto accountDtoFound2 = accountDtoFinderService.findById(
			accountDto2.getId()
		);
		
		assertNotEquals(
			accountDtoFound1, accountDtoFound2,
			"Different ids find the same AccountDto"
		);
		
		assertEquals(
			accountDto1, accountDtoFound1,
			"AccountDto found does not equal AccountDto created"
		);
		
		assertEquals(
			accountDto2, accountDtoFound2,
			"AccountDto found does not equal AccountDto created"
		);
	}
	
	@Test
	public void findDtoByUsernameNullThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountDtoFinderService.findById(null),
			"Serching AccountDto by null username does not throw "
			+ "IllegalArgumentException"
		);
	}
	
	@Test
	public void findDtoByUsernameWhenUsernameIsNotFoundThrowsTest() {
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountDtoFinderService.findByUsername(username1),
			"Searching AccountDto by username that is not found from the "
			+ "database does not throw ResourceNotFoundException"
		);	
	}
	
	@Test
	public void findDtoByUsernameDoesNotThrowWhenUsernameExists() {
		Account account = accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertDoesNotThrow(
			() -> accountDtoFinderService.findByUsername(
				account.getUsername()
			),
			"When finding the created AccountDto by username the method throws"
		);
	}
	
	@Test
	public void findDtoByUsernameAfterCreatingAccountFindsTheCorrectOneTest() {
		AccountDto accountDto1 = accountCreatorService.createAndGetDto(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		AccountDto accountDto2 = accountCreatorService.createAndGetDto(
			new AccountCreationDto(username2, password2), Role.ADMIN
		).get();
		
		AccountDto accountDtoFound1 = accountDtoFinderService.findByUsername(
			accountDto1.getUsername()
		);
		AccountDto accountDtoFound2 = accountDtoFinderService.findByUsername(
			accountDto2.getUsername()
		);
		
		assertNotEquals(
			accountDtoFound1, accountDtoFound2, 
			"Different usernames find the same AccountDto"
		);
		
		assertEquals(
			accountDto1, accountDtoFound1,
			"The first created Account differs from the found one"
		);
		
		assertEquals(
			accountDto2, accountDtoFound2,
			"The second created Account differs from the found one"
		);
	}
	
	@Test
	public void listDtoIsEmptyTest() {
		assertTrue(
			accountDtoFinderService.list().isEmpty(),
			"AccountDto list is not empty before creating any Accounts"
		);
	}
	
	@Test
	public void listDtoCreatingAccountsWithUniqueUsernamesIncrementTheListSizeTest() {
		accountCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		);
		
		assertEquals(
			1, accountDtoFinderService.list().size(),
			"After creating the first Account, the list size is not incremented"
		);
		
		accountCreatorService.create(
			new AccountCreationDto(username2, password2), Role.ADMIN
		);
		
		assertEquals(
			2, accountDtoFinderService.list().size(),
			"After creating the second Account, the list size is not incremented"
		);
	}
	
	@Test
	public void listDtoCreatingAccountsWithDublicateUsernamesDoesNotIncrementTheListSizeTest() {
		final String commonUsername = username1;
		accountCreatorService.create(
			new AccountCreationDto(commonUsername, password1), Role.USER
		);
		
		assertEquals(
			1, accountDtoFinderService.list().size(),
			"After creating the first Account, the list size is not incremented"
		);
		
		accountCreatorService.create(
			new AccountCreationDto(commonUsername, password2), Role.ADMIN
		);
		
		assertEquals(
			1, accountDtoFinderService.list().size(),
			"After creating the second Account with the same username as the "
			+ "first one, the list size changes"
		);
	}
	
	@Test
	public void listDtoCreatedAccountsCanBeFoundInTheListTest() {
		AccountDto accountDto1 = accountCreatorService.createAndGetDto(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertTrue(
			accountDtoFinderService.list().contains(accountDto1),
			"The first created AccountDto can not be found in the list"
		);
		
		AccountDto accountDto2 = accountCreatorService.createAndGetDto(
			new AccountCreationDto(username2, password2), Role.ADMIN
		).get();
		
		assertTrue(
			accountDtoFinderService.list().contains(accountDto2),
			"The second created AccountDto can not be found in the list"
		);
	}
	
	@Test
	public void listDtoNotCreatedAccountCanNotBeFoundFromTheListTest() {
		AccountDto accountDto = new AccountDto(id, username1);
		
		assertFalse(
			accountDtoFinderService.list().contains(accountDto),
			"AccountDto that has not saved to the database can be found from "
			+ " Account list"
		);
	}
}
