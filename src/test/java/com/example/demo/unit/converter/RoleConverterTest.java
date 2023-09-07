
package com.example.demo.unit.converter;

import com.example.demo.domain.Role;
import com.example.demo.converter.RoleConverter;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RoleConverterTest {
	
	@Autowired
	private RoleConverter roleConverter;
	
	private final List<String> INVALID_ROLE_FIELD_VALUES = Arrays.asList(
		"", "-", "Anonymous", "Moderator", "USER", "admin"
	);
	
	@Test
	public void convertingNullEnumToDatabaseColumnReturnsNullTest() {
		assertEquals(
			null, roleConverter.convertToDatabaseColumn(null)
		);
	}
	
	@Test
	public void convertingNonNullEnumToDatabaseColumnReturnsEnumFieldValueTest() {
		assertEquals(
			"User", roleConverter.convertToDatabaseColumn(Role.USER)
		);
		
		assertEquals(
			"Admin", roleConverter.convertToDatabaseColumn(Role.ADMIN)
		);
	}
	
	@Test
	public void convertingNullToEntityAttributeThrowsTest() {
		assertThrows(
			UnsupportedOperationException.class,
			() -> roleConverter.convertToEntityAttribute(null)
		);
	}
	
	@Test
	public void convertingValidEnumFieldValueToEntityAttributeReturnsTheEnumTest() {
		assertEquals(
			Role.USER, roleConverter.convertToEntityAttribute("User")
		);
		
		assertEquals(
			Role.ADMIN, roleConverter.convertToEntityAttribute("Admin")
		);
	}
	
	@Test
	public void convertingInValidEnumFieldValueToEntityAttributeThrowsTest() {
		for (final String value : INVALID_ROLE_FIELD_VALUES) {
			assertThrows(
				UnsupportedOperationException.class, 
				() -> roleConverter.convertToEntityAttribute(value),
				"Converting value '" + value + "' should throw"
			);
		}
	}
	
	/*
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
	*/
}
