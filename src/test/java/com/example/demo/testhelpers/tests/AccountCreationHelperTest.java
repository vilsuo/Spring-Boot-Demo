
package com.example.demo.testhelpers.tests;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Role;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoForOneOfEachRoleStream;
import com.example.demo.validator.PasswordValidator;
import com.example.demo.validator.UsernameValidator;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.uniqueAccountCreationDtoStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationDtoPairStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationPairForAllRoleCombinationsStream;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountCreationHelperTest {
	
	@Autowired
	private UsernameValidator usernameValidator;
	
	@Autowired
	private PasswordValidator passwordValidator;
	
	private final int TOTAL_ROLES = Role.values().length;
	
	@CartesianTest
	public void uniqueAccountCreationDtoStreamIsNotEmptyTestTest(
			@Values(booleans = {true, false}) boolean setSameUsernameToPair,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		assertTrue(
			uniqueAccountCreationDtoStream(setSameUsernameToPair, setSamePasswordToPair)
				.findAny()
				.isPresent()
		);
	}
	
	@CartesianTest
	public void uniqueAccountCreationDtoStreamParametersIndicateTheValidityOfUsernameAndPasswordTest(
			@Values(booleans = {true, false}) boolean setValidUsernames,
			@Values(booleans = {true, false}) boolean setValidPasswords) {
		
		uniqueAccountCreationDtoStream(setValidUsernames, setValidPasswords)
			.forEach(accountCreationDto -> {
				final String username = accountCreationDto.getUsername();
				final boolean usernameIsValid = usernameValidator
					.isValid(username, null);
				
				assertEquals(
					setValidUsernames, usernameIsValid,
					"Username '" + username + "' is supposed to "
					+ (setValidUsernames ? "" : "not ") + "be valid but it is"
					+ (usernameIsValid ? "" : " not")
				);
				
				final String password = accountCreationDto.getPassword();
				final boolean passwordIsValid = passwordValidator
					.isValid(password, null);
						
				assertEquals(
					setValidPasswords, passwordIsValid,
					"Password '" + password + "' is supposed to "
					+ (setValidPasswords ? "" : "not ") + "be valid but it is"
					+ (passwordIsValid ? "" : " not")
				);
			});
	}
	
	@Test
	public void accountCreationDtoForOneOfEachRoleStreamSizeTest() {
		assertEquals(
			TOTAL_ROLES,
			accountCreationDtoForOneOfEachRoleStream().count()
		);
	}
	
	@Test
	public void accountCreationDtoForOneOfEachRoleStreamHasExactlyOneOfEachRoleTest() {
		final Map<Role, Integer> roleCounts = new HashMap<>();
		accountCreationDtoForOneOfEachRoleStream()
			.forEach(pair -> {
				final Role role = pair.getSecond();
				roleCounts.put(role, roleCounts.getOrDefault(role, 0) + 1);
			});
		
		for (final Role role : Role.values()) {
			final int actualRoleCount = roleCounts.getOrDefault(role, 0);
			assertEquals(
				1, actualRoleCount,
				"The stream is supposed to have exactly one occurence of Role "
				+ role + ", but it has " + actualRoleCount + " occurences"
			);
		}
	}
	
	@CartesianTest
	public void validAndUniqueAccountCreationDtoPairStreamIsNotEmptyTest(
			@Values(booleans = {true, false}) boolean setSameUsernameToPair,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		assertTrue(
			validAndUniqueAccountCreationDtoPairStream(setSameUsernameToPair, setSamePasswordToPair)
				.findAny()
				.isPresent()
		);
	}
	
	@CartesianTest
	public void validAndUniqueAccountCreationDtoPairStreamParametersIndicateTheSamenessOfUsernameAndPasswordTest(
			@Values(booleans = {true, false}) boolean setSameUsernameToPair,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		validAndUniqueAccountCreationDtoPairStream(setSameUsernameToPair, setSamePasswordToPair)
			.forEach(pair -> {
				final AccountCreationDto first = pair.getFirst();
				final AccountCreationDto second = pair.getSecond();

				assertEquals(
					setSameUsernameToPair,
					first.getUsername().equals(second.getUsername()),
					"Usernames of the AccountCreationDtos "
					+ first.toString() + " and " + second.toString() + " were "
					+ "supposed to " + (setSameUsernameToPair ? "" : "not")
					+ " be equal"
				);

				assertEquals(
					setSamePasswordToPair,
					first.getPassword().equals(second.getPassword()),
					"Passwords of the AccountCreationDtos "
					+ first.toString() + " and " + second.toString() + " were "
					+ "supposed to " + (setSamePasswordToPair ? "" : "not ")
					+ "be equal"
				);
			});
	}
	
	@Test
	public void validAndUniqueAccountCreationPairForAllRoleCombinationsStreamSizeTest() {
		assertEquals(
			TOTAL_ROLES * TOTAL_ROLES,
			validAndUniqueAccountCreationPairForAllRoleCombinationsStream()
				.count()
		);
	}
	
	@Test
	public void validAndUniqueAccountCreationPairForAllRoleCombinationsStreamHasExactlyOneCombinationOfEachRoleTest() {
		final Map<Role, Map<Role, Integer>> rolePairCounts = new HashMap<>();
		validAndUniqueAccountCreationPairForAllRoleCombinationsStream()
			.forEach(pairOfPairs -> {
				final Role roleFirst = pairOfPairs.getFirst().getSecond();
				final Role roleSecond = pairOfPairs.getSecond().getSecond();
				
				rolePairCounts.putIfAbsent(roleFirst, new HashMap<>());
				
				final Map<Role, Integer> inner = rolePairCounts.get(roleFirst);
				inner.put(roleSecond, inner.getOrDefault(roleSecond, 0) + 1);
			});
		
		for (final Role roleFirst : Role.values()) {
			for (final Role roleSecond : Role.values()) {
				final int rolePairCount = rolePairCounts
						.getOrDefault(roleFirst, new HashMap<>())
						.getOrDefault(roleSecond, 0);
				
				assertEquals(
					1, rolePairCount,
					"Expected Role combination (" + roleFirst.getName() + ", "
					+ roleSecond.getName() + ") to appear once in the Stream, "
					+ "but it appeared " + rolePairCount + " times"
				);
			}
		}
	}
}
