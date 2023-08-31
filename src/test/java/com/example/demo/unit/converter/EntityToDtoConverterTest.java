
package com.example.demo.unit.converter;

import static com.example.demo.testhelpers.helpers.AccountCreationHelper.assertAccountDtoIsCreatedFromAccount;
import static com.example.demo.testhelpers.helpers.RelationCreationHelper.assertRelationDtoIsCreatedFromRelation;
import com.example.demo.testhelpers.helpers.AccountWithSettableId;
import com.example.demo.testhelpers.helpers.RelationWithSettableId;
import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountCreatorService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoForOneOfEachRoleStream;
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
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountWithSettableIdStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/*
write with creating accounts
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class EntityToDtoConverterTest {
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	/*
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	private Stream<Account> accountStream;
	*/
	private final static Long SKIP = 1l;
	
	/*
	@BeforeEach
	public void initAccounts() {
		accountStream = accountCreationDtoForOneOfEachRoleStream()
			.map(pair -> {
				return accountCreatorService
					.create(pair.getFirst(), pair.getSecond()).get();
			});
	}
	*/
	
	@BeforeAll
	public static void ensureStreamIsNotEmptyAfterSkipping() {
		for (final Role role : Role.values()) {
			assertTrue(
				validAndUniqueAccountWithSettableIdStream(role, SKIP)
					.findAny()
					.isPresent()
			);
		}
	}
	
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
		validAndUniqueAccountWithSettableIdStream(role)
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
			= validAndUniqueAccountWithSettableIdStream(roleSource)
				.findFirst()
				.get();
		
		validAndUniqueAccountWithSettableIdStream(roleTarget, SKIP)
			.forEach(accountWithSettableId -> {
				final Long relationId = accountWithSettableId.getId() + 1;
				final RelationWithSettableId relation
					= new RelationWithSettableId(
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
		validAndUniqueAccountWithSettableIdStream(role)
			.forEach(accountWithSettableId -> {
				final Optional<AccountWithSettableId> opt = Optional.of(
					accountWithSettableId
				);

				assertTrue(
					entityToDtoConverter.convertOptionalAccount(opt)
						.isPresent()
				);
			});
	}
	
	@CartesianTest
	public void convertingNonEmptyOptionalRelationReturnsNonEmptyOptionalTest(
			@CartesianTest.Enum Status status,
			@CartesianTest.Enum Role roleSource,
			@CartesianTest.Enum Role roleTarget) {
		
		final AccountWithSettableId source
			= validAndUniqueAccountWithSettableIdStream(roleSource)
				.findFirst()
				.get();
		
		validAndUniqueAccountWithSettableIdStream(roleTarget, SKIP)
			.forEach(accountWithSettableId -> {
				final Long relationId = accountWithSettableId.getId() + 1;
				final Optional<RelationWithSettableId> opt = Optional.of(
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
		validAndUniqueAccountWithSettableIdStream(role)
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
			= validAndUniqueAccountWithSettableIdStream(roleSource)
				.findFirst()
				.get();
		
		validAndUniqueAccountWithSettableIdStream(roleTarget, SKIP)
			.forEach(accountWithSettableId -> {
				final Long relationId = accountWithSettableId.getId() + 1;
				final RelationWithSettableId relation =
					new RelationWithSettableId(
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
