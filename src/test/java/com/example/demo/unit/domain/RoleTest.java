
package com.example.demo.unit.domain;

import com.example.demo.domain.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest {
	
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
}
