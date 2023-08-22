
package com.example.demo.testhelpers.helpers;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

/*
useful class?
*/
@AllArgsConstructor @Data
public class AccountCreationDtoRolePair {
	
	private AccountCreationDto accountCreationDto;
	private Role role;
}
