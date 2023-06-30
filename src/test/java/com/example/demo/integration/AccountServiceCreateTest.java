package com.example.demo.integration;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.error.validation.ResourceNotFoundException;
import com.example.demo.service.AccountService;
import com.example.demo.validator.PasswordValidator;
import com.example.demo.validator.UsernameValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
public class AccountServiceCreateTest {

	@Autowired
	private AccountService accountService;
	
	private final String validUsername1 = "valid1";
	private final String validUsername2 = "valid2";
	private final String validUsername3 = "valid3";
	private final String invalidUsername = "_invalid";
	
	private final String validPassword = "placeholder";
	private final String invalidPassword = "";
	
	@Test
	public void credentialsTest() {
		UsernameValidator usernameValidator = new UsernameValidator();
		assertTrue(
			usernameValidator.isValid(validUsername1, null),
			"Username that is supposed to be valid is not: " + validUsername1
		);
		assertTrue(
			usernameValidator.isValid(validUsername2, null),
			"Username that is supposed to be valid is not: " + validUsername2
		);
		assertTrue(
			usernameValidator.isValid(validUsername3, null),
			"Username that is supposed to be valid is not: " + validUsername3
		);
		assertFalse(
			usernameValidator.isValid(invalidUsername, null),
			"Username that is supposed to be invalid is not: " + invalidUsername
		);
		
		PasswordValidator passwordValidator = new PasswordValidator();
		assertTrue(
			passwordValidator.isValid(validPassword, null),
			"Password that is supposed to be valid is not: " + validPassword
		);
		assertFalse(
			passwordValidator.isValid(invalidPassword, null),
			"Password that is supposed to be invalid is not: " + invalidPassword
		);
	}
	
	@Test
	public void createThrowsOnNullParameters() {
		assertThrows(
			ConstraintViolationException.class,
			() -> accountService.createUSER(new AccountCreationDto(invalidUsername, validPassword)),
			"Creating account with invalid username does not throw ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountService.createUSER(new AccountCreationDto(validUsername1, invalidPassword)),
			"Creating account with invalid password does not throw ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountService.createUSER(new AccountCreationDto(null, validPassword)),
			"Creating account with null username does not throw ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountService.createUSER(new AccountCreationDto(validUsername1, null)),
			"Creating account with null password does not throw ConstraintViolationException"
		);
		
		assertThrows(
			ConstraintViolationException.class,
			() -> accountService.createUSER(new AccountCreationDto()),
			"Creating account with uninitialized AccountCreationDto does not throw ConstraintViolationException"
		);
		
		assertThrows(
			NullPointerException.class,
			() -> accountService.createUSER(null),
			"Creating account with null AccountCreationDto does not throw NullPointerException"
		);
	}
	
	@Test
	public void createWithoutTakenUsernamesTest() {
		assertTrue(
			accountService.list().isEmpty(),
			"Account list is not empty initially"
		);
		
		AccountCreationDto accountCreationDto1
			= new AccountCreationDto(validUsername1, validPassword);
		
		Optional<AccountDto> opt1 = accountService.createUSER(accountCreationDto1);
		
		assertTrue(
			opt1.isPresent(),
			"Could not create Account in empty database"
		);
		assertEquals(
			accountCreationDto1.getUsername(), opt1.get().getUsername(), 
			"Username of the first created accountCreationDto and Account being saved does not match"
		);
		assertEquals(
			1, accountService.list().size(),
			"Creating the first Account did not increment the database size"
		);
		
		AccountCreationDto accountCreationDto2
			= new AccountCreationDto(validUsername2, validPassword);
		
		Optional<AccountDto> opt2 = accountService.createUSER(accountCreationDto2);
		
		assertTrue(
			opt2.isPresent(),
			"Could not create second Account with different a username than the first one"
		);
		
		assertEquals(
			2, accountService.list().size(), 
			"Creating the second Account did not increment the database size"
		);	
	}
	
