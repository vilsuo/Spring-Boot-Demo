
package com.example.demo.service;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.AccountDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountDtoFinderService {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	public AccountDto findById(Long id) {
		return EntityToDtoConverter.convertAccount(
			accountFinderService.findById(id)
		);
	}
	
	public AccountDto findByUsername(String username) {
		return EntityToDtoConverter.convertAccount(
			accountFinderService.findByUsername(username)
		);
	}
	
	public boolean existsByUsername(String username) {
		return accountFinderService.existsByUsername(username);
	}
	
	public List<AccountDto> list() {
		return accountFinderService.list().stream()
				.map(EntityToDtoConverter::convertAccount)
				.toList();
	}
}
