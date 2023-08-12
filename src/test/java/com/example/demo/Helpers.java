
package com.example.demo;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Helpers {
	
	private static final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private static final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	
	/**
	 * 
	 * @return Stream of AccountCreationDto objects.
	 * 
	 * The ALl AccountCreationDto objects have unique username chosen from
	 * VALID_USERNAMEs list. There are as many objects in this stream as there
	 * are usernames in the list. Passwords are chosen from the VALID_PASSWORDs 
	 * list. All passwords are not guaranteed to be unique.
	 */
	public Stream<AccountCreationDto> accountCreationDtosWithUniqueUsernamesStream() {
		return IntStream.range(0, VALID_USERNAMES.size())
			.mapToObj(i -> new AccountCreationDto(
				VALID_USERNAMES.get(i), getValidPasswordModuloSize(i)
			)
		);
	}
	
	public String getValidPasswordModuloSize(int i) {
		return VALID_PASSWORDS.get(i % VALID_PASSWORDS.size());
	}
	
	public String accountInfo(AccountCreationDto accountCreationDto, Role role) {
		return "Account with username '" + accountCreationDto.getUsername()
			+ "', raw password '" + accountCreationDto.getPassword()
			+ "' and role '" + role.getName() + "'";
	}
	
	public String accountInfo(Account account) {
		return "Account with username '" + account.getUsername()
			+ "', password '" + account.getPassword()
			+ "' and role '" + account.getRole().getName() + "'";
	}
}
