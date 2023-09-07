
package com.example.demo.unit.converter;

import com.example.demo.converter.StatusConverter;
import com.example.demo.domain.Status;
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
public class StatusConverterTest {
	
	@Autowired
	private StatusConverter statusConverter;
	
	private final List<String> INVALID_STATUS_FIELD_VALUES = Arrays.asList(
		"", "-", "Employee", "Child", "friend", "FRIEND"
	);
	
	@Test
	public void convertingNullEnumToDatabaseColumnReturnsNullTest() {
		assertEquals(
			null, statusConverter.convertToDatabaseColumn(null)
		);
	}
	
	@Test
	public void convertingNonNullEnumToDatabaseColumnReturnsEnumFieldValueTest() {
		assertEquals(
			"Friend", statusConverter.convertToDatabaseColumn(Status.FRIEND)
		);
		
		assertEquals(
			"Blocked", statusConverter.convertToDatabaseColumn(Status.BLOCKED)
		);
	}
	
	@Test
	public void convertingNullToEntityAttributeThrowsTest() {
		assertThrows(
			UnsupportedOperationException.class,
			() -> statusConverter.convertToEntityAttribute(null)
		);
	}
	
	@Test
	public void convertingValidEnumFieldValueToEntityAttributeReturnsTheEnumTest() {
		assertEquals(
			Status.FRIEND, statusConverter.convertToEntityAttribute("Friend")
		);
		
		assertEquals(
			Status.BLOCKED, statusConverter.convertToEntityAttribute("Blocked")
		);
	}
	
	@Test
	public void convertingInValidEnumFieldValueToEntityAttributeThrowsTest() {
		for (final String value : INVALID_STATUS_FIELD_VALUES) {
			assertThrows(
				UnsupportedOperationException.class, 
				() -> statusConverter.convertToEntityAttribute(value),
				"Converting value '" + value + "' should throw"
			);
		}
	}
}
