
package com.example.demo.converter;

import com.example.demo.domain.Role;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class RoleConverter extends AbstractEnumConverter<Role, String> {
	//implements AttributeConverter<Role, String> {

	public RoleConverter() {
		super(Role.class);
	}
	/*
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
	*/
}
