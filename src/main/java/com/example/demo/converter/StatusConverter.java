
package com.example.demo.converter;

import com.example.demo.domain.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
	
	@Override
	public String convertToDatabaseColumn(Status status) {
		if (status == null) {
			return null;
		}
		return status.getName();
	}

	@Override
	public Status convertToEntityAttribute(String name) {
		Status status = Status.getStatus(name);
		if (status == null) {
			throw new IllegalArgumentException();
		}
		return status;
	}
}
