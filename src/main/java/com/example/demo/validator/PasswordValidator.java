
package com.example.demo.validator;

import com.example.demo.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

	public final static int PASSWORD_MIN_LENGTH = 1;
	public final static int PASSWORD_MAX_LENGTH = 30;
	
	/*
	https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#cg
	
	Boundary matchers
		^	The beginning of a line
		$	The end of a line
	
	Special constructs (named-capturing and non-capturing)
		(?=X)	X, via zero-width positive lookahead
	*/
	private final static String REGEX
			// length USERNAME_MIN_LENGTH -- USERNAME_MAX_LENGTH
			= "^.{" + PASSWORD_MIN_LENGTH + "," + PASSWORD_MAX_LENGTH + "}$";
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(value);
		
		return matcher.matches();
	}
}
