
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
	 * 
	 * @return The Stream has as many elements as there are possible combination
	 *	of Roles. Each Pair of the Stream has the following properties:
	 * 
	 *	1)	each AccounCreationDto has valid and unique username and password
	 *	2)	the combination of Roles of first and second Pair is unique. This
	 *		means that there are no other Pairs in the Stream that has the same
	 *		first Role AND second Role.
	 * 
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
	 * Asserts two things:
	 * 1.	that ids and Statuses of the two parameters are equal
	 * 2.	source and target Account/AccountDto are equal based on the method
	 *		assertAccountDtoIsCreatedFromAccount
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
