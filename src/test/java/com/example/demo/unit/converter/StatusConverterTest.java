
package com.example.demo.unit.converter;

import com.example.demo.converter.StatusConverter;
import com.example.demo.domain.Status;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/*
TODO
- add StatusConverter as Bean?
*/
public class StatusConverterTest {
	
	private final StatusConverter statusConverter = new StatusConverter();
	
	@Test
	public void convertToDatabaseColumnTest() {
		assertEquals(statusConverter.convertToDatabaseColumn(Status.FRIEND), "FRIEND");
		assertEquals(statusConverter.convertToDatabaseColumn(Status.BLOCKED), "BLOCKED");
		
		assertEquals(statusConverter.convertToDatabaseColumn(null), null);
	}
	
	@Test
	public void convertToEntityAttributeTest() {
		assertEquals(statusConverter.convertToEntityAttribute("FRIEND"), Status.FRIEND);
		assertEquals(statusConverter.convertToEntityAttribute("BLOCKED"), Status.BLOCKED);
	}
	
	@Test
	public void convertToEntityAttributeThrowsExceptionTest() {
		List<String> values = Arrays.asList(
			null, "", "NONEXISTENT", "SISTER", "CHILD", "friend", "Friend"
		);
		
		for (final String value : values) {
			assertThrows(
				IllegalArgumentException.class, 
				() -> statusConverter.convertToEntityAttribute(value),
				"Converting String '" + value + "' to Status should throw"
			);
		}
	}
}
