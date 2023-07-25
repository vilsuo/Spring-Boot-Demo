
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
        return new ChromeDriver();
    }
	
	/*
	@Bean
	public WebDriver getChromeDriver() {
		ChromeOptions options = new ChromeOptions();
		//options.addArguments("--headless");
        return new ChromeDriver(options);
	}
	*/
}