	@Test
	public void createWithTakenUsernameTest() {
		assertTrue(
			accountService.list().isEmpty(),
			"Account list is not empty initially"
		);
		
		AccountCreationDto accountCreationDto
			= new AccountCreationDto(validUsername1, validPassword);
		
		Optional<AccountDto> opt1 = accountService.createUSER(accountCreationDto);
		
		assertTrue(
			opt1.isPresent(),
			"Could not create Account in empty database"
		);
		
		assertEquals(
			1, accountService.list().size(), 
			"Creating the first Account did not increment the database size"
		);
		
		AccountCreationDto accountCreationDtoDublicateUsername
			= new AccountCreationDto(validUsername1, validPassword);
		
		Optional<AccountDto> opt2
			= accountService.createUSER(accountCreationDtoDublicateUsername);
		
		assertTrue(
			opt2.isEmpty(),
			"Account with username that was already taken was created"
		);
		
		assertEquals(
			1, accountService.list().size(), 
			"Trying to create an Account with already taken username incremented the database size"
		);
	}
	
	@Test
	public void findByIdTest() {
		assertThrows(
			NullPointerException.class,
			() -> accountService.findById(null),
			"Serching Account by null id does not throw NullPointerException"
		);
		
		assertThrows(
			NullPointerException.class,
			() -> accountService.findDtoById(null),
			"Serching AccountDto by null id does not throw NullPointerException"
		);
		
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountService.findById(1l),
			"Searching Account by id that is not found from the database does not throw ResourceNotFoundException"
		);
		
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountService.findDtoById(1l),
			"Searching AccountDto by id that is not found from the database does not throw ResourceNotFoundException"
		);
		
		AccountDto accountDto
			= accountService.createUSER(
				new AccountCreationDto(validUsername1, validPassword)
			).get();
		
		Account accountFound = accountService.findById(accountDto.getId());
		AccountDto accountDtoFound = accountService.findDtoById(accountDto.getId());
		
		assertEquals(
			accountDto.getUsername(), accountFound.getUsername(),
			"Finding Account does not return an AccountDto with correct username"
		);
		assertEquals(
			accountDto.getUsername(), accountDtoFound.getUsername(),
			"Finding AccountDto does not return AccountDto with correct username"
		);
	}
	
	@Test
	public void findByUsernameTest() {
		assertThrows(
			NullPointerException.class,
			() -> accountService.findByUsername(null),
			"Serching Account by null username does not throw NullPointerException"
		);
		
		assertThrows(
			NullPointerException.class,
			() -> accountService.findDtoByUsername(null),
			"Serching AccountDto by null username does not throw NullPointerException"
		);
		
		assertThrows(
			NullPointerException.class,
			() -> accountService.existsByUsername(null),
			"Serching if null username is taken does not throw NullPointerException"
		);
		
		assertFalse(
			accountService.existsByUsername(validUsername1),
			"Username can be found before creation"
		);
		
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountService.findByUsername(validUsername1),
			"Searching Account by username that is not found from the database does not throw ResourceNotFoundException"
		);
		
		assertThrows(
			ResourceNotFoundException.class,
			() -> accountService.findDtoByUsername(validUsername1),
			"Searching AccountDto by id that is not found from the database does not throw ResourceNotFoundException"
		);
		
		AccountDto accountDto
			= accountService.createUSER(
				new AccountCreationDto(validUsername1, validPassword)
			).get();
		
		assertTrue(
			accountService.existsByUsername(validUsername1),
			"Username can not be found after creation"
		);
		
		Account accountFound = accountService.findByUsername(accountDto.getUsername());
		AccountDto accountDtoFound = accountService.findDtoByUsername(accountDto.getUsername());
		
		assertEquals(
			accountDto.getUsername(), accountFound.getUsername(),
			"Finding Account does not return an AccountDto with correct username"
		);
		assertEquals(
			accountDto.getUsername(), accountDtoFound.getUsername(),
			"Finding AccountDto does not return AccountDto with correct username"
		);
	}
	
	public void listTest() {
		assertTrue(accountService.list().isEmpty(), "Account list is not empty initially");
		
		AccountCreationDto dto1 = new AccountCreationDto(validUsername1, validPassword);
		accountService.createUSER(dto1);
		assertEquals(
			1, accountService.list().size(), 
			"After creating an account, the account list size is not incremented"
		);
		assertTrue(
			accountService.list().contains(dto1),
			"After creating an account, the account list does not contain the created account"
		);
		
		AccountCreationDto dto2 = new AccountCreationDto(validUsername2, validPassword);
		accountService.createUSER(dto2);
		assertEquals(
			2, accountService.list().size(),
			"After creating a second account, the account list size is not incremented"
		);
		assertTrue(
			accountService.list().contains(dto1),
			"After creating a second account, the account list does not contain the first account"
		);
		assertTrue(
			accountService.list().contains(dto2),
			"After creating a second account, the account list does not contain the second account"
		);
		
	}
}