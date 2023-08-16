
package com.example.demo.unit.converter;

import com.example.demo.domain.Role;
import com.example.demo.converter.RoleConverter;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/*
TODO
- add RoleConverter as Bean?
*/
public class RoleConverterTest {
	
	private final RoleConverter roleConverter = new RoleConverter();
	
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
	
	@Test
	public void convertToEntityAttributeThrowsExceptionTest() {
		List<String> values = Arrays.asList(
			null, "", "NONEXISTENT", "User", "user", "admin", "Admin"
		);
		
		for (final String value : values) {
			assertThrows(
				IllegalArgumentException.class, 
				() -> roleConverter.convertToEntityAttribute(value),
				"Converting String '" + value + "' to Role should throw"
			);
		}
	}
}
