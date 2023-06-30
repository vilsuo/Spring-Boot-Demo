
package com.example.demo.domain;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	FRIEND, BLOCKED;
	
	private static final Map<String, Status> STATUS_MAP = new HashMap<>();
	
	public String getName() {
		return this.name();
	}
	
	/*
	Static initialization occurs top to bottom. Enums constants are 
	implicitly final static and are declared before the static initializer 
	block
	*/
	static {
        for (Status instance : Status.values()) {
			STATUS_MAP.put(instance.getName(), instance);
        }
	}
	
	public static Status getStatus(String status) {
		return STATUS_MAP.get(status);
	}
}
