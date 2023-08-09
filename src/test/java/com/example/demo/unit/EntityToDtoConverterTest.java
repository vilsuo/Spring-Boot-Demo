
package com.example.demo.unit;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class EntityToDtoConverterTest {
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	private final List<String> usernames = UsernameValidatorTest.VALID_USERNAMES;
	private final List<String> passwords = PasswordValidatorTest.VALID_PASSWORDS;
	
	private final int TOTAL_VALUES = Math.min(usernames.size(), passwords.size());
	
	public class AccountWithSettableId extends Account {

		public AccountWithSettableId(
				String username, String password, Long id) {
			
			super(
				username, password, Role.USER,
				new HashSet<>(), new HashSet<>(), new HashSet<>()
			);
			
			super.setId(id);
		}
	}
	
	@Test
	public void convertAccountNullAccountThrowsTest() {
		assertThrows(
			NullPointerException.class,
			() -> entityToDtoConverter.convertAccount(null),
			"Trying to convert null Account to AccountDto does not throw"
		);
	}
	
	@Test
	public void convertAccountIdAndNameAreConvertedTest() {
		for (int i = 0; i < TOTAL_VALUES; ++i) {
			final AccountWithSettableId account = createAccountWithSettableId(
				usernames.get(i), passwords.get(i), Long.valueOf(i)
			);
			
			assertAccountDtoIsCreatedFromAccount(
				entityToDtoConverter.convertAccount(account),
				account
			);
		}
	}
	
	@Test
	public void convertOptionalAccountConvertingEmptyOptionalAccountReturnsEmptyOptionalTest() {
		assertTrue(
			entityToDtoConverter.convertOptionalAccount(
				Optional.empty()
			).isEmpty(),
			"After trying to convert an empty Optional to Optional<AccountDto>"
			+ ", the returned Optional is not Empty"
		);
	}
	
	@Test
	public void convertOptionalAccountConvertingNonEmptyOptionalAccountReturnsNonEmptyOptionalTest() {
		for (int i = 0; i < TOTAL_VALUES; ++i) {
			Optional<AccountWithSettableId> opt = Optional.of(
				createAccountWithSettableId(
					usernames.get(i), passwords.get(i), Long.valueOf(i)
				)
			);
			
			assertTrue(
				entityToDtoConverter.convertOptionalAccount(opt).isPresent(),
				"After trying to convert a non empty Optional to "
				+ "Optional<AccountDto>, the returned Optional is not Present"
			);
		}
	}
	
	@Test
	public void convertOptionalAccountConvertingNonEmptyOptionalAccountReturnsNonEmptyOptionalWithMatchingAccountDtoTest() {
		for (int i = 0; i < TOTAL_VALUES; ++i) {
			AccountWithSettableId account = createAccountWithSettableId(
				usernames.get(i), passwords.get(i), Long.valueOf(i)
			);
			
			assertAccountDtoIsCreatedFromAccount(
				entityToDtoConverter.convertOptionalAccount(
					Optional.of(account)
				).get(),
				account
			);
		}
	}
	
	private AccountWithSettableId createAccountWithSettableId(
		final String username, final String password, Long id) {
		
		return new AccountWithSettableId(username, password, id);
	}
	
	private void assertAccountDtoIsCreatedFromAccount(
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
}
