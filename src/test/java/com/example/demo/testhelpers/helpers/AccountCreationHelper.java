
package com.example.demo.testhelpers.helpers;

import com.codepoetics.protonpack.StreamUtils;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.unit.validator.PasswordValidatorTest;
import com.example.demo.unit.validator.UsernameValidatorTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.data.util.Pair;

public final class AccountCreationHelper {
	
	private static final List<String> VALID_USERNAMES = new ArrayList<>(UsernameValidatorTest.VALID_USERNAMES);
	private static final List<String> INVALID_USERNAMES = new ArrayList<>(UsernameValidatorTest.INVALID_USERNAMES);
	private static final List<String> VALID_PASSWORDS = new ArrayList<>(PasswordValidatorTest.VALID_PASSWORDS);
	private static final List<String> INVALID_PASSWORDS = new ArrayList<>(PasswordValidatorTest.INVALID_PASSWORDS);
	
	public static Stream<Pair<AccountCreationDto, AccountCreationDto>> accountCreationDtoPairStream(
			final boolean setSameUsernameToPair,
			final boolean setSamePasswordToPair) {
		
		List<Pair<AccountCreationDto, AccountCreationDto>> lst = new ArrayList<>();
		for (int u_i = 0, p_i = 0;
			u_i < VALID_USERNAMES.size() - 1 && p_i < VALID_PASSWORDS.size() - 1;
			++u_i, ++p_i) {
			
			final String username1 = VALID_USERNAMES.get(u_i);
			final String password1 = VALID_PASSWORDS.get(p_i);
			
			if (!setSameUsernameToPair) { ++u_i; }
			if (!setSamePasswordToPair) { ++p_i; }
			
			final String username2 = VALID_USERNAMES.get(u_i);
			final String password2 = VALID_PASSWORDS.get(p_i);
			
			lst.add(Pair.of(
				new AccountCreationDto(username1, password1),
				new AccountCreationDto(username2, password2)
			));
		}
		return lst.stream();
	}
	
	public static Stream<Pair<AccountCreationDto, AccountCreationDto>> accountCreationDtoPairStream() {
		return accountCreationDtoPairStream(false, false);
	}
	
	public static Stream<AccountCreationDto> accountCreationDtoStream(
			final boolean setValidUsernames, final boolean setValidPasswords) {
		
		final int nUsernames = setValidUsernames
			? VALID_USERNAMES.size() : INVALID_USERNAMES.size();
		
		final int nPasswords = setValidPasswords
			? VALID_PASSWORDS.size() : INVALID_PASSWORDS.size();
		
		return IntStream.range(0, Math.min(nUsernames, nPasswords))
			.mapToObj(i -> {
				final String username = setValidUsernames
						? VALID_USERNAMES.get(i) : INVALID_USERNAMES.get(i);
				
				final String password = setValidPasswords
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
			final Role role, final Long skip) {
		
		return StreamUtils.zipWithIndex(accountCreationDtoStream())
			.skip(skip)
			.map(indexed -> {
				return new AccountWithSettableId(
					indexed.getIndex(),	// Id
					indexed.getValue(), // AccountCreationDto
					role				// Role
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
			"After converting Account with id '" + account.getId() + "' to "
			+ "AccountDto, the AccountDto has 'id " + accountDto.getId() + "'"
		);
		
		assertEquals(
			account.getUsername(), accountDto.getUsername(),
			"After converting Account with username '" + account.getUsername()
			+ "' to AccountDto, the AccountDto has username '"
			+ accountDto.getUsername() + "'"
		);
	}
	
	public static String accountCreateInfo(AccountCreationDto accountCreationDto, Role role) {
		return "Account created with username '"
			+ accountCreationDto.getUsername()
			+ "', raw password '" + accountCreationDto.getPassword()
			+ "' and Role '" + role.getName() + "'";
	}
}