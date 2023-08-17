
package com.example.demo.integration.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.AccountRelationService;
import com.example.demo.unit.validator.PasswordValidatorTest;
import com.example.demo.unit.validator.UsernameValidatorTest;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO
- test all methods
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccountRelationServiceTest {

	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private AccountRelationService accountRelationService;
	
	@BeforeEach
	public void init() {
		
	}
	
	@Test
	public void dublicateRelationDoesNotGetAddedTest() {
	
	}
	
	@Test
	public void removingNonExistingRelationDoesNothing() {
	
	}
}