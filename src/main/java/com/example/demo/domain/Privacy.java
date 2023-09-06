
package com.example.demo.domain;

import java.util.HashMap;
import java.util.Map;

public enum Privacy {
	
	/*
	THESE RULES APPLY TO ALL VALUES
	-	Accounts with Role.ADMIN can view all resources unless the viewer 
		blocks the owner of the resource.
	-	Account can always view all its own resources.
	-	If there exists a Relation with Status.BLOCKED between the viewer 
		Account and the target Account, then the viewer Account can not 
		view the resources of the target Account. 
	*/
	
	ALL,		// the viewer can always see the resource regardless if the 
				// viewer is logged in or not
	
	FRIENDS,	// the viewer Account and the owner of the resource Account 
				// must have a MUTUAL Relation with Status.FRIEND for the 
				// viewer Account to see the resource
	
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
