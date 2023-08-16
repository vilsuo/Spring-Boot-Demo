
package com.example.demo.unit;

import static com.example.demo.testhelpers.AccountCreationHelpers.assertAccountDtoIsCreatedFromAccount;
import static com.example.demo.testhelpers.RelationCreationHelpers.assertRelationDtoIsCreatedFromRelation;
import com.example.demo.testhelpers.AccountWithSettableId;
import com.example.demo.testhelpers.RelationWithSettableId;
import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import jakarta.transaction.Transactional;
import java.util.Optional;
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
import static com.example.demo.testhelpers.AccountCreationHelpers.accountCreationWithIdAndRoleStream;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class EntityToDtoConverterTest {
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	@Test
	public void convertingNullAccountThrowsTest() {
		assertThrows(
			NullPointerException.class,
			() -> entityToDtoConverter.convertAccount(null)
		);
	}
	
	@Test
	public void convertingNullRelationThrowsTest() {
		assertThrows(
			NullPointerException.class,
			() -> entityToDtoConverter.convertRelation(null)
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertsAccountToMatchingAccountDtoTest(final Role role) {
		accountCreationWithIdAndRoleStream(role)
			.forEach(accountWithSettableId -> {
				assertAccountDtoIsCreatedFromAccount(
					entityToDtoConverter.convertAccount(accountWithSettableId),
					accountWithSettableId
				);
			});
	}
	
	@CartesianTest
	public void convertsRelationToMatchingRelationDtoTest(
			@CartesianTest.Enum Status status,
			@CartesianTest.Enum Role roleSource,
			@CartesianTest.Enum Role roleTarget) {
		
		final AccountWithSettableId source
			= accountCreationWithIdAndRoleStream(roleSource).findFirst().get();
		
		accountCreationWithIdAndRoleStream(roleTarget, 1l)
			.forEach(accountWithSettableId -> {
				final Long relationId = accountWithSettableId.getId() + 1;
				RelationWithSettableId relation = new RelationWithSettableId(
					relationId, source, accountWithSettableId, status
				);

				assertRelationDtoIsCreatedFromRelation(
					entityToDtoConverter.convertRelation(relation),
					relation
				);
			});
	}
	
	@Test
	public void convertingEmptyOptionalAccountReturnsEmptyOptionalTest() {
		assertTrue(
			entityToDtoConverter.convertOptionalAccount(Optional.empty())
				.isEmpty()
		);
	}
	
	@Test
	public void convertingEmptyOptionalRelationReturnsEmptyOptionalTest() {
		assertTrue(
			entityToDtoConverter.convertOptionalRelation(Optional.empty())
				.isEmpty()
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertingNonEmptyOptionalAccountReturnsNonEmptyOptionalTest(final Role role) {
		accountCreationWithIdAndRoleStream(role)
			.forEach(accountWithSettableId -> {
				Optional<AccountWithSettableId> opt = Optional.of(
					accountWithSettableId
				);

				assertTrue(
					entityToDtoConverter.convertOptionalAccount(opt).isPresent()
				);
			});
	}
	
	@CartesianTest
	public void convertingNonEmptyOptionalRelationReturnsNonEmptyOptionalTest(
			@CartesianTest.Enum Status status,
			@CartesianTest.Enum Role roleSource,
			@CartesianTest.Enum Role roleTarget) {
		
		final AccountWithSettableId source
			= accountCreationWithIdAndRoleStream(roleSource).findFirst().get();
		
		accountCreationWithIdAndRoleStream(roleTarget, 1l)
			.forEach(accountWithSettableId -> {
				final Long relationId = accountWithSettableId.getId() + 1;
				Optional<RelationWithSettableId> opt = Optional.of(
					new RelationWithSettableId(
						relationId, source, accountWithSettableId, status
					)
				);

				assertTrue(
					entityToDtoConverter.convertOptionalRelation(opt)
						.isPresent()
				);
			});
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertingNonEmptyOptionalAccountReturnsNonEmptyOptionalWithMatchingAccountDtoTest(final Role role) {
		accountCreationWithIdAndRoleStream(role)
			.forEach(accountWithSettableId -> {
				assertAccountDtoIsCreatedFromAccount(
					entityToDtoConverter.convertOptionalAccount(
						Optional.of(accountWithSettableId)
					).get(),
					accountWithSettableId
				);
			});
	}
	
	@CartesianTest
	public void convertingNonEmptyOptionalRelationReturnsNonEmptyOptionalWithMatchingRelationDtoTest(
			@CartesianTest.Enum Status status,
			@CartesianTest.Enum Role roleSource,
			@CartesianTest.Enum Role roleTarget) {
		
		final AccountWithSettableId source
			= accountCreationWithIdAndRoleStream(roleSource).findFirst().get();
		
		accountCreationWithIdAndRoleStream(roleTarget, 1l)
			.forEach(accountWithSettableId -> {
				final Long relationId = accountWithSettableId.getId() + 1;
				RelationWithSettableId relation = new RelationWithSettableId(
					relationId, source, accountWithSettableId, status
				);

				assertRelationDtoIsCreatedFromRelation(
					entityToDtoConverter.convertOptionalRelation(
						Optional.of(relation)
					).get(),
					relation
				);
			});
	}
}
