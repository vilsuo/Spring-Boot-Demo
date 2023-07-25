
package com.example.demo.unit;

import com.example.demo.domain.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest {
	
	@Test
	public void hasTwoRolesTest() {
		assertEquals(Role.values().length, 2);
	}
	
	@Test
	public void getNameTest() {
		assertEquals(Role.USER.getName(), "USER");
		assertEquals(Role.ADMIN.getName(), "ADMIN");
	}
	
	@Test
	public void getRoleTest() {
		String user = "USER";
		String admin = "ADMIN";
		String invalidName = "NONEXISTENT";
		
		assertEquals(Role.getRole(user), Role.USER);
		assertEquals(Role.getRole(admin), Role.ADMIN);
		assertNull(Role.getRole(invalidName));
		assertNull(Role.getRole(null));
	}	
}
