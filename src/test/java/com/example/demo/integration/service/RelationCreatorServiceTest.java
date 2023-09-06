
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.RelationCreatorService;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.RelationFinderService;
import jakarta.transaction.Transactional;
import java.util.List;
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

/*
TODO
- test transitive relation removal
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class RelationCreatorServiceTest {
	
	@Autowired
	private RelationCreatorService relationCreatorService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
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
	public void creatingRelationWithNullStatusThrowsTest() {
		accountPairStream.forEach(pair -> {
			assertThrows(
				IllegalArgumentException.class,
				() -> relationCreatorService.create(
					pair.getFirst(), pair.getSecond(), null
				),
				"Creating a Relation with null Status does not throw"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void creatingRelationsToSelfThrowsTest(final Status status) {
		accountPairStream.forEach(pair -> {
			final Account account = pair.getFirst();
			
			assertThrows(
				IllegalArgumentException.class,
				() -> relationCreatorService
					.create(account, account, status),
				"Creating" + getRelationInfo(account, account, status)
				+ "does not throw"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void optionalIsPresentAfterCreatingANewRelationTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			final Optional<Relation> opt = relationCreatorService
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

				final Optional<Relation> opt = relationCreatorService
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
	public void optionalIsNotPresentWhenRecreatingExistingRelationTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			relationCreatorService.create(source, target, status);
			
			final Optional<Relation> opt = relationCreatorService
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
	public void returnedOptionalRelationHasValuesFromParametersTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationCreatorService
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
				() -> relationCreatorService.removeRelation(
					pair.getFirst(), pair.getSecond(), null
				)
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removingARelationThatDoesNotExistDoesNotThrowTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			assertDoesNotThrow(
				() -> relationCreatorService
					.removeRelation(source, target, status),
				"The method throws when trying to remove a nonexisting "
				+ getRelationInfo(source, target, status)
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void relationDoesNotExistAfterItHasBeenRemovedTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			final Relation relation = relationCreatorService
				.create(source, target, status)
				.get();

			assertTrue(relationExists(relation));
			removeRelation(relation);

			assertFalse(
				relationExists(relation),
				relation + " exists even after it has been removed"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removingARelationDoesNotRemoveRelationTheOtherWayTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation1 = relationCreatorService
				.create(source, target, status)
				.get();

			final Relation relation2 = relationCreatorService
				.create(target, source, status)
				.get();

			assertTrue(relationExists(relation1));
			assertTrue(relationExists(relation2));

			removeRelation(relation1);

			assertTrue(
				relationExists(relation2),
				"Created " + getRelationInfo(target, source, status)
				+ " does not exists after removing "
				+ getRelationInfo(source, target, status) + " even when it has "
				+ "not been removed"
			);
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void removingARelationWithOneStatusDoesNotRemoveRelationsWithOtherStatusesTest(
			final Status statusToBeRemoved) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationCreatorService
				.create(source, target, statusToBeRemoved)
				.get();
			
			for (final Status status : Status.values()) {
				if (status != statusToBeRemoved) {
					relationCreatorService.create(source, target, status);
				}
			}
			
			removeRelation(relation);
			
			for (final Status status : Status.values()) {
				if (status != statusToBeRemoved) {
					assertTrue(
						relationFinderService.relationExists(
							source, target, status
						),
						"Created " + getRelationInfo(source, target, status)
						+ "does not exist after removing " + relation
					);
				}
			}
		});
	}
	
	@ParameterizedTest
	@EnumSource(Status.class)
	public void creatingDublicateRelationsDoesNotChangeTheSourceAndTargetRelationSizeTest(
			final Status status) {
		
		accountPairStream.forEach(pair -> {
			final Account source = pair.getFirst();
			final Account target = pair.getSecond();
			
			final Relation relation = relationCreatorService
				.create(source, target, status)
				.get();
			
			final int relationsFrom = relationFinderService
				.getRelationsFrom(source)
				.size();
			
			final int relationsTo = relationFinderService
				.getRelationsTo(target)
				.size();
			
			relationCreatorService.create(source, target, status);
			
			assertEquals(
				relationsFrom,
				relationFinderService.getRelationsFrom(source).size(),
				"After creating the " + relation + " for the second time, the "
				+ "source Accounts Relation count changes"
			);
			
			assertEquals(
				relationsTo,
				relationFinderService.getRelationsTo(target).size(),
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
			
			final Relation relation = relationCreatorService
				.create(source, target, status)
				.get();
			
			removeRelation(relation);
			
			assertFalse(
				relationFinderService
					.getRelationsFrom(source)
					.contains(relation),
				"Relation " + relation + " can be found from the source "
				+ "Accounts " + source + " Relation list after removal"
			);
			
			assertFalse(
				relationFinderService
					.getRelationsTo(target)
					.contains(relation),
				"Relation " + relation + " can be found from the target "
				+ "Accounts " + target + " Relation list after removal"
			);
		});
	}
	
	private boolean relationExists(final Relation relation) {
		return relationFinderService.relationExists(
			relation.getSource(), relation.getTarget(), relation.getStatus()
		);
	}
	
	private void removeRelation(final Relation relation) {
		relationCreatorService.removeRelation(
			relation.getSource(), relation.getTarget(), relation.getStatus()
		);
	}
}
