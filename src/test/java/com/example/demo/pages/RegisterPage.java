
package com.example.demo.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterPage {/*extends LoadableComponent<RegisterPage> {
	
	@Autowired
	private WebDriver driver;
	
	public RegisterPage(WebDriver driver) {
		
	}
	
	@FindBy(how = How.ID, using = "username-input")
	public WebElement usernameElement;
	
	@FindBy(how = How.ID, using = "password-input")
	public WebElement passwordElement;
	
	@FindBy(how = How.ID, using = "submit-button")
	public WebElement submitButton;
	
	//The method contains the code that is executed to navigate to the page
	@Override
	protected void load() {
		System.out.println("load()");
		//driver.get(url);
	}
	
	//The method is used to evaluate whether we are on the correct page and 
	//whether page loading has finished successfully
	@Override
	protected void isLoaded() throws Error {
		System.out.println("isLoaded()");
		final String currentUrl = driver.getCurrentUrl();
	
		//assertEquals(
		//	url, currentUrl,
		//	"Url is supposed to be " + url + ", but it was " + currentUrl
		//);
	}
	/*
	public LoginPage registerAs(String username, String password) {
		usernameElement.sendKeys(username);
		passwordElement.sendKeys(password);
		
		submitButton.click();
		return new LoginPage(driver);
	}
	*/
	
	/*
	public RegisterPage registerAsExpectingError(String username, String password) {
    }
	
	public String getErrorMessage() {
    }
	*/
}
