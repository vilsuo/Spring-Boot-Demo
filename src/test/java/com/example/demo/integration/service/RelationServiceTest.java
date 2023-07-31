
package com.example.demo.integration.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.service.RelationService;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO
test methods in this class
	- getRelationsFrom
	- getRelationsTo

implement tests for class RelationDtoService in RelationDtoServiceTest class
- test only methods that map Relation to RelationDto
	(assume this is only thing that happens)

*/

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class RelationServiceTest {
	
	@Autowired
	private RelationService relationService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	private final List<String> USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	
	private Account account1;
	private Account account2;
	private Account account3;
	
	@BeforeEach
	public void init() {
		account1 = accountCreatorService.create(
			new AccountCreationDto(
				USERNAMES.get(0), PASSWORDS.get(0)), Role.ADMIN
		).get();
		
		assertNotNull(account1, "First created Account is null");
		
		account2 = accountCreatorService.create(
			new AccountCreationDto(
				USERNAMES.get(1), PASSWORDS.get(1)), Role.USER
		).get();
		assertNotNull(account2, "Second created Account is null");
		
		account3 = accountCreatorService.create(
			new AccountCreationDto(
				USERNAMES.get(2), PASSWORDS.get(1)), Role.USER
		).get();
		assertNotNull(account3, "Third created Account is null");
	}
	
	@Test
	public void relationExistsNullStatusThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> relationService.relationExists(
				account1, account2, null
			),
			"Checking if a Relation with a Null Status exists does not throw "
			+ "an exception"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationExistsNonExistentRelationReturnsFalseTest(final Status status) {
		assertFalse(
			relationService.relationExists(account1, account2, status),
			"Relation exists with Status " + status.getName() + " when no "
			+ "Relations are created"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationExistsWhenRelationHasBeenCreatedTest(final Status status) {
		relationService.create(account1, account2, status);
		
		assertTrue(
			relationService.relationExists(account1, account2, status), 
			"Relation with Status " + status.getName() + " can not be found "
			+ "after it was created"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationExistsWhenRelationIsCreatedItIsNotAddedOtherWayTest(final Status status) {
		relationService.create(account1, account2, status);
		
		assertFalse(
			relationService.relationExists(account2, account1, status), 
			"Relation with Status " + status.getName() + " from target Account "
			+ "to source Account can be found when it was created from source "
			+ "Account to target Account"
		);
	}
	
	@Test
	public void createThrowsWithNullStatus() {
		assertThrows(
			IllegalArgumentException.class,
			() -> relationService.create(account1, account2, null),
			"Creating a Relation with null Status does not throw"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void createCanCreateRelationsToSelfTest(final Status status) {
		assertTrue(
			relationService.create(account1, account1, status).isPresent(),
			"The Optional is not present when creating a Relation from Account "
			+ "to itself with Status "+ status.getName()
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void createOptionalIsPresentWhenCreatingNonExistingRelationTest(final Status status) {
		assertTrue(
			relationService.create(account1, account2, status).isPresent(),
			"The Optional should be present when creating a Relation that does "
			+ "not exist with Status " + status.getName()
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void createOptionalIsNotPresentWhenCreatingExistingRelationTest(final Status status) {
		relationService.create(account1, account2, status);
				
		assertTrue(
			relationService.create(account1, account2, status).isEmpty(),
			"The Optional should be not present when creating a Relation that "
			+ "does exist with Status not" + status.getName()
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void createReturnedOptionalRelationIsCreatedFromParametersTest(final Status status) {
		Relation relation
			= relationService.create(account1, account2, status).get();
		
		assertEquals(
			account1, relation.getSource(),
			"Created Relation source Account differs from the passed in source "
			+ "Account"
		);
		assertEquals(
			account2, relation.getTarget(),
			"Created Relation target Account differs from the passed in target "
			+ "Account"
		);
		assertEquals(
			status, relation.getStatus(),
			"Created Relation Status " + relation.getStatus().getName()
			+ " differs from the passed in Status " + status.getName()
		);
	}
	
	@Test
	public void removeRelationNullStatusThrowsTest() {
		assertThrows(
			IllegalArgumentException.class,
			() -> relationService.removeRelation(account1, account2, null),
			"Trying to remove a Relation with null Status does not throw"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removeRelationRemovingANonExistingRelationDoesNotThrowTest(final Status status) {
		assertFalse(relationService.relationExists(account1, account2, status));
		
		assertDoesNotThrow(
			() -> relationService.removeRelation(account1, account2, status),
			"The method throws when trying to remove a Relation that does not "
			+ "exist"
		);
	}
	
	private boolean relationExists(final Relation relation) {
		return relationService.relationExists(
			relation.getSource(), relation.getTarget(), relation.getStatus()
		);
	}
	
	private void removeRelation(final Relation relation) {
		relationService.removeRelation(
			relation.getSource(), relation.getTarget(), relation.getStatus()
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removeRelationRelationDoesNotExistAfterRemovingTest(final Status status) {
		Relation relation
			= relationService.create(account1, account2, status).get();
		
		assertTrue(relationExists(relation));
		
		removeRelation(relation);
		
		assertFalse(
			relationExists(relation),
			"Relation with Status " + status.getName() + " exists even after "
			+ "it was removed"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removeRelationDoesNotRemoveRelationFromTargetToSourceTest(final Status status) {
		Relation relation1
			= relationService.create(account1, account2, status).get();
		
		Relation relation2
			= relationService.create(account2, account1, status).get();
		
		assertTrue(relationExists(relation1));
		assertTrue(relationExists(relation2));
		
		removeRelation(relation1);
		
		assertTrue(
			relationExists(relation2),
			"The other way of a bidirectional Relation was removed after "
			+ "removing the Relation one way"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removeRelationRemovingARelationWithOneStatusDoesNotRemoveRelationsWithOtherStatusesTest(final Status status) {
		Relation relationToBeRemoved
			= relationService.create(account1, account2, status).get();
		
		List<Relation> relationList = new ArrayList<>();
		for (final Status statusFill : Status.values()) {
			relationService.create(
					relationToBeRemoved.getSource(),
					relationToBeRemoved.getTarget(), 
					statusFill
			).ifPresent(
				(relation) -> relationList.add(relation)
			);
		}
		
		removeRelation(relationToBeRemoved);
		
		relationList.stream().forEach(
			(relation) -> {
				assertTrue(
					relationExists(relation),
					"After removing a Relation with Status "
					+ relationToBeRemoved.getStatus().getName() + " a Relation "
					+ "with Status " + relation.getStatus().getName() + " was "
					+ "also removed"
				);
			}
		);
	}
}
