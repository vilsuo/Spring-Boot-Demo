
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.RelationService;
import com.example.demo.service.AccountCreatorService;
import static com.example.demo.testhelpers.helpers.RelationCreationHelper.accountCreationPairWithAllRoleCombinationsStream;
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

/*
TODO
- rewrite error messages
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
	
	private Stream<Pair<Account, Account>> accountPairStream;
	
	@BeforeEach
	public void initAccountPairStream() {
		// create a Stream of Account Pair with every possible Role combination
		accountPairStream = accountCreationPairWithAllRoleCombinationsStream()
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
		/*
		// 1) create a Account Pair for each Role
		final List<Pair<Account, Account>> accountPairList = StreamUtils
			.zipWithIndex(accountCreationDtoPairStream())
			.limit(TOTAL_ROLES)
			.map(indexed -> {
				final Role role = Role.values()[(int) indexed.getIndex()];
				
				final Account account1 = accountCreatorService
					.create(indexed.getValue().getFirst(), role).get();
				
				final Account account2 = accountCreatorService
					.create(indexed.getValue().getSecond(), role).get();
				
				return Pair.of(account1, account2);
			}).toList();
		
		// 2) create Account Pairs with all possible combinations of Roles
		List<Pair<Account, Account>> lst = new ArrayList<>();
		for (int x = 0; x < accountPairList.size(); ++x) {
			for (int y = 0; y < accountPairList.size(); ++y) {
				lst.add(
					Pair.of(
						accountPairList.get(x).getFirst(), 
						accountPairList.get(y).getSecond()
					)
				);
			}
		}
		
		assertEquals(TOTAL_ROLES * TOTAL_ROLES, lst.size());
		accountPairStream = lst.stream();
		*/
	}
	
	@Test
	public void checkingIfRelationWithNullStatusExistThrowsTest() {
		accountPairStream.forEach(pair -> {
			assertThrows(
				IllegalArgumentException.class,
				() -> relationService.relationExists(
					pair.getFirst(), pair.getSecond(), null
				),
				"Checking if a Relation with a Null Status exists does not "
				+ "throw"
			);
		});
	}
	
	// has dublicate checks
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationsToSelfDoesNotExistTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account sourceAndTarget = pair.getFirst();
			
			assertFalse(
				relationService.relationExists(
					sourceAndTarget, sourceAndTarget, status
				),
				sourceAndTarget + " has Relation with Status '"
				+ status.getName()+ "' with itself when no Relations has been "
				+ "created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationThatHasNotBeenCreatedDoesNotExistTest(final Status status) {
		accountPairStream.forEach(pair -> {
			assertFalse(
				relationService.relationExists(
					pair.getFirst(), pair.getSecond(), status
				),
				"Relation exists with Status " + status.getName() + " when no "
				+ "Relations are created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationExistsAfterItHasBeenCreatedTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			relationService.create(source, target, status);
			
			assertTrue(
				relationService.relationExists(source, target, status), 
				"Relation with Status " + status.getName() + " does not exist "
				+ "after it was created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void afterCreatingARelationTheRelationDoesNotExistTheOtherWayTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			relationService.create(source, target, status);
			
			assertFalse(
				relationService.relationExists(target, source, status), 
				"Relation with Status " + status.getName() + " from target "
				+ "Account to source Account can be found when it was created "
				+ "from source Account to target Account"
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
					"The Optional is not present when creating a Relation from "
					+ "Account to itself with Status "+ status.getName()
				);
			}
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void optionalIsPresentAfterCreatingANewRelationTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Optional<Relation> opt = relationService
				.create(pair.getFirst(), pair.getSecond(), status);
			
			assertTrue(
				opt.isPresent(),
				"The Optional should be present when creating a Relation that"
				+ " does not exist with Status " + status.getName()
			);
		});
	}
	
	@Test
	public void optionalIsPresenWhenCreatingMultipleRelationsWithDifferentStatusesTest() {
		accountPairStream.forEach(pair -> {
			for(final Status status : Status.values()) {
				final Account source = pair.getFirst();
				final Account target = pair.getSecond();

				final Optional<Relation> opt = relationService
					.create(source, target, status);
				
				assertTrue(
					opt.isPresent(),
					"Optional is not presen when creating relation with "
					+ "Status '" + status.getName() + "'"
				);
			}
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
				"The Optional should be not present when creating a Relation "
				+ "that does exist with Status not" + status.getName()
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
				"Created Relation source Account is unequal to the passed in "
				+ "source Account"
			);
			assertEquals(
				target, relation.getTarget(),
				"Created Relation target Account is unequal to the passed in "
				+ "target Account"
			);
			assertEquals(
				status, relation.getStatus(),
				"Created Relation Status " + relation.getStatus().getName()
				+ " is unqual to the passed in Status " + status.getName()
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
				),
				"Trying to remove a Relation with null Status does not throw"
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
				"The method throws when trying to remove a Relation that does "
				+ "not exist"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationDoesNotExistAfterRemovingItTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Relation relation = relationService
				.create(pair.getFirst(), pair.getSecond(), status).get();

			assertTrue(relationExists(relation));
			removeRelation(relation);

			assertFalse(
				relationExists(relation),
				"Relation with Status " + status.getName() + " exists even "
				+ "after it was removed"
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
				.create(source, target, status).get();

			final Relation relation2 = relationService
				.create(target, source, status).get();

			assertTrue(relationExists(relation1));
			assertTrue(relationExists(relation2));

			removeRelation(relation1);

			assertTrue(
				relationExists(relation2),
				"The other way of a bidirectional Relation was removed after "
				+ "removing the Relation one way"
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
					.create(source, target, statusToBeRemoved).get();
			
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
						"Relation with source Account " + source + " target "
						+ "Account " + target + " and Status " + status
						+ "does not exist after removing Relation with Status "
						+ statusToBeRemoved
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
				account + " is a source of a Relation initially"
			);
		});
	}
	
	@Test
	public void newAccountIsNotTheTargetOfAnyRelationsTest() {
		accountPairStream.forEach(pair -> {
			final Account account = pair.getFirst();
			assertTrue(
				relationService.getRelationsTo(account).isEmpty(),
				account + " is a target of a Relation initially"
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
				.create(source, target, status).get();
			
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
	public void relationToListDoesNotContainRelationSourceAndRelationsFromListDoesNotContainTargetTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationService
				.create(source, target, status).get();
			
			assertTrue(
				relationService.getRelationsTo(source).isEmpty(),
				"The list containing the Relations to the Relations " + relation
				+ " source Account is not empty when no such Relations are "
				+ "created where the source Account is the target"
			);
			
			assertTrue(
				relationService.getRelationsFrom(target).isEmpty(),
				"The list containing the Relations from the Relations "
				+ relation + " target Account is not empty when no such "
				+ "Relations are created where the target Account is the source"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void dublicateSourceRelationsAreNotAddedTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			relationService.create(source, target, status);
			final int relations1 = relationService
				.getRelationsFrom(source).size();
			
			relationService.create(source, target, status);
			final int relations2 = relationService
					.getRelationsFrom(source).size();

			assertEquals(
				relations1, relations2,
				"After creating the Relation for the second time with source "
				+ "Account " + source + " target Account " + target + " and "
				+ "Status " + status + ", the source Accounts Relation count "
				+ "changes"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void dublicateTargetRelationsAreNotAddedTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			relationService.create(source, target, status);
			final int relations1 = relationService
				.getRelationsTo(target).size();
			
			relationService.create(source, target, status);
			final int relations2 = relationService
					.getRelationsTo(target).size();

			assertEquals(
				relations1, relations2,
				"After creating the Relation for the second time with source "
				+ "Account " + source + " target Account " + target + " and "
				+ "Status " + status + ", the target Accounts Relation count "
				+ "changes"
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
				.create(source, target, status).get();
			
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
