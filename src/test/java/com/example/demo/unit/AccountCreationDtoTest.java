
package com.example.demo.unit;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.validator.PasswordValidator;
import com.example.demo.validator.UsernameValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountCreationDtoTest {
	
	private final List<String> VALID_USERNAMES 
		= Arrays.asList(
			"1", "kUq1", "rMCy0", "95B081", "iUz_XU", "R5iBg",
			"ad", "9594XM0DZ", "T23N4eE3", "rMCy0", "3sJ3yzT2r_V_z", 
			"jeo", "IYt9z4u", "7j7mabMO", "B9V6p2_0", "J_3ft0L0", 
			"123", "4XRhfYa1J4M", "LAGdVXf_8_4ue", "H79Gsg_1d3OYM",
			"H1_fj62Ujf2MIhv_Ot_kqr", "Hk756n1Zpvhbju4ErTC"
		);
	
	private final List<String> INVALID_USERNAMES
		= Arrays.asList(
			null, "", " ", "ä", "_", "&", "-",
			"_namebegins", "nameends_", "middle__h1",
			"N4me withspac3", "<script>", "function(",
			"ACCOUNTS;", "SELECT *", "index.html",
			"waytoolongusernameWAYTOOLONGUSERNAME"
		);
	
	private final List<String> VALID_PASSWORDS
		= Arrays.asList(
			"ojrqofu", "bramtj fgyjsc", "vdjtsnmtptvilldnepne",
			"PBCAWXU", "FT^U-QZGOMYXPQ", "UJSHE_YYISWZGLLBMUKOF",
			"3785840", "568216808412", "66426609978378513369",
			"\";).]%#+", "?+{.\\=(},\"]'", "]!#/=[@\\;=?%][?]*}#[",
			"SRlfgYPQI", "GSdaPfmnPak&h", "gNVYMZNmpDO$OkHkHNdpS", 
			"2K16P2Vm0", "KFuDRHORh8pd", "165dW9y7OjwJL363FpS50",
			"L5r94nz[.", "<F5MyW=#T\"O6U::9", "4ggG5p5e_%W73341)3kzd"
		);
	
	private final List<String> INVALID_PASSWORDS
		= Arrays.asList(
			null, "", "waytoolongpasswordsurelythisisalreadytoolongtowriteallthetime"
		);
	
	private UsernameValidator usernameValidator = new UsernameValidator();
	private PasswordValidator passwordValidator = new PasswordValidator();
	
	private final Validator validator
			= Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void validInitialsTest() {
		for (final String username : VALID_USERNAMES) {
			assertTrue(
				usernameValidator.isValid(username, null),
				"Username '" + username + "' should be valid!"
			);
		}
		
		for (final String password : VALID_PASSWORDS) {
			assertTrue(
				passwordValidator.isValid(password, null),
				"Password '" + password + "' should be valid!"
			);
		}
	}
	
	@Test
	public void invalidInitialsTest() {
		for (final String username : INVALID_USERNAMES) {
			assertFalse(
				usernameValidator.isValid(username, null),
				"Username '" + username + "' should be invalid!"
			);
		}
		
		for (final String password : INVALID_PASSWORDS) {
			assertFalse(
				passwordValidator.isValid(password, null),
				"Password '" + password + "' should be invalid!"
			);
		}
	}
	
	@Test
	public void invalidUsernameTest() {
		int nValues = Math.min(INVALID_USERNAMES.size(), VALID_PASSWORDS.size());
		
		for (int i = 0; i < nValues; ++i) {
			String username = INVALID_USERNAMES.get(i);
			String password = VALID_PASSWORDS.get(i);
			
			Set<ConstraintViolation<AccountCreationDto>> violations
				= validator.validate(new AccountCreationDto(username, password));
			
			assertFalse(
				violations.isEmpty(), 
				"Invalid username '" + username + "' should cause a validation error!"
			);
		}
	}
	
	@Test
	public void invalidPasswordTest() {
		int nValues = Math.min(VALID_USERNAMES.size(), INVALID_PASSWORDS.size());
		
		for (int i = 0; i < nValues; ++i) {
			String username = VALID_USERNAMES.get(i);
			String password = INVALID_PASSWORDS.get(i);
			
			Set<ConstraintViolation<AccountCreationDto>> violations
				= validator.validate(new AccountCreationDto(username, password));
			
			assertFalse(
				violations.isEmpty(), 
				"Invalid password '" + password + "' should cause a validation error!"
			);
		}
	}
	
	@Test
	public void validUsernameAndPasswordTest() {
		int nValues = Math.min(VALID_USERNAMES.size(), VALID_PASSWORDS.size());
		
		for (int i = 0; i < nValues; ++i) {
			String username = VALID_USERNAMES.get(i);
			String password = VALID_PASSWORDS.get(i);
			
			Set<ConstraintViolation<AccountCreationDto>> violations
				= validator.validate(new AccountCreationDto(username, password));
			
			assertTrue(
				violations.isEmpty(), 
				"Valid username '" + username + "' and " + "password '" + password + "' should not cause a validation error!"
			);
		}
		
	}
	
}
