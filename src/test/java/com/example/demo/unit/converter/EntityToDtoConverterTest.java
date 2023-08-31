
package com.example.demo.unit.converter;

import static com.example.demo.testhelpers.helpers.AccountCreationHelper.assertAccountDtoIsCreatedFromAccount;
import static com.example.demo.testhelpers.helpers.RelationCreationHelper.assertRelationDtoIsCreatedFromRelation;
import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.RelationService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoForOneOfEachRoleStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationPairForAllRoleCombinationsStream;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.springframework.data.util.Pair;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class EntityToDtoConverterTest {
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private RelationService relationService;

	@Nested
	public class Accounts {
		
		private Stream<Account> accountStream;

		@BeforeEach
		public void initAccounts() {
			accountStream = accountCreationDtoForOneOfEachRoleStream()
				.map(pair -> {
					return accountCreatorService
						.create(pair.getFirst(), pair.getSecond())
						.get();
				});
		}

		@Test
		public void convertingNullAccountThrowsTest() {
			assertThrows(
				NullPointerException.class,
				() -> entityToDtoConverter.convertAccount(null)
			);
		}

		@Test
		public void convertsAccountToMatchingAccountDtoTest() {
			accountStream.forEach(account -> {
				assertAccountDtoIsCreatedFromAccount(
					entityToDtoConverter.convertAccount(account),
					account
				);
			});
		}

		@Test
		public void convertingEmptyOptionalAccountReturnsEmptyOptionalTest() {
			assertTrue(
				entityToDtoConverter
					.convertOptionalAccount(Optional.empty())
					.isEmpty()
			);
		}

		@Test
		public void convertingNonEmptyOptionalAccountReturnsNonEmptyOptionalTest() {
			accountStream.forEach(account -> {
				assertTrue(
					entityToDtoConverter
						.convertOptionalAccount(Optional.of(account))
						.isPresent()
				);
			});
		}

		@Test
		public void convertingNonEmptyOptionalAccountReturnsNonEmptyOptionalWithMatchingAccountDtoTest() {
			accountStream.forEach(account -> {
				assertAccountDtoIsCreatedFromAccount(
					entityToDtoConverter
						.convertOptionalAccount(Optional.of(account))
						.get(),
					account
				);
			});
		}

	}
	
	@Nested
	public class Relations {
		
		private Stream<Pair<Account, Account>> accountPairStream;
		
		@BeforeEach
		public void initAccountPairs() {
			accountPairStream
				= validAndUniqueAccountCreationPairForAllRoleCombinationsStream()
					.map(pairOfPairs -> {
						final Account source = accountCreatorService
							.create(
								pairOfPairs.getFirst().getFirst(),
								pairOfPairs.getFirst().getSecond()
							).get();

						final Account target = accountCreatorService
							.create(
								pairOfPairs.getSecond().getFirst(),
								pairOfPairs.getSecond().getSecond()
							).get();

						return Pair.of(source, target);
					});
		}

		@Test
		public void convertingNullRelationThrowsTest() {
			assertThrows(
				NullPointerException.class,
				() -> entityToDtoConverter.convertRelation(null)
			);
		}
		
		@ParameterizedTest
		@EnumSource(Status.class)
		public void convertsRelationToMatchingRelationDtoTest(
				@CartesianTest.Enum Status status) {

			accountPairStream.forEach(pair -> {
				final Account source = pair.getFirst();
				final Account target = pair.getSecond();
				
				final Relation relation = relationService
					.create(source, target, status)
					.get();
				
				assertRelationDtoIsCreatedFromRelation(
					entityToDtoConverter.convertRelation(relation),
					relation
				);
			});
		}

		@Test
		public void convertingEmptyOptionalRelationReturnsEmptyOptionalTest() {
			assertTrue(
				entityToDtoConverter
					.convertOptionalRelation(Optional.empty())
					.isEmpty()
			);
		}

		@ParameterizedTest
		@EnumSource(Status.class)
		public void convertingNonEmptyOptionalRelationReturnsNonEmptyOptionalTest(
				@CartesianTest.Enum Status status) {

			accountPairStream.forEach(pair -> {
				final Account source = pair.getFirst();
				final Account target = pair.getSecond();
				
				final Optional<Relation> opt = relationService
					.create(source, target, status);
				
				assertTrue(
					entityToDtoConverter
						.convertOptionalRelation(opt)
						.isPresent()
				);
			});
		}

		@ParameterizedTest
		@EnumSource(Status.class)
		public void convertingNonEmptyOptionalRelationReturnsNonEmptyOptionalWithMatchingRelationDtoTest(
				@CartesianTest.Enum Status status) {

			accountPairStream.forEach(pair -> {
				final Account source = pair.getFirst();
				final Account target = pair.getSecond();
				
				final Optional<Relation> opt = relationService
					.create(source, target, status);
				
				assertTrue(
					entityToDtoConverter
						.convertOptionalRelation(opt)
						.isPresent()
				);
				
				assertRelationDtoIsCreatedFromRelation(
					entityToDtoConverter
						.convertOptionalRelation(opt)
						.get(),
					opt.get()
				);
			});
		}
	}
}
