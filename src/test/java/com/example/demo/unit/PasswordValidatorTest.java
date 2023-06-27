
package com.example.demo.unit;

import com.example.demo.validator.PasswordValidator;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class PasswordValidatorTest {
	
	private PasswordValidator validator = new PasswordValidator();
	
	@Test
	public void minLengthTest() {
		String password = "1".repeat(PasswordValidator.PASSWORD_MIN_LENGTH);
		assertTrue(validator.isValid(password, null));
	}
	
	@Test
	public void maxLengthTest() {
		String password = "1".repeat(PasswordValidator.PASSWORD_MAX_LENGTH);
		assertTrue(validator.isValid(password, null));
	}
	
	@Test
	public void tooLongTest() {
		String password = "1".repeat(PasswordValidator.PASSWORD_MAX_LENGTH) + "1";
		assertFalse(validator.isValid(password, null));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
		" abcdefgh", "jiklmnopqrstu", "vwxyzåäö", "1234567890",
		"ABCDEFGHIJ", "KLMNOPQRST", "UVWXYZÅÄÖ",
		"<>|,;.:-_'*¨^~", "§½!@#£¤$%€&", "/\\{}()[]=?}´`}"
	})
	public void allowedCharactersTest(String value) {
		assertTrue(
			validator.isValid(value, null),
			getErrorMessage(value)
		);
	}
	
	@NullAndEmptySource
	public void notAllowedNullOrEmptyTest(String value) {
		assertFalse(validator.isValid(value, null));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
		"        ", " between ", "l3tt3r5_4ND NuM83R5",
		"hR2/-Rz+ls", "951mC}w75e3A", "xO5\"xuu8,u2=sG", 
		"q7M\"uh4+7l!wP?wa", "izX3-fp59AMSm/,_Fr", 
		"8GhJ*G\\LDy=5#01cHYR", ".EI.y[TR021t6j4&Aj\\)"
	})
	public void randomValuesTest(String value) {
		assertTrue(
			validator.isValid(value, null), 
			getErrorMessage(value)
		);
	}
	
	private String getErrorMessage(String value) {
		return "Value '" + value + "' should be valid password!";
	}
}
