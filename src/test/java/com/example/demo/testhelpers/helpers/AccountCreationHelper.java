
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

/*
TODO
- rename tests
- move tests from RelationCreationHelper
*/
public final class AccountCreationHelper {
	
	private static final List<String> VALID_USERNAMES
		= new ArrayList<>(UsernameValidatorTest.VALID_USERNAMES);
	
	private static final List<String> INVALID_USERNAMES
		= new ArrayList<>(UsernameValidatorTest.INVALID_USERNAMES);
	
	private static final List<String> VALID_PASSWORDS
		= new ArrayList<>(PasswordValidatorTest.VALID_PASSWORDS);
	
	private static final List<String> INVALID_PASSWORDS
		= new ArrayList<>(PasswordValidatorTest.INVALID_PASSWORDS);
	
	private final static int TOTAL_ROLES = Role.values().length;
	
	/**
	 * Stream of {@link AccountCreationDto} {@link Account Accounts}. Each 
	 * {@code AccountCreationDto} object has unique username and password.
	 * 
	 * @param setValidUsernames whether to set valid or invalid username for 
	 *							each {@link AccountCreationDto}
	 * @param setValidPasswords whether to set valid or invalid password for 
	 *							each {@link AccountCreationDto}
	 * @return					the created {@code Stream}
	 */
	public static Stream<AccountCreationDto> uniqueAccountCreationDtoStream(
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
	 * Shortcut for method 
	 * {@link #uniqueAccountCreationDtoStream(boolean, boolean)} call with both 
	 * parameters set to {@code true}
	 * 
	 * @return the created {@code Stream}
	 */
	public static Stream<AccountCreationDto> 
			validAndUniqueAccountCreationDtoStream() {
				
		return uniqueAccountCreationDtoStream(true, true);
	}
	
	// TODO COMMENT
	/**
	 * 
	 * @return 
	 */
	public static Stream<Pair<AccountCreationDto, Role>> 
			accountCreationDtoForOneOfEachRoleStream() {
			
		return StreamUtils
			.zipWithIndex(validAndUniqueAccountCreationDtoStream())
			.limit(TOTAL_ROLES)
			.map(indexed -> {
				return Pair.of(
					indexed.getValue(),
					Role.values()[(int) indexed.getIndex()]
				);
			});
	}
	
	/**
	 * Stream of {@link AccountWithSettableId} objects used for testing 
	 * {@link Account} objects without any database operations. Transforms the 
	 * {@code Stream} of {@link #validAndUniqueAccountCreationDtoStream()} to a 
	 * {@code Stream} of {@code AccountWithSettableId}. This transform includes 
	 * setting the id to the index of appearance in the {@code Stream} and 
	 * {@link Role} to the {@code role}.
	 * 
	 * @param role	the {@link Role} to be set for each 
	 *				{@link AccountWithSettableId}
	 * @param skip	the number of values to skip from the start of the
	 *				{@code Stream}
	 * @return		the created {@code Stream}
	 */
	public static Stream<AccountWithSettableId> 
			validAndUniqueAccountWithSettableIdStream(final Role role, 
													  final Long skip){
		
		return StreamUtils
			.zipWithIndex(validAndUniqueAccountCreationDtoStream())
			.skip(skip)
			.map(indexed -> {
				return new AccountWithSettableId(
					indexed.getIndex(),	// Id
					indexed.getValue(), // AccountCreationDto
					role				// Role
				);
			});
	}
	
	/**
	 * Shortcut for method 
	 * {@link #validAndUniqueAccountWithSettableIdStream(Role, Long)} call with 
	 * zero skipped values.
	 * 
	 * @param	role
	 * @return	the created {@code Stream} of {@link AccountWithSettableId} 
	 *			objects
	 */
	public static Stream<AccountWithSettableId> 
			validAndUniqueAccountWithSettableIdStream(final Role role) {
		
		return validAndUniqueAccountWithSettableIdStream(role, 0l);
	}
			
	/**
	 * Stream of {@link AccountCreationDto AccountCreationDto} 
	 * {@link Pair Pairs} used for creating {@link Account Accounts}. The 
	 * usernames and passwords of the {@code AccountCreationDto} objects are 
	 * unique {@code Pair}-wise.
	 * 
	 * @param setSameUsernameToPair whether to set the same username for both
	 *								{@link AccountCreationDto} of the 
	 *								{@link Pair}
	 * @param setSamePasswordToPair whether to set the same password for both
	 *								{@link AccountCreationDto} of the 
	 *								{@link Pair}
	 * @return						the created {@code Stream}
	 */
	public static Stream<Pair<AccountCreationDto, AccountCreationDto>> 
			validAndUniqueAccountCreationDtoPairStream(
				final boolean setSameUsernameToPair,
				final boolean setSamePasswordToPair) {
		
		final List<Pair<AccountCreationDto, AccountCreationDto>> lst
			= new ArrayList<>();
		
		for (int uIdx = 0, pIdx = 0; (uIdx < VALID_USERNAMES.size() - 1)
				&& (pIdx < VALID_PASSWORDS.size() - 1); ++uIdx, ++pIdx) {
			
			final String username1 = VALID_USERNAMES.get(uIdx);
			final String password1 = VALID_PASSWORDS.get(pIdx);
			
			if (!setSameUsernameToPair) { ++uIdx; }
			if (!setSamePasswordToPair) { ++pIdx; }
			
			final String username2 = VALID_USERNAMES.get(uIdx);
			final String password2 = VALID_PASSWORDS.get(pIdx);
			
			lst.add(Pair.of(
				new AccountCreationDto(username1, password1),
				new AccountCreationDto(username2, password2)
			));
		}
		return lst.stream();
	}
	
	/**
	 * Shortcut for method 
	 * {@link #validAndUniqueAccountCreationDtoPairStream(boolean, boolean)} 
	 * call with both parameters set to {@code false}
	 * 
	 * @return the created {@code Stream}
	 */
	public static Stream<Pair<AccountCreationDto, AccountCreationDto>>
			validAndUniqueAccountCreationDtoPairStream() {
			
		return validAndUniqueAccountCreationDtoPairStream(false, false);
	}
		
	/**
	 * Creates a Stream of {@link AccountCreationDto} and {@link Role} object
	 * {@link Pair Pairs}. Each {@code Pair} has unique combination of 
	 * {@code Roles}. All possible combinations of {@code Roles} are in the 
	 * {@code Stream}. All {@code AccountCreationDto} objects are picked from 
	 * the {@link #validAndUniqueAccountCreationDtoPairStream()}.
	 * 
	 * @return the created {@code Stream}
	 */
	public static Stream<Pair<Pair<AccountCreationDto, Role>, Pair<AccountCreationDto, Role>>>
			validAndUniqueAccountCreationPairForAllRoleCombinationsStream() {
		
		return StreamUtils
			.zipWithIndex(validAndUniqueAccountCreationDtoPairStream())
			.limit(TOTAL_ROLES * TOTAL_ROLES)
			.map(indexed -> {
				final int index = (int) indexed.getIndex();
				
				return Pair.of(
					Pair.of(
						indexed.getValue().getFirst(),
						Role.values()[index / TOTAL_ROLES]
					),
					Pair.of(
						indexed.getValue().getSecond(),
						Role.values()[index % TOTAL_ROLES]
					)
				);
			});
	}
	
	/**
	 * Asserts that ids and usernames of the {@link AccountDto} and
	 * {@link Account} objects are equal.
	 * 
	 * @param accountDto
	 * @param account 
	 */
	public static void assertAccountDtoIsCreatedFromAccount(
			final AccountDto accountDto, final Account account) {
		
		assertEquals(
			account.getId(), accountDto.getId(),
			"After creating " + accountDto + " from " + account + ", the id is "
			+ "supposed be " + account.getId() + ", not " + accountDto.getId()
		);
		
		assertEquals(
			account.getUsername(), accountDto.getUsername(),
			"After creating " + accountDto + " from " + account + ", the "
			+ "username is supposed be " + account.getUsername() + ", not "
			+ accountDto.getUsername()
		);
	}
	
	/**
	 * Creates an info message about the parameters of 
	 * {@link AccountCreationService#create}.
	 * 
	 * @param	accountCreationDto
	 * @param	role
	 * @return	the info message
	 */
	public static String accountCreateInfo(
			final AccountCreationDto accountCreationDto, final Role role) {
		
		return "Account creation parameters with " + accountCreationDto
				+ " and " + role;
	}
}