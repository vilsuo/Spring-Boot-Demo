
package com.example.demo.unittest;

import com.example.demo.domain.Role;
import com.example.demo.converter.RoleConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class RoleConverterTest {
	
	private RoleConverter roleConverter = new RoleConverter();
	
	@Test
	public void convertToDatabaseColumnTest() {
		assertEquals(roleConverter.convertToDatabaseColumn(Role.USER), "USER");
		assertEquals(roleConverter.convertToDatabaseColumn(Role.ADMIN), "ADMIN");
		
		assertEquals(roleConverter.convertToDatabaseColumn(null), null);
	}
	
	@Test
	public void convertToEntityAttributeTest() {
		assertEquals(roleConverter.convertToEntityAttribute("USER"), Role.USER);
		assertEquals(roleConverter.convertToEntityAttribute("ADMIN"), Role.ADMIN);
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "NONEXISTENT", "User", "user", "admin", "Admin" })
	public void convertToEntityAttributeThrowsExceptionTest(String value) {
		assertThrows(IllegalArgumentException.class, () -> roleConverter.convertToEntityAttribute(value));
	}
}
