
package com.example.demo.testhelpers;

import com.codepoetics.protonpack.StreamUtils;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.unit.PasswordValidatorTest;
import com.example.demo.unit.UsernameValidatorTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.data.util.Pair;

/*
TODO
- write infos as toString in their own classes
*/
public final class AccountCreationHelpers {
	
	private static final List<String> VALID_USERNAMES = UsernameValidatorTest.VALID_USERNAMES;
	private static final List<String> INVALID_USERNAMES = UsernameValidatorTest.INVALID_USERNAMES;
	private static final List<String> VALID_PASSWORDS = PasswordValidatorTest.VALID_PASSWORDS;
	private static final List<String> INVALID_PASSWORDS = PasswordValidatorTest.INVALID_PASSWORDS;
	
	public static Stream<Pair<AccountCreationDto, AccountCreationDto>> accountCreationDtoPairStream(
			boolean sameUsername, boolean samePassword) {
		
		List<Pair<AccountCreationDto, AccountCreationDto>> lst = new ArrayList<>();
		for (int u_i = 0, p_i = 0;
			u_i < VALID_USERNAMES.size() - 1 && p_i < VALID_PASSWORDS.size() - 1;
			++u_i, ++p_i) {
			
			final String username1 = VALID_USERNAMES.get(u_i);
			final String password1 = VALID_PASSWORDS.get(p_i);
			
			if (!sameUsername) { ++u_i; }
			if (!samePassword) { ++p_i; }
			
			final String username2 = VALID_USERNAMES.get(u_i);
			final String password2 = VALID_PASSWORDS.get(p_i);
			
			lst.add(Pair.of(
				new AccountCreationDto(username1, password1),
				new AccountCreationDto(username2, password2)
			));
		}
		return lst.stream();
	}
	
	public static Stream<AccountCreationDto> accountCreationDtoStream(
			final boolean validUsernames, final boolean validPasswords) {
		
		final int nUsernames = validUsernames
			? VALID_USERNAMES.size() : INVALID_USERNAMES.size();
		
		final int nPasswords = validPasswords
			? VALID_PASSWORDS.size() : INVALID_PASSWORDS.size();
		
		return IntStream.range(0, Math.min(nUsernames, nPasswords))
			.mapToObj(i -> {
				final String username = validUsernames
						? VALID_USERNAMES.get(i) : INVALID_USERNAMES.get(i);
				
				final String password = validPasswords
					? VALID_PASSWORDS.get(i) : INVALID_PASSWORDS.get(i);
				
				return new AccountCreationDto(username, password);
			});
	}
	
	/**
	 * @return Stream of AccountCreationDto objects. Each username is valid and 
	 * unique. Each password is valid and unique
	 * 
	 * I:th Object has i:th username from VALID_USERNAMES and i:th password from 
	 * VALID_PASSWORDS
	 * 
	 */
	public static Stream<AccountCreationDto> accountCreationDtoStream() {
		return accountCreationDtoStream(true, true);
	}
	
	public static Stream<AccountWithSettableId> accountCreationWithIdAndRoleStream(
			final Role role, Long skip) {
		
		return StreamUtils.zipWithIndex(accountCreationDtoStream())
			.skip(skip)
			.map(indexed -> {
				return new AccountWithSettableId(
					indexed.getIndex(),
					indexed.getValue(),
					role
				);
			});
	}
	
	public static Stream<AccountWithSettableId> accountCreationWithIdAndRoleStream(
			final Role role) {
		
		return accountCreationWithIdAndRoleStream(role, 0l);
	}
	
	// implement Role here in the future?
	/**
	 * Asserts that ids and usernames of the two parameters are equal
	 * 
	 * @param accountDto
	 * @param account 
	 */
	public static void assertAccountDtoIsCreatedFromAccount(
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
	
	public static String accountInfo(AccountCreationDto accountCreationDto, Role role) {
		return "Account with username '" + accountCreationDto.getUsername()
			+ "', raw password '" + accountCreationDto.getPassword()
			+ "' and role '" + role.getName() + "'";
	}
	
	public static String accountInfo(Account account) {
		return "Account with username '" + account.getUsername()
			+ "', password '" + account.getPassword()
			+ "' and role '" + account.getRole().getName() + "'";
	}
}