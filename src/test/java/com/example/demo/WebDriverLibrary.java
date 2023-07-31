
package com.example.demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverLibrary {
	
    @Bean
    public WebDriver getChromeDriver() {
        WebDriverManager.chromedriver().setup();
		//ChromeOptions options = new ChromeOptions();
		//options.addArguments("--headless");
		
        return newWebDriver();
    }
	
	public static WebDriver newWebDriver(ChromeOptions options) {
		return new ChromeDriver(options);
	}
	
	public static WebDriver newWebDriver() {
		return new ChromeDriver();
	}
}