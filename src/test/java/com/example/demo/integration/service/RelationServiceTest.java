
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.RelationService;
import com.example.demo.service.AccountCreatorService;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationPairForAllRoleCombinationsStream;
import static com.example.demo.testhelpers.helpers.RelationCreationHelper.getRelationInfo;
import java.util.ArrayList;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class RelationServiceTest {
	
	@Autowired
	private RelationService relationService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	private Stream<Pair<Account, Account>> accountPairStream;
	
	@BeforeEach
	public void initAccountPairStream() {
		// create a Stream of Account Pairs with every possible Role combination
		accountPairStream
			= validAndUniqueAccountCreationPairForAllRoleCombinationsStream()
				.map(pairOfPairs -> {
					final Account account1 = accountCreatorService
						.create(
							pairOfPairs.getFirst().getFirst(),
							pairOfPairs.getFirst().getSecond()
						).get();

					final Account account2 = accountCreatorService
						.create(
							pairOfPairs.getSecond().getFirst(),
							pairOfPairs.getSecond().getSecond()
						).get();

					return Pair.of(account1, account2);
				});
	}
	
	@Test
	public void checkingIfRelationWithNullStatusExistThrowsTest() {
		accountPairStream.forEach(pair -> {
			assertThrows(
				IllegalArgumentException.class,
				() -> relationService.relationExists(
					pair.getFirst(), pair.getSecond(), null
				)
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationsToSelfDoesNotExistTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account sourceAndTarget = pair.getFirst();
			assertFalse(
				relationService.relationExists(
					sourceAndTarget, sourceAndTarget, status
				),
				getRelationInfo(sourceAndTarget, sourceAndTarget, status)
				+ " exists when no Relations has been created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationThatHasNotBeenCreatedDoesNotExistTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			assertFalse(
				relationService.relationExists(source, target, status),
				getRelationInfo(source, target, status)
				+ " exists when no Relations have been created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationExistsAfterItHasBeenCreatedTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status)
				.get();
			
			assertTrue(
				relationExists(relation), 
				relation + " does not exists after it was created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void afterCreatingARelationTheRelationDoesNotExistTheOtherWayTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			final Relation relation = relationService
				.create(source, target, status)
				.get();			
			
			assertFalse(
				relationService.relationExists(target, source, status), 
				getRelationInfo(target, source, status) + " exists when "
				+ relation + " has been created"
			);
			
			assertFalse(
				relationService.mutualRelationExists(source, target, status),
				"Relation exists mutually, when the Relation was created only "
				+ "one way: " + relation
			);
		});
	}
	
	@Test
	public void creatingRelationWithNullStatusThrowsTest() {
		accountPairStream.forEach(pair -> {
			assertThrows(
				IllegalArgumentException.class,
				() -> relationService.create(
					pair.getFirst(), pair.getSecond(), null
				),
				"Creating a Relation with null Status does not throw"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void optionalIsPresentWhenCreatingRelationToSelfTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account sourceAndTarget = pair.getFirst();
			
			final boolean relationWasAlreadyCreated = relationService
				.relationExists(sourceAndTarget, sourceAndTarget, status);
			
			if (!relationWasAlreadyCreated) {
				final Optional<Relation> opt = relationService
					.create(sourceAndTarget, sourceAndTarget, status);

				assertTrue(
					opt.isPresent(),
					"The Optional is not present when creating a "
					+ getRelationInfo(sourceAndTarget, sourceAndTarget, status)
				);
			}
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void optionalIsPresentAfterCreatingANewRelationTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			final Optional<Relation> opt = relationService
				.create(source, target, status);
			
			assertTrue(
				opt.isPresent(),
				"The Optional should be present when creating a "
				+ getRelationInfo(source, target, status)
				+ " that does not already exist"
			);
		});
	}
	
	@Test
	public void optionalIsPresentWhenCreatingMultipleRelationsWithDifferentStatusesTest() {
		final List<Status> createdStatuses
			= new ArrayList<>(Status.values().length);
		
		accountPairStream.forEach(pair -> {
			for(final Status status : Status.values()) {
				final Account source = pair.getFirst();
				final Account target = pair.getSecond();

				final Optional<Relation> opt = relationService
					.create(source, target, status);
				
				assertTrue(
					opt.isPresent(),
					"Optional is not present when creating a "
					+ getRelationInfo(source, target, status)
					+ " when Relations with Statuses " + createdStatuses
					+ " from the source to the target has been created"
				);
			}
			createdStatuses.clear();
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void optionalIsNotPresentWhenRecreatingExistingRelationTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			relationService.create(source, target, status);
			
			final Optional<Relation> opt = relationService
				.create(source, target, status);
			
			assertTrue(
				opt.isEmpty(),
				"The Optional should not be present when creating a "
				+ getRelationInfo(source, target, status)
				+ " for the second time"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void returnedOptionalRelationHasValuesFromParametersTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status).get();
			
			assertEquals(
				source, relation.getSource(),
				"Created Relation source Account " + relation.getSource()
				+ " is unequal to the passed in source Account " + source
			);
			assertEquals(
				target, relation.getTarget(),
				"Created Relation target Account " + relation.getTarget()
				+ " is unequal to the passed in target Account " + target
			);
			assertEquals(
				status, relation.getStatus(),
				"Created Relation Status " + relation.getStatus()
				+ " is unequal to the passed in Status " + status
			);
		});
	}
	
	@Test
	public void tryingToRemoveARelationWithNullStatusThrowsTest() {
		accountPairStream.forEach(pair -> {
			assertThrows(
				IllegalArgumentException.class,
				() -> relationService.removeRelation(
					pair.getFirst(), pair.getSecond(), null
				)
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removingARelationThatDoesNotExistDoesNotThrowTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			assertFalse(
				relationService.relationExists(source, target, status)
			);
		
			assertDoesNotThrow(
				() -> relationService.removeRelation(source, target, status),
				"The method throws when trying to remove a nonexisting "
				+ getRelationInfo(source, target, status)
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationDoesNotExistAfterRemovingItTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			final Relation relation = relationService
				.create(source, target, status)
				.get();

			assertTrue(relationExists(relation));
			removeRelation(relation);

			assertFalse(
				relationExists(relation),
				"Created " + getRelationInfo(source, target, status)
				+ " exists even after it has been removed"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removingARelationDoesNotRemoveRelationTheOtherWayTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation1 = relationService
				.create(source, target, status)
				.get();

			final Relation relation2 = relationService
				.create(target, source, status)
				.get();

			assertTrue(relationExists(relation1));
			assertTrue(relationExists(relation2));
			
			assertTrue(
				relationService.mutualRelationExists(source, target, status),
				"Relation does not exist mutually after creating the Relation "
				+ "both ways. The created Relations: " + relation1 + " and "
				+ relation2
			);

			removeRelation(relation1);

			assertTrue(
				relationExists(relation2),
				"Created " + getRelationInfo(target, source, status)
				+ " does not exists after removing "
				+ getRelationInfo(source, target, status) + " even when it has "
				+ "not been removed"
			);
			
			assertFalse(
				relationService.mutualRelationExists(source, target, status),
				"Relation still exists mutually after " + relation1 + " was "
				+ "removed"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removingARelationWithOneStatusDoesNotRemoveRelationsWithOtherStatusesTest(final Status statusToBeRemoved) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, statusToBeRemoved)
				.get();
			
			for (final Status status : Status.values()) {
				if (status != statusToBeRemoved) {
					relationService.create(source, target, status);
				}
			}
			
			removeRelation(relation);
			
			for (final Status status : Status.values()) {
				if (status != statusToBeRemoved) {
					assertTrue(
						relationService.relationExists(
							source, target, status
						),
						"Created " + getRelationInfo(source, target, status)
						+ "does not exist after removing " + relation
					);
				}
			}
		});
	}
	
	@Test
	public void newAccountIsNotTheSourceOfAnyRelationsTest() {
		accountPairStream.forEach(pair -> {
			final Account account = pair.getFirst();
			assertTrue(
				relationService.getRelationsFrom(account).isEmpty(),
				"A new created " + account + " is a source of a Relation"
			);
		});
	}
	
	@Test
	public void newAccountIsNotTheTargetOfAnyRelationsTest() {
		accountPairStream.forEach(pair -> {
			final Account account = pair.getFirst();
			assertTrue(
				relationService.getRelationsTo(account).isEmpty(),
				"A new created " + account + " is a target of a Relation"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void createdRelationCanBeFoundFromSourceRelationsTest(final Status status) {
		final Map<Account, Integer> relationCounts = new HashMap<>();
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status)
				.get();
			
			relationCounts.put(
				source, relationCounts.getOrDefault(source, 0) + 1
			);

			final List<Relation> relations = relationService
				.getRelationsFrom(source);
			
			final int actual = relationCounts.get(source);
			final int observed = relations.size();
			assertEquals(
				actual, observed,
				"Expected source Account " + source + " to have " + actual
				+ " Relation(s), when it has " + observed + " Relation(s)"
			);
			
			assertTrue(
				relations.contains(relation),
				"The created Relation " + relation + " can not be found from "
				+ "the Relations list"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void createdRelationCanBeFoundFromTargetRelationsTest(final Status status) {
		final Map<Account, Integer> relationCounts = new HashMap<>();
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status).get();
			
			relationCounts.put(
				target, relationCounts.getOrDefault(target, 0) + 1
			);
			
			final List<Relation> relations = relationService
				.getRelationsTo(target);
			
			final int actual = relationCounts.get(target);
			final int observed = relations.size();
			assertEquals(
				actual, observed,
				"Expected target Account " + target + " to have " + actual
				+ " Relation(s), when it has " + observed + " Relation(s)"
			);

			assertTrue(
				relations.contains(relation),
				"The created Relation " + relation + " can not be found from "
				+ "the Relations list"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationToListDoesNotContainRelationSourceAndRelationsFromListDoesNotContainRelationTargetTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status)
				.get();
			
			assertTrue(
				relationService.getRelationsTo(source).isEmpty(),
				"The list containing the Relations to " + source + " is not "
				+ "empty when no Relations are created with this Account as "
				+ "the target. The last created was " + relation
			);
			
			assertTrue(
				relationService.getRelationsFrom(target).isEmpty(),
				"The list containing the Relations from " + target + " is not "
				+ "empty when no Relations are created with this Account as "
				+ "the source. The last created was " + relation
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void creatingDublicateRelationsDoesNotChangeTheSourceAndTargetRelationSizeTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status)
				.get();
			
			final int relationsFrom = relationService
				.getRelationsFrom(source)
				.size();
			
			final int relationsTo = relationService
				.getRelationsTo(target)
				.size();
			
			relationService.create(source, target, status);
			
			assertEquals(
				relationsFrom, relationService.getRelationsFrom(source).size(),
				"After creating the " + relation + " for the second time, the "
				+ "source Accounts Relation count changes"
			);
			
			assertEquals(
				relationsTo, relationService.getRelationsTo(target).size(),
				"After creating the " + relation + " for the second time, the "
				+ "target Accounts Relation count changes"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationCanNotBeFoundAfterRemovingItTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status)
				.get();
			
			removeRelation(relation);
			
			assertFalse(
				relationService.getRelationsFrom(source).contains(relation),
				"Relation " + relation + " can be found from the source "
				+ "Accounts " + source + " Relation list after removal"
			);
			
			assertFalse(
				relationService.getRelationsTo(target).contains(relation),
				"Relation " + relation + " can be found from the target "
				+ "Accounts " + target + " Relation list after removal"
			);
		});
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
}
