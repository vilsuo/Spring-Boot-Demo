
package com.example.demo.testhelpers.helpers;

import static com.example.demo.testhelpers.helpers.AccountCreationHelper.assertAccountDtoIsCreatedFromAccount;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Relation;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class RelationCreationHelper {
	
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
