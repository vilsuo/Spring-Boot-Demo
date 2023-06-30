
package com.example.demo.unit;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.validator.UsernameValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class AccountDtoTest {
	
	private UsernameValidator usernameValidator = new UsernameValidator();
	
	private final Validator validator
			= Validation.buildDefaultValidatorFactory().getValidator();
	
	@Test
	public void idTest() {
		/*
		Set<ConstraintViolation<AccountDto>> violations
				= validator.validate(new AccountDto(null, ));
		
		assertThrows(
				
		);
		*/
	}
}
