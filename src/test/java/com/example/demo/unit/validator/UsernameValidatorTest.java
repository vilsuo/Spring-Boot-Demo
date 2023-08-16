
package com.example.demo.unit.validator;

import com.example.demo.validator.UsernameValidator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UsernameValidatorTest {
	
	@Autowired
	private UsernameValidator validator;
	
	private static final String ALLOWED_USERNAME_CHARACTERS = "abcdefghjiklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789";
	private static final String UNALLOWED_USERNAME_CHARACTERS = "()[]{}<>|;,:._-*'^¨~`´?+\\/&%€¤$#£\"@!§½= ";
	
	// VALID USERNAMES
	private static final String MIN_LENGTH_USERNAME = "1".repeat(UsernameValidator.USERNAME_MIN_LENGTH);
	private static final String MAX_LENGTH_USERNAME = "1".repeat(UsernameValidator.USERNAME_MAX_LENGTH);
	
	private static final List<String> VALID_WITH_UNDERSCORE = Arrays.asList(
		"no_space", "u_s_e_r_n_a_m_e", "1_2_3_456_7", "Abc_def_ABC",
		"Valid_USER_1", "V4L1D_U53R_2", "under_sc0re"
	);
	
	private static final List<String> RANDOM_VALID_USERNAMES = Arrays.asList(
		"a", "7", "C", "b1", "7d", "42", "192837465", "eL900VQeU",
		"exactly_at_the_lenght_limit123", "ALLCAPS", "alllowercase", 
		"normal_username1", "W1eRd_U5eR_n4M3", "66EKF5cvV926BtDb",
		"vJdjk6t4m39F", "T68LhFKhWqDTpLz", "Uz0D44Sj7gHS3f"
	);
	
	public static final Set<String> VALID_USERNAMES = new HashSet<>() {{
		addAll(Arrays.asList(MIN_LENGTH_USERNAME, MAX_LENGTH_USERNAME));
		addAll(VALID_WITH_UNDERSCORE);
		addAll(RANDOM_VALID_USERNAMES);
	}};
	
	// INVALID USERNAMES
	private static final String TOO_SHORT_USERNAME = MIN_LENGTH_USERNAME.substring(0, MIN_LENGTH_USERNAME.length() - 1);
	private static final String TOO_LONG_USERNAME = MAX_LENGTH_USERNAME + "1";
	
	private static final List<String> INVALID_WITH_UNDERSCORE = Arrays.asList(
		"_", "__", "_A_", "_1_A_2", "_illeg4lStart", "ill3g4lEnd_",
		"too__many", "here_too__o", "w4y____too_many", "_wr00__ng"
	);
	
	private static final List<String> RANDOM_INVALID_USERNAMES = Arrays.asList(
		null, "", " ", "ä", "_", "&", "-", "user-name",
		"_namebegins", "nameends_", "middle__h1",
		"N4me withspac3", "<script>", "function(",
		"ACCOUNTS;", "SELECT *", "index.html",
		"waytoolongusernameWAYTOOLONGUSERNAME"
	);

	public static final Set<String> INVALID_USERNAMES = new HashSet<>() {{
		addAll(Arrays.asList(null, ""));
		addAll(Arrays.asList(TOO_SHORT_USERNAME, TOO_LONG_USERNAME));
		addAll(INVALID_WITH_UNDERSCORE);
		addAll(RANDOM_INVALID_USERNAMES);
	}};
	
	@Test
	public void minLengthTest() {
		assertTrue(validator.isValid(MIN_LENGTH_USERNAME, null));
	}
	
	@Test
	public void maxLengthTest() {
		assertTrue(validator.isValid(MAX_LENGTH_USERNAME, null));
	}
	
	@Test
	public void tooShortTest() {
		assertFalse(validator.isValid(TOO_SHORT_USERNAME, null));
	}
	
	@Test
	public void tooLongTest() {
		assertFalse(validator.isValid(TOO_LONG_USERNAME, null));
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	public void notAllowedNullOrEmptyTest(String value) {
		assertFalse(validator.isValid(value, null));
	}
	
	// Does not test underscore
	@Test
	public void validCharactersTest() {
		final int length = (MIN_LENGTH_USERNAME.length() + MAX_LENGTH_USERNAME.length()) / 2;
		
		for (final char c : ALLOWED_USERNAME_CHARACTERS.toCharArray()) {
			if (c == '_') { continue; }
			
			String value = String.valueOf(c).repeat(length);
			assertTrue(
				validator.isValid(value, null),
				getShouldBeValidMessage(value)
			);
		}
	}
	
	// Tests by appending an invalid character to the end of valid username
	@Test
	public void invalidCharactersTest() {
		final String username = RANDOM_VALID_USERNAMES.get(0);
		
		assertTrue(
			username.length() + 1 <= MAX_LENGTH_USERNAME.length(),
			"Appending a value to the username would make it too long. "
			+ "Use different username in this test case"
		);
		
		for (final char c : UNALLOWED_USERNAME_CHARACTERS.toCharArray()) {
			String value = username + String.valueOf(c);
			assertFalse(
				validator.isValid(value, null),
				getShouldBeInvalidMessage(value)
			);
		}
	}
	
	@Test
	public void validUnderscoreTest() {
		for (String value : VALID_WITH_UNDERSCORE) {
			assertTrue(
				validator.isValid(value, null),
				getShouldBeValidMessage(value)
			);
		}
	}
	
	@Test
	public void invalidUnderscoreTest() {
		for (String value : INVALID_WITH_UNDERSCORE) {
			assertFalse(
				validator.isValid(value, null),
				getShouldBeInvalidMessage(value)
			);
		}
	}
	
	@Test
	public void randomValidUsernamesTest() {
		for (String value : RANDOM_VALID_USERNAMES) {
			assertTrue(
				validator.isValid(value, null),
				getShouldBeValidMessage(value)
			);
		}
	}
	
	@Test
	public void randomInvalidUsernamesTest() {
		for (String value : RANDOM_INVALID_USERNAMES) {
			assertFalse(
				validator.isValid(value, null),
				getShouldBeInvalidMessage(value)
			);
		}
	}
	
	private String getShouldBeValidMessage(String value) {
		return "Value '" + value + "' should be valid username!";
	}
	
	private String getShouldBeInvalidMessage(String value) {
		return "Value '" + value + "' should be invalid username!";
	}
}
