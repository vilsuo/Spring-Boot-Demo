
package com.example.demo.service.datatransfer;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Role;
import com.example.demo.service.AccountCreatorService;
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
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	public Optional<AccountDto> create(
			final AccountCreationDto accountCreationDto, final Role role) {
		
		return entityToDtoConverter.convertOptionalAccount(
			accountCreatorService.create(accountCreationDto, role)
		);
	}
}
