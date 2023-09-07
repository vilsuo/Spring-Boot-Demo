
package com.example.demo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

@Component
public class LoginPage {//extends BasePage<LoginPage> {
	
	/*
	public LoginPage(WebDriver driver) {
		super(driver, "/login");
	}
	*/
	/*
	@FindBy(how = How.ID, using = "username-input")
	public WebElement usernameElement;
	
	@FindBy(how = How.ID, using = "password-input")
	public WebElement passwordElement;
	
	@FindBy(how = How.ID, using = "submit-button")
	public WebElement submitButton;
	
	@FindBy(how = How.ID, using = "register-link")
	public WebElement registerLink;
	
	// change return value to HomePage
	public void loginAs(String username, String password) {
        // ... clever magic happens here
		usernameElement.sendKeys(username);
		passwordElement.sendKeys(password);
		
		submitButton.click();
    }
    */
	/*
    public LoginPage loginAsExpectingError(String username, String password) {
        //  ... failed login here, maybe because one or both of the username 
		// and password are wrong
    }
    
    public String getErrorMessage() {
		// So we can verify that the correct error is shown
    }
	
	public void followRegisterLink() {
		registerLink.click();
	}
	*/
}
