
package com.example.demo.unit;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class EntityToDtoConverterTest {
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	private final List<String> usernames = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> passwords = PasswordValidatorTest.VALID_PASSWORDS;
	
	private final int TOTAL_VALUES = Math.min(usernames.size(), passwords.size());
	
	private class AccountWithSettableId extends Account {

		public AccountWithSettableId(
				Long id, String username, String password, Role role) {
			
			super(
				username, password, role,
				new HashSet<>(), new HashSet<>(), new HashSet<>()
			);
			
			super.setId(id);
		}
	}
	
	private class RelationWithSettableId extends Relation {
		
		public RelationWithSettableId(
				Long id, Account source, Account target, Status status) {
			
			super(source, target, status);
			super.setId(id);
		}
	}
	
	@Test
	public void convertAccountNullAccountThrowsTest() {
		assertThrows(
			NullPointerException.class,
			() -> entityToDtoConverter.convertAccount(null),
			"Trying to convert null Account to AccountDto does not throw"
		);
	}
	
	@Test
	public void convertRelationNullRelationThrowsTest() {
		assertThrows(
			NullPointerException.class,
			() -> entityToDtoConverter.convertRelation(null),
			"Trying to convert null Relation to RelationDto does not throw"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertAccountAccountIsConvertedTest(final Role role) {
		for (int i = 0; i < TOTAL_VALUES; ++i) {
			final AccountWithSettableId account = createAccountWithSettableId(
				Long.valueOf(i), usernames.get(i), passwords.get(i), role
			);
			
			assertAccountDtoIsCreatedFromAccount(
				entityToDtoConverter.convertAccount(account),
				account
			);
		}
	}
	
	@CartesianTest
	public void convertRelationRelationIsConvertedTest(
			@CartesianTest.Enum Status status,
			@CartesianTest.Enum Role roleSource,
			@CartesianTest.Enum Role roleTarget) {
		
		final AccountWithSettableId source = createAccountWithSettableId(
			Long.valueOf(0), usernames.get(0), passwords.get(0), roleSource
		);
		
		for (int i = 1; i < TOTAL_VALUES; ++i) {
			final Long targetId = Long.valueOf(i);
			final AccountWithSettableId target = createAccountWithSettableId(
				targetId, usernames.get(i), passwords.get(i), roleTarget
			);
			
			final Long relationId = targetId + 1;
			RelationWithSettableId relation = createRelationWithSettableId(
				relationId, source, target, status
			);
			
			assertRelationDtoIsCreatedFromRelation(
				entityToDtoConverter.convertRelation(relation),
				relation
			);
		}
	}
	
	@Test
	public void convertOptionalAccountConvertingEmptyOptionalAccountReturnsEmptyOptionalTest() {
		assertTrue(
			entityToDtoConverter.convertOptionalAccount(
				Optional.empty()
			).isEmpty(),
			"After trying to convert an empty Optional to Optional<AccountDto>"
			+ ", the returned Optional is not Empty"
		);
	}
	
	@Test
	public void convertOptionalRelationConvertingEmptyOptionalRelationReturnsEmptyOptionalTest() {
		assertTrue(
			entityToDtoConverter.convertOptionalRelation(
				Optional.empty()
			).isEmpty(),
			"After trying to convert an empty Optional to Optional<RelationDto>"
			+ ", the returned Optional is not Empty"
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertOptionalAccountConvertingNonEmptyOptionalAccountReturnsNonEmptyOptionalTest(final Role role) {
		for (int i = 0; i < TOTAL_VALUES; ++i) {
			Optional<AccountWithSettableId> opt = Optional.of(
				createAccountWithSettableId(
					Long.valueOf(i), usernames.get(i), passwords.get(i), role
				)
			);
			
			assertTrue(
				entityToDtoConverter.convertOptionalAccount(opt).isPresent(),
				"After trying to convert a non empty Optional to "
				+ "Optional<AccountDto>, the returned Optional is not Present"
			);
		}
	}
	
	@CartesianTest
	public void convertOptionalRelationConvertingNonEmptyOptionalRelationReturnsNonEmptyOptionalTest(
			@CartesianTest.Enum Status status,
			@CartesianTest.Enum Role roleSource,
			@CartesianTest.Enum Role roleTarget) {
		
		final AccountWithSettableId source = createAccountWithSettableId(
			Long.valueOf(0), usernames.get(0), passwords.get(0), roleSource
		);
		
		for (int i = 1; i < TOTAL_VALUES; ++i) {
			final Long targetId = Long.valueOf(i);
			final AccountWithSettableId target = createAccountWithSettableId(
				targetId, usernames.get(i), passwords.get(i), roleTarget
			);
			
			final Long relationId = targetId + 1;
			Optional<RelationWithSettableId> opt = Optional.of(
				createRelationWithSettableId(
					relationId, source, target, status
				)
			);
			
			assertTrue(
				entityToDtoConverter.convertOptionalRelation(opt).isPresent(),
				"After trying to convert a non empty Optional to "
				+ "Optional<RelationDto>, the returned Optional is not Present"
			);
		}
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertOptionalAccountConvertingNonEmptyOptionalAccountReturnsNonEmptyOptionalWithMatchingAccountDtoTest(final Role role) {
		for (int i = 0; i < TOTAL_VALUES; ++i) {
			AccountWithSettableId account = createAccountWithSettableId(
				Long.valueOf(i), usernames.get(i), passwords.get(i), role
			);
			
			assertAccountDtoIsCreatedFromAccount(
				entityToDtoConverter.convertOptionalAccount(
					Optional.of(account)
				).get(),
				account
			);
		}
	}
	
	@CartesianTest
	public void convertOptionalRelationConvertingNonEmptyOptionalRelationReturnsNonEmptyOptionalWithMatchingRelationDtoTest(
			@CartesianTest.Enum Status status,
			@CartesianTest.Enum Role roleSource,
			@CartesianTest.Enum Role roleTarget) {
		
		final AccountWithSettableId source = createAccountWithSettableId(
			Long.valueOf(0), usernames.get(0), passwords.get(0), roleSource
		);
		
		for (int i = 1; i < TOTAL_VALUES; ++i) {
			final Long targetId = Long.valueOf(i);
			final AccountWithSettableId target = createAccountWithSettableId(
				targetId, usernames.get(i), passwords.get(i), roleTarget
			);
			
			final Long relationId = targetId + 1;
			RelationWithSettableId relation = createRelationWithSettableId(
				relationId, source, target, status
			);
			
			assertRelationDtoIsCreatedFromRelation(
				entityToDtoConverter.convertOptionalRelation(
					Optional.of(relation)
				).get(),
				relation
			);
		}
	}
	
	private AccountWithSettableId createAccountWithSettableId(
			final Long id, final String username,
			final String password, final Role role) {
		
		return new AccountWithSettableId(id, username, password, role);
	}
	
	private RelationWithSettableId createRelationWithSettableId(
			final Long id, final Account source,
			final Account target, final Status status) {
		
		return new RelationWithSettableId(id, source, target, status);
	}
	
	// implement Role here in the future?
	/**
	 * Asserts that ids and usernames of the two parameters are equal
	 * 
	 * @param accountDto
	 * @param account 
	 */
	private void assertAccountDtoIsCreatedFromAccount(
			final AccountDto accountDto, final Account account) {
		
		assertEquals(
			account.getId(), accountDto.getId(),
			"After converting Account with id " + account.getId() + " to "
			+ "AccountDto, the AccountDto has id " + accountDto.getId()
		);
		
		assertEquals(
			account.getUsername(), accountDto.getUsername(),
			"After converting Account with username " + account.getUsername()
			+ " to AccountDto, the AccountDto has username "
			+ accountDto.getUsername()
		);
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
	private void assertRelationDtoIsCreatedFromRelation(
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
