
package com.example.demo.domain;

public enum Role implements PersistableEnum<String> {
	
	USER("User"),	// Basic role for Account
	
	ADMIN("Admin");	// role for Account with more permissions
	
	private final String value;
	
	@Override
    public String getValue() {
        return value;
    }

    private Role(String value) {
        this.value = value;
    }
	
	public static boolean isAnonymous(final Account account) {
		return account == null;
	}
	
	/*
	private static final Map<String, Role> ROLE_MAP = new HashMap<>();
	
	public String getName() {
		return this.name();
	}
	
	//Static initialization occurs top to bottom. Enums constants are 
	//implicitly final static and are declared before the static initializer 
	//block
	static {
        for (Role instance : Role.values()) {
			ROLE_MAP.put(instance.getName(), instance);
        }
	}
	
	public static Role getRole(String name) {
		return ROLE_MAP.get(name);
	}
	*/
}
