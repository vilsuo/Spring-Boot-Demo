
package com.example.demo.unit.converter;

import com.example.demo.converter.PrivacyConverter;
import com.example.demo.domain.Privacy;
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
public class PrivacyConverterTest {
	
	@Autowired
	private PrivacyConverter privacyConverter;
	
	private final List<String> INVALID_PRIVACY_FIELD_VALUES = Arrays.asList(
		"", "-", "Hidden", "Admins", "PRIVATE", "all"
	);
	
	@Test
	public void convertingNullEnumToDatabaseColumnReturnsNullTest() {
		assertEquals(
			null, privacyConverter.convertToDatabaseColumn(null)
		);
	}
	
	@Test
	public void convertingNonNullEnumToDatabaseColumnReturnsEnumFieldValueTest() {
		assertEquals(
			"All", privacyConverter.convertToDatabaseColumn(Privacy.ALL)
		);
		
		assertEquals(
			"Friends", privacyConverter.convertToDatabaseColumn(Privacy.FRIENDS)
		);
		
		assertEquals(
			"Private", privacyConverter.convertToDatabaseColumn(Privacy.PRIVATE)
		);
	}
	
	@Test
	public void convertingNullToEntityAttributeThrowsTest() {
		assertThrows(
			UnsupportedOperationException.class,
			() -> privacyConverter.convertToEntityAttribute(null)
		);
	}
	
	@Test
	public void convertingValidEnumFieldValueToEntityAttributeReturnsTheEnumTest() {
		assertEquals(
			Privacy.ALL, privacyConverter.convertToEntityAttribute("All")
		);
		
		assertEquals(
			Privacy.FRIENDS,
			privacyConverter.convertToEntityAttribute("Friends")
		);
		
		assertEquals(
			Privacy.PRIVATE,
			privacyConverter.convertToEntityAttribute("Private")
		);
	}
	
	@Test
	public void convertingInValidEnumFieldValueToEntityAttributeThrowsTest() {
		for (final String value : INVALID_PRIVACY_FIELD_VALUES) {
			assertThrows(
				UnsupportedOperationException.class, 
				() -> privacyConverter.convertToEntityAttribute(value),
				"Converting value '" + value + "' should throw"
			);
		}
	}
}
