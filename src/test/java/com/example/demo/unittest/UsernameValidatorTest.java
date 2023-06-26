
package com.example.demo.unittest;

import com.example.demo.validator.UsernameValidator;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class UsernameValidatorTest {
	
	private UsernameValidator validator = new UsernameValidator();
	
	@ParameterizedTest
	@ValueSource(
		strings = {
			"no_space", "u_s_e_r_n_a_m_e", "1_2_3_456_7", "Abc_def_ABC",
			"Valid_USER_1", "V4L1D_U53R_2", "under_sc0re"
		})
	public void validUnderscoreTest(String value) {
		assertTrue(validator.isValid(value, null));
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(
		strings = {
			"_", "__", "_A_", "_1_A_2", "_illeg4lStart", "ill3g4lEnd_",
			"too__many", "here_too__o", "w4y____too_many", "_wr00__ng"
		})
	public void invalidUnderscoreTest(String value) {
		assertFalse(validator.isValid(value, null));
	}
	
	@ParameterizedTest
	@ValueSource(
		strings = {
			"?", "&", "/", " ", ".",
			"no space", "IllegälLetter", "Nöt_legal", "nånånånå",
			"DO_NOT_USE/", "user1/delete", "a/nNEWROW/tb",
			"params&methods", "what?isthis2", "no.dot.Allowed",
		})
	public void invalidCharactersTest(String value) {
		assertFalse(validator.isValid(value, null));
	}
	
	@ParameterizedTest
	@ValueSource(
		strings = { 
			"thisUsernameIsOverThirtyCharactersLong",
			"exactlyOverTheLimitOf30aaaaaaaa",
		})
	public void InvalidUsernamesTest(String value) {
		assertFalse(validator.isValid(value, null));
	}
	
	@ParameterizedTest
	@ValueSource(
		strings = {
			"a", "1", "C", "b1", "7d", "42", // min lenght is 1
			"exactly_at_the_lenght_limit123", // max length is 30
			"ALLCAPS", "alllowercase", "192837465",
			"normal_username1", "W1eRd_U5eR_n4M3"
		}
	)
	public void validUsernamesTest(String value) {
		assertTrue(validator.isValid(value, null));
	}
}
