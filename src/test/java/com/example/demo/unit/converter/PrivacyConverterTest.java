
package com.example.demo.unit.converter;

import com.example.demo.converter.AbstractEnumConverter;
import com.example.demo.domain.Privacy;
import java.util.Arrays;
import java.util.List;
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
public class PrivacyConverterTest extends AbstractEnumConverterTest<Privacy, String> {
	
	private final List<String> INVALID_PRIVACY_FIELD_VALUES = Arrays.asList(
		"", "-", "Hidden", "Admins", "PRIVATE", "all"
	);

	@Autowired
	public PrivacyConverterTest(AbstractEnumConverter<Privacy, String> converter) {
		super(converter);
	}
	
	@Test
	@Override
	public void convertingInValidEnumFieldValueToEntityAttributeThrowsTest() {
		for (final String value : INVALID_PRIVACY_FIELD_VALUES) {
			assertThrows(
				UnsupportedOperationException.class, 
				() -> converter.convertToEntityAttribute(value),
				"Converting value '" + value + "' should throw"
			);
		}
	}
}
