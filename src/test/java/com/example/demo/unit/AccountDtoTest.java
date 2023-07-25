
package com.example.demo.unit;

import com.example.demo.datatransfer.AccountDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountDtoTest {
	
	private final Validator validator
			= Validation.buildDefaultValidatorFactory().getValidator();
	
	private final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> INVALID_USERNAMES = UsernameValidatorTest.INVALID_USERNAMES;
	
	@Test
	public void equalsTest() {
		final Long id1 = 1l;
		final Long id2 = 2l;
		
		final String username1 = VALID_USERNAMES.get(0);
		final String username2 = VALID_USERNAMES.get(1);
		
		AccountDto ac110 = new AccountDto(id1, username1);
		AccountDto ac11 = new AccountDto(id1, username1);
		AccountDto ac12 = new AccountDto(id1, username2);
		AccountDto ac21 = new AccountDto(id2, username1);
		AccountDto ac22 = new AccountDto(id2, username2);
		
		assertNotEquals(ac11, null, "Non null AccountDto equals null");
		
		assertEquals(ac11, ac11, "AccountDto does not equal self");
		assertEquals(
			ac11, ac110,
			"AccountDto objects does not equal when ids and usernames equals"
		);
		
		assertNotEquals(
			ac11, ac12, 
			"AccountDto objects equals when usernames does not equal"
		);
		assertNotEquals(
			ac11, ac21,
			"AccountDto objects equals when ids does not equal"
		);
		
		assertNotEquals(
			ac11, ac22,
			"AccountDto objects equals when ids and usernames does not equal"
		);
	}
	
	@Test
	public void nullIdTest() {
		Set<ConstraintViolation<AccountDto>> violations
			= validator.validate(new AccountDto(null, VALID_USERNAMES.get(0)));
		
		assertFalse(violations.isEmpty(), "Null id should be a validation error");
	}
	
	@Test
	public void validUsernameTest() {
		final Long id = 1l;
		
		for (String username : VALID_USERNAMES) {
			Set<ConstraintViolation<AccountDto>> violations
				= validator.validate(new AccountDto(id, username));
			
			assertTrue(
				violations.isEmpty(),
				"Id '" + id + "' and username '" + username 
				+ "' should be valid and not cause a validation error"
			);
		}
	}
	
	@Test
	public void invalidUsernameTest() {
		final Long id = 1l;
		
		for (String username : INVALID_USERNAMES) {
			Set<ConstraintViolation<AccountDto>> violations
				= validator.validate(new AccountDto(id, username));
			
			assertFalse(
				violations.isEmpty(),
				"Id '" + id + "' and username '" + username 
				+ "' should cause a validation error since username is invalid"
			);
		}
	}
}
