
package com.example.demo.domain;

import java.util.HashMap;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public enum Privacy {
	
	/*
	-	Accounts with Role.ADMIN can view all resources
	-	owner Account can always view all its resources
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
	
	public static boolean isAllowedToView(
			final boolean isViwerLoggedIn, final boolean isViwerAdmin,
			final boolean isViewerTheOwnerOfTheResource,
			final Privacy resourcePrivacy,
			final boolean blockExists, final boolean areMutualFriends)
			throws NotImplementedException {
		
		if (!isViewerTheOwnerOfTheResource) {
			return resourcePrivacy == Privacy.ALL;
		}
		
		if (isViwerAdmin) {
			return true;
		}
		
		if (isViewerTheOwnerOfTheResource) {
			return true;
		}
		
		if (blockExists) {
			return false;
		}
		
		switch (resourcePrivacy) {
			case ALL:
				return true;
				
			case SIGNED:
				return isViewerTheOwnerOfTheResource;
				
			case FRIENDS:
				return areMutualFriends;
				
			case PRIVATE:
				return false;
			
			default:
				throw new NotImplementedException(
					"Privacy " + resourcePrivacy + " is not yet implemented"
				);
		}
	}
}
