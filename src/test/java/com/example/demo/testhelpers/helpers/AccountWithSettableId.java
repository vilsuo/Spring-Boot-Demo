
package com.example.demo.testhelpers.helpers;

import com.example.demo.domain.Account;
import com.example.demo.domain.Role;

/*
delete class?
*/
public class AccountWithSettableId extends Account {
	

	public AccountWithSettableId(final Long id, final String username, 
			final String encodedPassword, final Role role) {
		
		super(username, encodedPassword, role);
		super.setId(id);
	}
}
