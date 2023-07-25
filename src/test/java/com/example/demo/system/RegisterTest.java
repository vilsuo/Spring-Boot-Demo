
package com.example.demo.system;

import com.example.demo.pages.RegisterPage;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterTest {
	
	private final String validUsername1 = UsernameValidatorTest.VALID_USERNAMES.get(0);
	private final String validUsername2 = UsernameValidatorTest.VALID_USERNAMES.get(1);
	
	private final String validPassword1 = PasswordValidatorTest.VALID_PASSWORDS.get(0);
	private final String validPassword2 = PasswordValidatorTest.VALID_PASSWORDS.get(1);
	
	private final String invalidUsername = UsernameValidatorTest.INVALID_USERNAMES.get(0);
	private final String invalidPassword = PasswordValidatorTest.INVALID_PASSWORDS.get(0);
	
	@Autowired
	private RegisterPage registerPage;
	
    @LocalServerPort
    private Integer port;
	
	/*
	private static String registerUrl;
	private static String loginUrl;
	
	@BeforeAll
	public static void setUp() {
		registerUrl = "http://localhost:" + port + "/register";
		loginUrl = "http://localhost:" + port + "/login";
	}
	*/
	
	@Test
    public void redirectsToLoginPageAfterRegisteringWithValidUsernameAndPasswordTest() {
		registerPage.navigate("http://localhost:" + port + "/register");
		
		registerPage.registerAs(validUsername1, validPassword1);
    }
	
}
