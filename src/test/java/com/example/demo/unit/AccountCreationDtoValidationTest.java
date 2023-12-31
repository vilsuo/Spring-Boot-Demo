
package com.example.demo.unit;

import com.example.demo.datatransfer.AccountCreationDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.uniqueAccountCreationDtoStream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountCreationDtoValidationTest {
	
	@Autowired
    private Validator validator;
	
	@Test
	public void invalidUsernameCausesValidationErrorTest() {
		usernameAndPasswordValidationHelper(false, true);
	}
	
	@Test
	public void invalidPasswordCausesValidationErrorTest() {
		usernameAndPasswordValidationHelper(true, false);
	}
	
	@Test
	public void validUsernameAndPasswordDoNotCauseValidationErrorsTest() {
		usernameAndPasswordValidationHelper(true, true);
	}
	
	private void usernameAndPasswordValidationHelper(
			boolean validUsernames, boolean validPasswords) {
		
		uniqueAccountCreationDtoStream(validUsernames, validPasswords)
			.forEach(accountCreationDto -> {
			
				final Set<ConstraintViolation<AccountCreationDto>> violations
					= validator.validate(accountCreationDto);
			
				final String username = accountCreationDto.getUsername();
				final String password = accountCreationDto.getPassword();
				if (validUsernames && validPasswords) {
					assertTrue(
						violations.isEmpty(),
						"Username '" + username + "' and password '" + password 
						+ "' should be valid and not cause a validation error"
					);
				} else {
					assertFalse(
						violations.isEmpty(),
						"Username '" + username + "' and password '" + password
						+ "' should cause a validation error because username "
						+ "is " + (validUsernames ? "valid" : "invalid")
						+ " and password is "
						+ (validPasswords ? "valid" : "invalid")
					);
				}
			});
	}
}
