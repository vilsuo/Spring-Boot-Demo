
package com.example.demo.testhelpers.tests;

import com.example.demo.datatransfer.AccountCreationDto;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoPairStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoStream;
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
public class AccountCreationHelperTest {
	
	@Autowired
	private UsernameValidator usernameValidator;
	
	@Autowired
	private PasswordValidator passwordValidator;
	
	@CartesianTest
	public void accountCreationDtoPairStreamIsNotEmptyTest(
			@Values(booleans = {true, false}) boolean setSameUsernameToPair,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		assertTrue(
			accountCreationDtoPairStream(setSameUsernameToPair, setSamePasswordToPair)
				.findFirst().isPresent()
		);
	}
	
	@CartesianTest
	public void accountCreationDtoStreamIsNotEmptyTestTest(
			@Values(booleans = {true, false}) boolean setSameUsernameToPair,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		assertTrue(
			accountCreationDtoStream(setSameUsernameToPair, setSamePasswordToPair)
				.findFirst().isPresent()
		);
	}
	
	@CartesianTest
	public void accountCreationDtoPairStreamParametersIndicateSamenessOfUsernameAndPasswordTest(
			@Values(booleans = {true, false}) boolean setSameUsernameToPair,
			@Values(booleans = {true, false}) boolean setSamePasswordToPair) {
		
		accountCreationDtoPairStream(setSameUsernameToPair, setSamePasswordToPair)
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
	
	@CartesianTest
	public void accountCreationDtoStreamParametersIndicateValidityOfUsernameAndPasswordTest(
			@Values(booleans = {true, false}) boolean setValidUsernames,
			@Values(booleans = {true, false}) boolean setValidPasswords) {
		
		accountCreationDtoStream(setValidUsernames, setValidPasswords)
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
}