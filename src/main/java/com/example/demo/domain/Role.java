
package com.example.demo.domain;

import java.util.HashMap;
import java.util.Map;

public enum Role {
	
	USER,	// Basic role for Account
	
	ADMIN;	// role for Account with more permissions
	
	private static final Map<String, Role> ROLE_MAP = new HashMap<>();
	
	public String getName() {
		return this.name();
	}
	
	/*
	Static initialization occurs top to bottom. Enums constants are 
	implicitly final static and are declared before the static initializer 
	block
	*/
	static {
        for (Role instance : Role.values()) {
			ROLE_MAP.put(instance.getName(), instance);
        }
	}
	
	public static Role getRole(String name) {
		return ROLE_MAP.get(name);
	}
	
}
