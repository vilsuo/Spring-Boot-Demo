
package com.example.demo.testhelpers.tests;

import com.example.demo.datatransfer.AccountCreationDto;
import static com.example.demo.testhelpers.helpers.AccountCreationHelpers.accountCreationDtoPairStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelpers.accountCreationDtoStream;
import com.example.demo.validator.PasswordValidator;
import com.example.demo.validator.UsernameValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountCreationHelpersTest {
	
	@Autowired
	private UsernameValidator usernameValidator;
	
	@Autowired
	private PasswordValidator passwordValidator;
	
	@CartesianTest
	public void accountCreationDtoPairStreamIsNotEmptyTest(
			@Values(booleans = {true, false}) boolean sameUsernames,
			@Values(booleans = {true, false}) boolean samePasswords) {
		
		assertTrue(
			accountCreationDtoPairStream(sameUsernames, samePasswords)
				.findFirst().isPresent()
		);
	}
	
	@CartesianTest
	public void accountCreationDtoStreamIsNotEmptyTestTest(
			@Values(booleans = {true, false}) boolean sameUsernames,
			@Values(booleans = {true, false}) boolean samePasswords) {
		
		assertTrue(
			accountCreationDtoStream(sameUsernames, samePasswords)
				.findFirst().isPresent()
		);
	}
	
	@CartesianTest
	public void accountCreationDtoPairStreamParametersIndicateSamenessOfUsernameAndPasswordTest(
			@Values(booleans = {true, false}) boolean sameUsernames,
			@Values(booleans = {true, false}) boolean samePasswords) {
		
		accountCreationDtoPairStream(sameUsernames, samePasswords)
			.forEach(pair -> {
				final AccountCreationDto first = pair.getFirst();
				final AccountCreationDto second = pair.getSecond();

				assertEquals(
					sameUsernames,
					first.getUsername().equals(second.getUsername()),
					"Usernames of the AccountCreationDtos "
					+ first.toString() + " and " + second.toString()
					+ " were supposed to " + (sameUsernames ? "" : "not")
					+ " be equal"
				);

				assertEquals(
					samePasswords,
					first.getPassword().equals(second.getPassword()),
					"Passwords of the AccountCreationDtos "
					+ first.toString() + " and " + second.toString()
					+ " were supposed to " + (samePasswords ? "" : "not ")
					+ "be equal"
				);
			});
	}
	
	@CartesianTest
	public void accountCreationDtoStreamParametersIndicateValidityOfUsernameAndPasswordTest(
			@Values(booleans = {true, false}) boolean validUsernames,
			@Values(booleans = {true, false}) boolean validPasswords) {
		
		accountCreationDtoStream(validUsernames, validPasswords)
			.forEach(accountCreationDto -> {
				final String username = accountCreationDto.getUsername();
				final boolean usernameIsValid = usernameValidator
					.isValid(username, null);
				
				assertEquals(
					validUsernames, usernameIsValid,
					"Username '" + username + "' is supposed to "
					+ (validUsernames ? "" : "not ") + "be valid but it is"
					+ (usernameIsValid ? "" : " not")
				);
				
				final String password = accountCreationDto.getPassword();
				final boolean passwordIsValid = passwordValidator
					.isValid(password, null);
						
				assertEquals(
					validPasswords, passwordIsValid,
					"Password '" + password + "' is supposed to "
					+ (validPasswords ? "" : "not ") + "be valid but it is"
					+ (passwordIsValid ? "" : " not")
				);
			});
	}
}
