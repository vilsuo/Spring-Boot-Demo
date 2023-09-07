
package com.example.demo.domain;

public enum Status implements PersistableEnum<String> {
	
	FRIEND("Friend"),	// allows viewing more content between Accounts
	
	BLOCKED("Blocked");	// allows hiding content from Account to another
	
	private final String value;

    @Override
    public String getValue() {
        return value;
    }

    private Status(String value) {
        this.value = value;
    }
}
