
package com.example.demo.testhelpers;

import static com.example.demo.testhelpers.AccountCreationHelpers.assertAccountDtoIsCreatedFromAccount;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Relation;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class RelationCreationHelpers {
	
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
