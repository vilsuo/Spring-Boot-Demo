
package com.example.demo.unit;

import com.example.demo.datatransfer.AccountCreationDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountCreationDtoTest {
	
	private final Validator validator
			= Validation.buildDefaultValidatorFactory().getValidator();

	private final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> INVALID_USERNAMES = UsernameValidatorTest.INVALID_USERNAMES;
	
	private final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	private final List<String> INVALID_PASSWORDS = PasswordValidatorTest.INVALID_PASSWORDS;
	
	@Test
	public void equalsTest() {
		final String username1 = VALID_USERNAMES.get(0);
		final String username2 = VALID_USERNAMES.get(1);
		
		final String password1 = VALID_PASSWORDS.get(0);
		final String password2 = VALID_PASSWORDS.get(1);
		
		AccountCreationDto ac110 = new AccountCreationDto(username1, password1);
		AccountCreationDto ac11 = new AccountCreationDto(username1, password1);
		AccountCreationDto ac12 = new AccountCreationDto(username1, password2);
		AccountCreationDto ac21 = new AccountCreationDto(username2, password1);
		AccountCreationDto ac22 = new AccountCreationDto(username2, password2);
		
		assertNotEquals(ac11, null, "Non null AccountCreationDto equals null");
		
		assertEquals(ac11, ac11, "AccountCreationDto does not equal self");
		assertEquals(
			ac11, ac110,
			"AccountCreationDto objects does not equal when usernames "
			+ "and passwords equals"
		);
		
		assertNotEquals(
			ac11, ac12, 
			"AccountCreationDto objects equals when passwords does not equal"
		);
		assertNotEquals(
			ac11, ac21,
			"AccountCreationDto objects equals when usernames does not equal"
		);
		
		assertNotEquals(
			ac11, ac22,
			"AccountCreationDto objects equals when usernames "
			+ "and passwords does not equal"
		);
	}
	
	@Test
	public void invalidUsernameTest() {
		helper(INVALID_USERNAMES, VALID_PASSWORDS, false, true);
	}
	
	@Test
	public void invalidPasswordTest() {
		helper(VALID_USERNAMES, INVALID_PASSWORDS, true, false);
	}
	
	@Test
	public void validUsernameAndPasswordTest() {
		helper(VALID_USERNAMES, VALID_PASSWORDS, true, true);
	}
	
	private void helper(final List<String> usernames, final List<String> passwords, 
			boolean validUsernames, boolean validPasswords) {
		
		final int nUsernames = usernames.size();
		final int nPasswords = passwords.size();
		
		int ui = 0;
		int pi = 0;
		for (int i = 0; i < Math.max(nUsernames, nPasswords); ++i) {
			ui %= nUsernames;
			pi %= nPasswords;
			
			String username = usernames.get(ui);
			String password = passwords.get(pi);
			
			Set<ConstraintViolation<AccountCreationDto>> violations
				= validator.validate(new AccountCreationDto(username, password));
			
			if (validUsernames && validPasswords) {
				assertTrue(
					violations.isEmpty(),
					"Username '" + username + "' and password '" + password + "' "
					+ "should be valid and not cause a validation error"
				);
			} else {
				assertFalse(
					violations.isEmpty(),
					"Username '" + username + "' and password '" + password + "' "
					+ "should cause a validation error because username is "
					+ (validUsernames ? "valid" : "invalid") 
					+ " and password is "
					+ (validPasswords ? "valid" : "invalid")
				);
			}
			
			++ui;
			++pi;
		}
	}
}
