
package com.example.demo.system;

import com.example.demo.WebDriverLibrary;
import com.example.demo.pages.LoginPage;
import com.example.demo.pages.RegisterPage;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
	
	/*
	@Autowired
	private LoginPage loginPage;
	*/
	
    @LocalServerPort
    private Integer port;

	@Autowired
	private WebDriver driver;

	/*
    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }
	*/
	
	@BeforeEach
    void setupTest() {
		System.out.println("setupTest()");
        //driver = new ChromeDriver();
		driver = WebDriverLibrary.newWebDriver();
    }
	
	@AfterEach
    void teardown() {
		System.out.println("teardown()");
        if (driver != null) {
            driver.quit();
        }
    }
	
	/*
	@Test
    public void redirectsToLoginPageAfterRegisteringWithValidUsernameAndPasswordTest() {
		//LoginPage loginPage = registerPage.registerAs(validUsername1, validPassword1);
    }
	*/
	
}
