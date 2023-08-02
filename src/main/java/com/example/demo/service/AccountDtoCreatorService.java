
package com.example.demo.service;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
IMPLEMENT POSSIBLY IN FUTURE:
	- delete account
*/

@Service
public class AccountDtoCreatorService {
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	public Optional<AccountDto> create(
			AccountCreationDto accountCreationDto, Role role) {
		
		Optional<Account> opt = accountCreatorService.create(
			accountCreationDto, role
		);
		
		if (opt.isPresent()) {
			return Optional.ofNullable(
				EntityToDtoConverter.convertAccount(opt.get())
			);
		} else {
			return Optional.empty();
		}
	}
}
