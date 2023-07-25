
package com.example.demo.unit;

import com.example.demo.validator.PasswordValidator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.NullAndEmptySource;

/*
Tests assumes that all characters are allowed in a password
*/
public class PasswordValidatorTest {
	
	private static final PasswordValidator validator = new PasswordValidator();
	
	private static final String MIN_LENGTH_PASSWORD = "1".repeat(PasswordValidator.PASSWORD_MIN_LENGTH);
	private static final String MAX_LENGTH_PASSWORD = "1".repeat(PasswordValidator.PASSWORD_MAX_LENGTH);
	
	private static final List<String> RANDOM_PASSWORDS = Arrays.asList(
		// ONLY ALPHABET
		"a", "C", "rKfW", "sCFcMyuu", "DbnuvCSLPGgc", "fJYpiKtPnXKmkHYFLnMQYp",
		// ONLY LOWERCASE/UPPERCASE
		"vlxlbgcvdk", "aoqnhbyqcykub", "BUAONEIPPSFJNA", "NTCDZJXEDWVNNSCOTJJP", 
		// ONLY NUMBERS
		"1", "99", "782352404", "0317881165485043", "1345400776759283911501493",
		// ONLY OTHER
		"]./\"$#", ")%[]_{'+#-!,)", "-}&\\-=:$,:#-@", "{)/,/#:$[!,@+;(%])!#?\"",
		// ALPHA & NUMBERS
		"lx30O7s", "C8mi6N26Vy", "qy2rsLT51sun8MitcR", "b9fwV70120ujpyzKzUzq0",
		// ALL
		"<T530i=-", "y5 (u9qAi4S", "y2wb14999  (10if", " h%y=82y\5Q3jPd(3U6w", 
		"45H2F8ITq++#03sR'6fa@", "I862+V017@x7D}xH]exSi7iCr[iL("
	);
	
	public static final List<String> VALID_PASSWORDS = new ArrayList<>() {{
		addAll(Arrays.asList(MIN_LENGTH_PASSWORD, MAX_LENGTH_PASSWORD));
		addAll(RANDOM_PASSWORDS);
	}};
	
	private static final String TOO_SHORT_PASSWORD = MIN_LENGTH_PASSWORD.substring(0, MIN_LENGTH_PASSWORD.length() - 1);
	private static final String TOO_LONG_PASSWORD = MAX_LENGTH_PASSWORD + "1";
	
	public static final List<String> INVALID_PASSWORDS = new ArrayList<>() {{
		addAll(Arrays.asList(null, ""));
		addAll(Arrays.asList(TOO_SHORT_PASSWORD, TOO_LONG_PASSWORD));
	}};
	
	@Test
	public void minLengthTest() {
		assertTrue(validator.isValid(MIN_LENGTH_PASSWORD, null));
	}
	
	@Test
	public void maxLengthTest() {
		assertTrue(validator.isValid(MAX_LENGTH_PASSWORD, null));
	}
	
	@Test
	public void tooShortTest() {
		assertFalse(validator.isValid(TOO_SHORT_PASSWORD, null));
	}
	
	@Test
	public void tooLongTest() {
		assertFalse(validator.isValid(TOO_LONG_PASSWORD, null));
	}
	
	@NullAndEmptySource
	public void notAllowedNullOrEmptyTest(String value) {
		assertFalse(validator.isValid(value, null));
	}
	
	@Test
	public void randomValuesTest() {
		for (String value : RANDOM_PASSWORDS) {
			assertTrue(
				validator.isValid(value, null),
				getErrorMessage(value)
			);
		}
	}
	
	private String getErrorMessage(String value) {
		return "Value '" + value + "' should be valid password!";
	}
}
