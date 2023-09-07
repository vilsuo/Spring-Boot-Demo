
package com.example.demo.unit.converter;

import com.example.demo.domain.Role;
import com.example.demo.converter.RoleConverter;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertingNonNullEnumToDatabaseColumnReturnsEnumFieldValueTest(
			final Role role) {
		
		assertEquals(
			role.getValue(),
			roleConverter.convertToDatabaseColumn(role)
		);
	}
	
	@Test
	public void convertingNullToEntityAttributeThrowsTest() {
		assertThrows(
			UnsupportedOperationException.class,
			() -> roleConverter.convertToEntityAttribute(null)
		);
	}
	
	@ParameterizedTest
	@EnumSource(Role.class)
	public void convertingValidEnumFieldValueToEntityAttributeReturnsTheEnumTest(
			final Role role) {
		
		assertEquals(
			role,
			roleConverter.convertToEntityAttribute(role.getValue())
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
}
