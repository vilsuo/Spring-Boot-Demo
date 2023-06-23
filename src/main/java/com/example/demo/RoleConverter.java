
package com.example.demo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

	@Override
	public String convertToDatabaseColumn(Role role) {
		if (role == null) {
			return null;
		}
		return role.getName();
	}

	@Override
	public Role convertToEntityAttribute(String name) {
		Role role = Role.getRole(name);
		if (role == null) {
			throw new IllegalArgumentException();
		}
		return role;
	}
	
}
