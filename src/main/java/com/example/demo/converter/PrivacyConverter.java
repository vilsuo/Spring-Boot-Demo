
package com.example.demo.converter;

import com.example.demo.domain.Privacy;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PrivacyConverter implements AttributeConverter<Privacy, String> {

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
	
}
