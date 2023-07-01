package com.example.demo;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
TODO
IMPORTANT
- write tests for AccountWithRelationService
	- getAccountsRelations
	- getRelationToAccount
	- addRelationToAccount
	- removeRelationFromAccount
- write tests for RelationService
	- relationExists
	- create
		- does not create if exists?
	- removeRelation
		- removes all?
- login
	- no stack trace on invalid username


NOT SO IMPORTANT
- create common base for Role and Status
	- also implement validator for these classes
*/
@SpringBootApplication
public class DemoApplication {
	
	@PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+2"));
    }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
