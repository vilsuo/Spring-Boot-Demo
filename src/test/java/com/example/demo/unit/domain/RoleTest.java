
package com.example.demo.unit.domain;

import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleTest {
	
	public static final Account ANONYMOUS_ACCOUNT = null;
	
	@Test
	public void hasTwoRoleOptionsTest() {
		assertEquals(Role.values().length, 2);
	}
	
	@Test
	public void getNameTest() {
		assertEquals(Role.USER.getName(), "USER");
		assertEquals(Role.ADMIN.getName(), "ADMIN");
	}
	
	@Test
	public void getRoleTest() {
		assertEquals(Role.getRole("USER"), Role.USER);
		assertEquals(Role.getRole("ADMIN"), Role.ADMIN);
		
		final String invalidName = "NONEXISTENT";
		assertNull(Role.getRole(invalidName));
		assertNull(Role.getRole(null));
	}
	
	/**
	 * Created Accounts are tested to not be anonymous in
	 * {@link com.example.demo.integration.service.AccounCreatorServiceTest#createdReturnedAccountsAreNotAnonymousTest}
	 */
	@Test
	public void anonymousTest() {
		assertTrue(
			Role.isAnonymous(ANONYMOUS_ACCOUNT),
			ANONYMOUS_ACCOUNT + " is not anonymous Account"
		);
	}
}
