
package com.example.demo.integration.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.AccountRelationService;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
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
- test remove relation
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
	
	private final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	
	private final String username1 = VALID_USERNAMES.get(0);
	private final String username2 = VALID_USERNAMES.get(1);
	private final String username3 = VALID_USERNAMES.get(2);
	private final String unsedUsername = VALID_USERNAMES.get(3);
	
	private final String password1 = VALID_PASSWORDS.get(0);
	private final String password2 = VALID_PASSWORDS.get(1);
	
	@BeforeEach
	public void init() {
		accountCreatorService.create(new AccountCreationDto(username1, password1), Role.USER);
		accountCreatorService.create(new AccountCreationDto(username2, password1), Role.USER);
		accountCreatorService.create(new AccountCreationDto(username3, password2), Role.USER);
	}
	
	/*
	@Test
	public void a() {
		
	}
	
	@Test
	public void createRelationTest() {
		assertTrue(accountRelationService.getAccountRelationDtos(username1).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username2).isEmpty());
		
		accountRelationService.createRelationToAccount(username1, username2, Status.FRIEND);
		
		assertEquals(1, accountRelationService.getAccountRelationDtos(username1).size());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertEquals(1, accountRelationService.getRelationDtosToAccount(username2).size());
		
		accountRelationService.createRelationToAccount(username1, username2, Status.BLOCKED);
		
		assertEquals(2, accountRelationService.getAccountRelationDtos(username1).size());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertEquals(2, accountRelationService.getRelationDtosToAccount(username2).size());
		
		accountRelationService.createRelationToAccount(username2, username1, Status.FRIEND);
		
		assertEquals(2, accountRelationService.getAccountRelationDtos(username1).size());
		assertEquals(1, accountRelationService.getRelationDtosToAccount(username1).size());
		assertEquals(1, accountRelationService.getAccountRelationDtos(username2).size());
		assertEquals(2, accountRelationService.getRelationDtosToAccount(username2).size());
	}
	
	@Test
	public void dublicateRelationDoesNotGetAddedTest() {
		assertTrue(accountRelationService.getAccountRelationDtos(username1).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username2).isEmpty());
		
		Optional<RelationDto> opt1 = accountRelationService.createRelationToAccount(username1, username2, Status.FRIEND);
		Optional<RelationDto> opt2 = accountRelationService.createRelationToAccount(username1, username2, Status.FRIEND);
		assertTrue(opt1.isPresent());
		assertTrue(opt2.isEmpty(), "When creating a dublicate relation, the create method does not return empty optional");
		
		assertEquals(1, accountRelationService.getAccountRelationDtos(username1).size());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertEquals(1, accountRelationService.getRelationDtosToAccount(username2).size());
	}
	
	
	@Test
	public void getRelationsAndHasRelationTestTest() {
		assertTrue(accountRelationService.getAccountRelationDtos(username1).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username2).isEmpty());
		
		Optional<RelationDto> opt1
			= accountRelationService.createRelationToAccount(username1, username2, Status.FRIEND);
		
		RelationDto relationDtoFrom
			= accountRelationService.getAccountRelationDtos(username1).get(0);
		
		assertEquals(
			username1, opt1.get().getSource().getUsername(),
			"Created relation source username does not match the source username parameter it was created with"
		);
		assertEquals(
			username2, opt1.get().getTarget().getUsername(),
			"Created relation target username does not match the target username parameter it was created with"
		);
		assertEquals(
			Status.FRIEND, opt1.get().getStatus(),
			"Created relation status does not match the status parameter it was created with"
		);
		assertEquals(
			opt1.get(), relationDtoFrom,
			"When creating relation from an account and then after getting that accounts relations, the relations do not match"
		);
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(
			accountRelationService.hasRelationStatus(username1, username2, Status.FRIEND),
			"The created relation can not be found"
		);
		assertFalse(
			accountRelationService.hasRelationStatus(username1, username2, Status.BLOCKED),
			"Wrong kind of relation can be found"
		);
		
		RelationDto relationDtoTo
			= accountRelationService.getRelationDtosToAccount(username2).get(0);
		
		assertEquals(
			username1, relationDtoTo.getSource().getUsername(),
			"Relations to account source username does not match the source username parameter it was created with"
		);
		assertEquals(
			username2, relationDtoTo.getTarget().getUsername(),
			"Relations to account target username does not match the target username parameter it was created with"
		);
		assertEquals(
			Status.FRIEND, relationDtoTo.getStatus(),
			"Relations to account relations status does not match the status parameter it was created with"
		);
		assertEquals(
			opt1.get(), relationDtoTo,
			"When creating relation to an account and then after getting relations to that accounts, the relations do not match"
		);
		assertTrue(
			accountRelationService.getRelationDtosToAccount(username1).isEmpty(),
			"After creating a relation, the source account should not get any relations added to it from other accounts"
		);
		assertFalse(
			accountRelationService.hasRelationStatus(username2, username1, Status.FRIEND),
			"Relation from target to source can be found after creating the relation from source to target"
		);
		
		Optional<RelationDto> opt2 =
			accountRelationService.createRelationToAccount(username1, username2, Status.BLOCKED);
		
		assertTrue(
			accountRelationService.getAccountRelationDtos(username1).contains(opt1.get()),
			"After creating a second relation from an account, the first one can not be found"
		);
		assertTrue(
			accountRelationService.getAccountRelationDtos(username1).contains(opt2.get()),
			"After creating a second relation from an account, the second one can not be found"
		);
		assertTrue(
			accountRelationService.getAccountRelationDtos(username1).contains(opt1.get()),
			"After creating a second relation to an account, the first one can not be found"
		);
		assertTrue(
			accountRelationService.getRelationDtosToAccount(username2).contains(opt2.get()),
			"After creating a second relation to an account, the second one can not be found"
		);
		
		assertTrue(accountRelationService.hasRelationStatus(username1, username2, Status.FRIEND));
		assertTrue(accountRelationService.hasRelationStatus(username1, username2, Status.BLOCKED));
	}
	
	@Test
	public void removeRelationTest() {
		// test also relation exists in this method after removeing a relation
		
	}
	
	@Test
	public void removingNonExistingRelationDoesNothing() {
		assertTrue(accountRelationService.getAccountRelationDtos(username1).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username2).isEmpty());
		
		accountRelationService.removeRelationFromAccount(username1, username2, Status.FRIEND);
		
		assertTrue(accountRelationService.getAccountRelationDtos(username1).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username1).isEmpty());
		assertTrue(accountRelationService.getAccountRelationDtos(username2).isEmpty());
		assertTrue(accountRelationService.getRelationDtosToAccount(username2).isEmpty());
	}
	
	*/
}