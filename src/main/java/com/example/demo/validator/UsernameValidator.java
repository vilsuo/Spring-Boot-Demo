
package com.example.demo.validator;

import com.example.demo.annotation.Username;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class UsernameValidator implements ConstraintValidator<Username, String> {

	public final static int USERNAME_MIN_LENGTH = 1;
	public final static int USERNAME_MAX_LENGTH = 30;
	
	public final static String SIMPLE_USERNAME_PATTERN = "[a-zA-Z0-9_]+";
	
	/*
	https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#cg
	
	Boundary matchers
		^	The beginning of a line
		$	The end of a line
	
	Special constructs (named-capturing and non-capturing)
		(?=X)	X, via zero-width positive lookahead
		(?!X)	X, via zero-width negative lookahead
		(?<!X)	X, via zero-width negative lookbehind
	*/
	private final static String REGEX 
			// length USERNAME_MIN_LENGTH -- USERNAME_MAX_LENGTH
			= "^(?=.{" + USERNAME_MIN_LENGTH + "," + USERNAME_MAX_LENGTH + "}$)"
			
			// can not start by '_'
			+ "(?!_)"
			
			// can not contain more than one '_' in a row
			+ "(?!.*_{2})"
			
			// allowed characters, must contain atleast one
			+ "[a-zA-Z0-9_]+"
			
			// can not end in '_'
			+ "(?<!_)$";
	
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
