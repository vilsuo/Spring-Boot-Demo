
package com.example.demo.testhelpers.helpers;

import com.codepoetics.protonpack.StreamUtils;
import com.example.demo.datatransfer.AccountCreationDto;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.assertAccountDtoIsCreatedFromAccount;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Role;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoPairStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.data.util.Pair;

public final class RelationCreationHelper {
	
	private final static int TOTAL_ROLES = Role.values().length;
	
	/**
	 * Creates a Stream of {@link AccountCreationDto} and {@link Role} object
	 * {@link Pair Pairs}. Each {@code Pair} has unique combination of 
	 * {@code Roles}. All possible combinations of {@code Roles} are in the 
	 * {@code Stream}. All {@code AccountCreationDto} objects are picked from 
	 * the {@link AccountCreationHelper#accountCreationDtoPairStream()}.
	 * 
	 * @return the created {@code Stream}
	 */
	public static Stream<Pair<Pair<AccountCreationDto, Role>, Pair<AccountCreationDto, Role>>>
			accountCreationPairWithAllRoleCombinationsStream() {
				
		final List<Pair<Role, Role>> cartesianOfRoles = new ArrayList<>();
		for (final Role roleFirst : Role.values()) {
			for (final Role roleSecond : Role.values()) {
				cartesianOfRoles.add(Pair.of(roleFirst, roleSecond));
			}
		}
		
		return StreamUtils.zipWithIndex(accountCreationDtoPairStream())
			.limit(TOTAL_ROLES * TOTAL_ROLES)
			.map(indexed -> {
				final int index = (int) indexed.getIndex();
				return Pair.of(
					Pair.of(
						indexed.getValue().getFirst(),
						cartesianOfRoles.get(index).getFirst()
					),
					Pair.of(
						indexed.getValue().getSecond(),
						cartesianOfRoles.get(index).getSecond()
					)
				);
			});
	}
	
	/**
	 * Asserts that {@code RelationDto} is created from {@code Relation}. This
	 * checks two things: 1) the id and the {@link Status} between these two are
	 * equal 2) the source and the target {@link AccountDto} objects of 
	 * {@code RelationDto} are created from the corresponding {@link Account} 
	 * objects of {@code Relation}.
	 * 
	 * @param relationDto
	 * @param relation 
	 */
	public static void assertRelationDtoIsCreatedFromRelation(
			final RelationDto relationDto, final Relation relation) {
		
		// id
		assertEquals(
			relation.getId(), relationDto.getId(), 
			"After converting Relation with id " + relation.getId()
			+ " to RelationDto, the RelationDto has id " + relationDto.getId()
		);
		
		// source
		assertAccountDtoIsCreatedFromAccount(
			relationDto.getSource(), relation.getSource()
		);
		
		// target
		assertAccountDtoIsCreatedFromAccount(
			relationDto.getTarget(), relation.getTarget()
		);
		
		// status
		assertEquals(
			relation.getStatus(), relationDto.getStatus(),
			"After converting Relation with Status " + relation.getStatus()
			+ " to RelationDto, the RelationDto has Status "
			+ relationDto.getStatus()
		);
	}
}
