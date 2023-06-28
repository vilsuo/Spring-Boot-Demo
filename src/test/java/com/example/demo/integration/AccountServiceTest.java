
package com.example.demo.integration;

import com.example.demo.service.AccountService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountServiceTest {
	
	@Autowired
	private AccountService accountService;
	
	@Test
	public void t() {
		/*
		String username = "testAccount";
		String password = "testPassword";
		
		assertFalse(accountService.existsByUsername(username));
		
		boolean wasCreated
			= accountService.createUSER(new AccountDto(username, password));
		
		assertTrue(accountService.existsByUsername(username));
		*/
	}
	
}
