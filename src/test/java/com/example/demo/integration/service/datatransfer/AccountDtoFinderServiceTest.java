
package com.example.demo.integration.service.datatransfer;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Role;
import com.example.demo.error.validation.ResourceNotFoundException;
import com.example.demo.service.datatransfer.AccountDtoCreatorService;
import com.example.demo.service.datatransfer.AccountDtoFinderService;
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

maybe create common base class for these two

does not test existsbyusername method
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
	private AccountDtoCreatorService accountDtoCreatorService;
	
	@Test
	public void findByIdNullThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountDtoFinderService.findById(null),
			"Serching AccountDto by null id does not throw "
			+ "IllegalArgumentException"
		);
	}
	
	@Test
	public void findByIdWhenIdIsNotFoundThrowsTest() {
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountDtoFinderService.findById(id),
			"Searching AccountDto by id that is not found from the database "
			+ "does not throw ResourceNotFoundException"
		);	
	}
	
	@Test
	public void findByIdDoesNotThrowWhenIdExists() {
		AccountDto accountDto = accountDtoCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertDoesNotThrow(
			() -> accountDtoFinderService.findById(accountDto.getId()),
			"When finding the created AccountDto by id the method throws"
		);
	}
	
	@Test
	public void findByIdAfterCreatingAccountFindsTheCorrectOneTest() {
		AccountDto accountDto1 = accountDtoCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		AccountDto accountDto2 = accountDtoCreatorService.create(
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
	public void findByUsernameNullThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> accountDtoFinderService.findById(null),
			"Serching AccountDto by null username does not throw "
			+ "IllegalArgumentException"
		);
	}
	
	@Test
	public void findByUsernameWhenUsernameIsNotFoundThrowsTest() {
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountDtoFinderService.findByUsername(username1),
			"Searching AccountDto by username that is not found from the "
			+ "database does not throw ResourceNotFoundException"
		);	
	}
	
	@Test
	public void findByUsernameDoesNotThrowWhenUsernameExists() {
		AccountDto accountDto = accountDtoCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertDoesNotThrow(
			() -> accountDtoFinderService.findByUsername(
				accountDto.getUsername()
			),
			"When finding the created AccountDto by username the method throws"
		);
	}
	
	@Test
	public void findByUsernameAfterCreatingAccountFindsTheCorrectOneTest() {
		AccountDto accountDto1 = accountDtoCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		AccountDto accountDto2 = accountDtoCreatorService.create(
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
	public void listIsEmptyTest() {
		assertTrue(
			accountDtoFinderService.list().isEmpty(),
			"AccountDto list is not empty before creating any Accounts"
		);
	}
	
	@Test
	public void listCreatingAccountsWithUniqueUsernamesIncrementTheListSizeTest() {
		accountDtoCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		);
		
		assertEquals(
			1, accountDtoFinderService.list().size(),
			"After creating the first Account, the list size is not incremented"
		);
		
		accountDtoCreatorService.create(
			new AccountCreationDto(username2, password2), Role.ADMIN
		);
		
		assertEquals(
			2, accountDtoFinderService.list().size(),
			"After creating the second Account, the list size is not incremented"
		);
	}
	
	@Test
	public void listCreatingAccountsWithDublicateUsernamesDoesNotIncrementTheListSizeTest() {
		final String commonUsername = username1;
		accountDtoCreatorService.create(
			new AccountCreationDto(commonUsername, password1), Role.USER
		);
		
		assertEquals(
			1, accountDtoFinderService.list().size(),
			"After creating the first Account, the list size is not incremented"
		);
		
		accountDtoCreatorService.create(
			new AccountCreationDto(commonUsername, password2), Role.ADMIN
		);
		
		assertEquals(
			1, accountDtoFinderService.list().size(),
			"After creating the second Account with the same username as the "
			+ "first one, the list size changes"
		);
	}
	
	@Test
	public void listCreatedAccountsCanBeFoundInTheListTest() {
		AccountDto accountDto1 = accountDtoCreatorService.create(
			new AccountCreationDto(username1, password1), Role.USER
		).get();
		
		assertTrue(
			accountDtoFinderService.list().contains(accountDto1),
			"The first created AccountDto can not be found in the list"
		);
		
		AccountDto accountDto2 = accountDtoCreatorService.create(
			new AccountCreationDto(username2, password2), Role.ADMIN
		).get();
		
		assertTrue(
			accountDtoFinderService.list().contains(accountDto2),
			"The second created AccountDto can not be found in the list"
		);
	}
	
	@Test
	public void listNotCreatedAccountCanNotBeFoundFromTheListTest() {
		AccountDto accountDto = new AccountDto(id, username1);
		
		assertFalse(
			accountDtoFinderService.list().contains(accountDto),
			"AccountDto that has not saved to the database can be found from "
			+ " Account list"
		);
	}
}
