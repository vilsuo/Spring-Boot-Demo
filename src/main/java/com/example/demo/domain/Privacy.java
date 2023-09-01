
package com.example.demo.domain;

import java.util.HashMap;
import java.util.Map;

public enum Privacy {
	
	/*
	THIS OVERRIDES ALL VALUES!:
		-	Accounts with Role.ADMIN can see all resources
		-	if there is a Relation with Status.BLOCKED between the viewer 
			Account and the target Account, then the viewer Account can not 
			view the resource. 
	*/
	
	ALL,		// the viewer can always see the resource regardless if the 
				// viewer is logged in or not
	
	SIGNED,		// viewer Account must be signed in to see the resource
	
	FRIENDS,	// the viewer Account and the target Account must have a MUTUAL 
				// Relation with Status.FRIEND for the viewer Account to see
				// the resource
	
	PRIVATE;	// only the owner Account can view the resource
	
	private static final Map<String, Privacy> PRIVACY_MAP = new HashMap<>();
	
	public String getName() {
		return this.name();
	}
	
	/*
	Static initialization occurs top to bottom. Enums constants are 
	implicitly final static and are declared before the static initializer 
	block
	*/
	static {
        for (Privacy instance : Privacy.values()) {
			PRIVACY_MAP.put(instance.getName(), instance);
        }
	}
	
	public static Privacy getPrivacy(String name) {
		return PRIVACY_MAP.get(name);
	}
}
