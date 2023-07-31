
package com.example.demo.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

public abstract class BasePage<T extends LoadableComponent<T> >
		extends LoadableComponent<T> {
	
	@Autowired
	protected WebDriver driver;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt;
	
	protected String url;
	
	public BasePage(WebDriver driver, String url) {
		this.driver = driver;
		Integer port = webServerAppCtxt.getWebServer().getPort();
		System.out.println("port: " + port);
		this.url = "http://localhost:" + port + url;
		
		// This call sets the WebElement fields
		PageFactory.initElements(driver, this);
	}
	
	/*
	The method contains the code that is executed to navigate to the page
	*/
	@Override
	protected void load() {
		System.out.println("load()");
		driver.get(url);
	}
	
	/*
	The method is used to evaluate whether we are on the correct page and 
	whether page loading has finished successfully
	*/
	@Override
	protected void isLoaded() throws Error {
		System.out.println("isLoaded()");
		final String currentUrl = driver.getCurrentUrl();
		assertEquals(
			url, currentUrl,
			"Url is supposed to be " + url + ", but it was " + currentUrl
		);
	}
}
