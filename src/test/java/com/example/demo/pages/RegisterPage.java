
package com.example.demo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

@Component
public class RegisterPage extends BasePage {
	
	public RegisterPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(how = How.ID, using = "username-input")
	public WebElement usernameElement;
	
	@FindBy(how = How.ID, using = "password-input")
	public WebElement passwordElement;
	
	@FindBy(how = How.ID, using = "submit-button")
	public WebElement submitButton;
	
	public LoginPage registerAs(String username, String password) {
		// ... clever magic happens here
		usernameElement.sendKeys(username);
		passwordElement.sendKeys(password);
		
		submitButton.click();
		return new LoginPage(driver);
	}
	/*
	public RegisterPage registerAsExpectingError(String username, String password) {
        //  ... failed register here, maybe because one or both of the username 
		// and password are wrong
    }
	
	public String getErrorMessage() {
		// So we can verify that the correct error is shown
    }
	*/
}
