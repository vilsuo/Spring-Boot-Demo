
package com.example.demo.converter;

import com.example.demo.domain.Privacy;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class PrivacyConverter extends AbstractEnumConverter<Privacy, String> {
	// implements AttributeConverter<Privacy, String> {

	public PrivacyConverter() {
		super(Privacy.class);
	}

	/*
	@Override
	public String convertToDatabaseColumn(Privacy privacy) {
		if (privacy == null) {
			return null;
		}
		return privacy.getName();
	}

	@Override
	public Privacy convertToEntityAttribute(String name) {
		Privacy privacy = Privacy.getPrivacy(name);
		if (privacy == null) {
			throw new IllegalArgumentException();
		}
		return privacy;
	}
	*/
}
