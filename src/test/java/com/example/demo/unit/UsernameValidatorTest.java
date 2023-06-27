
package com.example.demo.unit;

import com.example.demo.validator.UsernameValidator;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class UsernameValidatorTest {
	
	private UsernameValidator validator = new UsernameValidator();
	
	@Test
	public void minLengthTest() {
		String username = "1".repeat(UsernameValidator.USERNAME_MIN_LENGTH);
		assertTrue(validator.isValid(username, null));
	}
	
	@Test
	public void maxLengthTest() {
		String username = "1".repeat(UsernameValidator.USERNAME_MIN_LENGTH);
		assertTrue(validator.isValid(username, null));
	}
	
	@Test
	public void tooLongTest() {
		String username = "1".repeat(UsernameValidator.USERNAME_MAX_LENGTH) + "1";
		assertFalse(validator.isValid(username, null));
	}
	
	@NullAndEmptySource
	public void notAllowedNullOrEmptyTest(String value) {
		assertFalse(validator.isValid(value, null));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
		"a_bcdefgh", "jiklmnopqrstu", "vwxyz", "1234567890",
		"ABCDEFGHIJ", "KLMNOPQRST", "UVWXYZ"
	})
	public void allowedCharactersTest(String value) {
		assertTrue(
			validator.isValid(value, null),
			getShouldBeValidMessage(value)
		);
	}
	
	@ParameterizedTest
	@ValueSource(
		strings = {
			"no_space", "u_s_e_r_n_a_m_e", "1_2_3_456_7", "Abc_def_ABC",
			"Valid_USER_1", "V4L1D_U53R_2", "under_sc0re"
		}
	)
	public void validUnderscoreTest(String value) {
		assertTrue(
			validator.isValid(value, null),
			getShouldBeValidMessage(value)
		);
	}
	
	@ParameterizedTest
	@ValueSource(
		strings = {
			"_", "__", "_A_", "_1_A_2", "_illeg4lStart", "ill3g4lEnd_",
			"too__many", "here_too__o", "w4y____too_many", "_wr00__ng"
		}
	)
	public void invalidUnderscoreTest(String value) {
		assertFalse(
			validator.isValid(value, null),
			getShouldBeInvalidMessage(value)
		);
	}
	
	@ParameterizedTest
	@ValueSource(
		strings = {
			"?", "&", "/", " ", ".", "<script>", "NOT\\ALLOW3D",
			"no space", "IllegälLetter", "Nöt_legal", "nånånånå",
			"DO_NOT_USE/", "user1/delete", "a\nNEWROW/tb",
			"params&methods", "what?isthis2", "no.dot.Allowed",
			"T*e6*L\\Aqr@\"+f/i", "l*@C68&Ot-S7n9", "m48_R{@:74",
			"+(C!:8P,J;7]615(\"Hd7Vz8kEq", "1LPBo!}?%4aim_cjdc"
		}
	)
	public void invalidCharactersTest(String value) {
		assertFalse(
			validator.isValid(value, null),
			getShouldBeInvalidMessage(value)
		);
	}
	
	@ParameterizedTest
	@ValueSource(
		strings = {
			"a", "1", "C", "b1", "7d", "42", "192837465", "eL900VQeU",
			"exactly_at_the_lenght_limit123", "ALLCAPS", "alllowercase", 
			"normal_username1", "W1eRd_U5eR_n4M3", "66EKF5cvV926BtDb",
			"vJdjk6t4m39F", "T68LhFKhWqDTpLz", "Uz0D44Sj7gHS3f"
		}
	)
	public void validUsernamesTest(String value) {
		assertTrue(
			validator.isValid(value, null),
			getShouldBeValidMessage(value)
		);
	}
	
	private String getShouldBeValidMessage(String value) {
		return "Value '" + value + "' should be valid username!";
	}
	
	private String getShouldBeInvalidMessage(String value) {
		return "Value '" + value + "' should be invalid username!";
	}
}
