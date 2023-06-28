package com.example.demo;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
TODO
- login
	- no stack trace on invalid username
*/
@SpringBootApplication
public class DemoApplication {
	
	@PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
