
package com.example.demo.testhelpers;

import com.example.demo.datatransfer.AccountCreationDto;
import static com.example.demo.testhelpers.AccountCreationHelpers.accountCreationDtoPairStream;
import static com.example.demo.testhelpers.AccountCreationHelpers.accountCreationDtoStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Values;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO
- implement accountCreationDtoStreamParametersIndicateValidityOfUsernameAndPasswordTest
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountCreationHelpersTest {
	
	//private static final UsernameValidator validator = new UsernameValidator();
	
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
			@Values(booleans = {true, false}) boolean sameUsernames,
			@Values(booleans = {true, false}) boolean samePasswords) {
		
		accountCreationDtoStream(sameUsernames, samePasswords)
			.forEach(accountCreationDto -> {
				//throw new UnsupportedOperationException("Method not implemented");
				
				// validate username...
				
				// validate password...
				
			});
	}
}
