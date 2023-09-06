
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.RelationCreatorService;
import com.example.demo.service.RelationFinderService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationPairForAllRoleCombinationsStream;
import static com.example.demo.testhelpers.helpers.RelationCreationHelper.getRelationInfo;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO
- test method getRelationsFromSourceToTarget
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class RelationFinderServiceTest {
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private RelationCreatorService relationCreatorService;
	
	@Autowired
	private RelationFinderService relationFinderService;
	
	private Stream<Pair<Account, Account>> accountPairStream;
	
	@BeforeEach
	public void initAccountPairStream() {
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
				() -> relationFinderService.relationExists(
					pair.getFirst(), pair.getSecond(), null
				)
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationsToSelfDoesNotExistOnNewAccountsTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account sourceAndTarget = pair.getFirst();
			assertFalse(
				relationFinderService.relationExists(
					sourceAndTarget, sourceAndTarget, status
				),
				getRelationInfo(sourceAndTarget, sourceAndTarget, status)
				+ " exists when no Relations has been created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationThatHasNotBeenCreatedDoesNotExistTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			assertFalse(
				relationFinderService.relationExists(source, target, status),
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
			
			final Relation relation = relationCreatorService
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
	public void creatingRelationWithOneStatusDoesNotMakeRelationsWithOtherStatusesExistTest(
			final Status createdStatus) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationCreatorService
				.create(source, target, createdStatus)
				.get();
			
			for (final Status notCreatedStatus : Status.values()) {
				if (notCreatedStatus != createdStatus) {
					assertFalse(
						relationFinderService
							.relationExists(source, target, notCreatedStatus), 
						getRelationInfo(source, target, notCreatedStatus)
						+ " exists when relation " + relation + " has been "
						+ "created"
					);
				}
			}
		});
	}
	
	@CartesianTest
	public void onlyIfRelationHasBeenCreatedAtleastOneWayThenItExistsAtleastOneWayTest(
		@CartesianTest.Values(booleans = {false, true})
			boolean createFromSource,
		@CartesianTest.Values(booleans = {false, true})
			boolean createFromTarget,
		@CartesianTest.Enum Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			if (createFromSource) {
				relationCreatorService.create(source, target, status);
			}
			
			if (createFromTarget) {
				relationCreatorService.create(target, source, status);
			}
			
			final boolean relationShouldExistsAtleastOneWay
				= (createFromSource || createFromTarget);
			
			final boolean relationExistsAtleastOneWay = relationFinderService
				.relationExistsAtleastOneWay(source, target, status);
			
			assertEquals(
				relationShouldExistsAtleastOneWay, relationExistsAtleastOneWay,
				"Relation does " + (relationExistsAtleastOneWay ? "" : "not ")
				+ "exist at least one way  when "
				+ getRelationInfo(source, target, status)
				+ " has " + (createFromSource ? "" : "not ") + "been created"
				+ " and when " + getRelationInfo(target, source, status)
				+ " has " + (createFromTarget ? "" : "not ") + "been created"
			);
		});
	}
	
	@CartesianTest
	public void onlyIfRelationHasBeenCreatedBothWaysThenItExistsBothWaysTest(
		@CartesianTest.Values(booleans = {false, true})
			boolean createFromSource,
		@CartesianTest.Values(booleans = {false, true})
			boolean createFromTarget,
		@CartesianTest.Enum Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			if (createFromSource) {
				relationCreatorService.create(source, target, status);
			}
			
			if (createFromTarget) {
				relationCreatorService.create(target, source, status);
			}
			
			final boolean relationShouldExistsBothWays
				= (createFromSource && createFromTarget);
			
			final boolean relationExistsBothWays = relationFinderService
				.relationExistsBothWays(source, target, status);
			
			assertEquals(
				relationShouldExistsBothWays, relationExistsBothWays,
				"Relation does " + (relationExistsBothWays ? "" : "not ")
				+ "exist both ways when "
				+ getRelationInfo(source, target, status)
				+ " has " + (createFromSource ? "" : "not ") + "been created"
				+ " and when " + getRelationInfo(target, source, status)
				+ " has " + (createFromTarget ? "" : "not ") + "been created"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void afterCreatingARelationTheRelationDoesNotExistTheOtherWayTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			final Relation relation = relationCreatorService
				.create(source, target, status)
				.get();			
			
			assertFalse(
				relationFinderService.relationExists(target, source, status), 
				getRelationInfo(target, source, status) + " exists when "
				+ relation + " has been created"
			);
		});
	}
	
	@Test
	public void newAccountIsNotTheSourceOfAnyRelationsTest() {
		accountPairStream.forEach(pair -> {
			final Account account = pair.getFirst();
			assertTrue(
				relationFinderService.getRelationsFrom(account).isEmpty(),
				"A new created " + account + " is a source of a Relation"
			);
		});
	}
	
	@Test
	public void newAccountIsNotTheTargetOfAnyRelationsTest() {
		accountPairStream.forEach(pair -> {
			final Account account = pair.getFirst();
			assertTrue(
				relationFinderService.getRelationsTo(account).isEmpty(),
				"A new created " + account + " is a target of a Relation"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void createdRelationCanBeFoundFromSourceRelationsTest(
			final Status status) {
		
		final Map<Account, Integer> relationCounts = new HashMap<>();
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationCreatorService
				.create(source, target, status)
				.get();
			
			relationCounts.put(
				source, relationCounts.getOrDefault(source, 0) + 1
			);

			final List<Relation> relations = relationFinderService
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
	public void createdRelationCanBeFoundFromTargetRelationsTest(
			final Status status) {
		
		final Map<Account, Integer> relationCounts = new HashMap<>();
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationCreatorService
				.create(source, target, status).get();
			
			relationCounts.put(
				target, relationCounts.getOrDefault(target, 0) + 1
			);
			
			final List<Relation> relations = relationFinderService
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
	public void relationToListDoesNotContainRelationSourceAndRelationsFromListDoesNotContainRelationTargetTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationCreatorService
				.create(source, target, status)
				.get();
			
			assertTrue(
				relationFinderService.getRelationsTo(source).isEmpty(),
				"The list containing the Relations to " + source + " is not "
				+ "empty when no Relations are created with this Account as "
				+ "the target. The last created was " + relation
			);
			
			assertTrue(
				relationFinderService.getRelationsFrom(target).isEmpty(),
				"The list containing the Relations from " + target + " is not "
				+ "empty when no Relations are created with this Account as "
				+ "the source. The last created was " + relation
			);
		});
	}
	
	private boolean relationExists(final Relation relation) {
		return relationFinderService.relationExists(
			relation.getSource(), relation.getTarget(), relation.getStatus()
		);
	}
}
