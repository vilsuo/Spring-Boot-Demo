
package com.example.demo.testhelpers;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import java.util.HashSet;

public class AccountWithSettableId extends Account {

	public AccountWithSettableId(
			Long id, AccountCreationDto accountCreationDto, Role role) {
		
		super(
			accountCreationDto.getUsername(),
			accountCreationDto.getPassword(),
			role,
			new HashSet<>(), new HashSet<>(), new HashSet<>()
		);
		
		super.setId(id);
	}
}
