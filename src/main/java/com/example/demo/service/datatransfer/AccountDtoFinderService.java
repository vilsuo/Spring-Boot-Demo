
package com.example.demo.service.datatransfer;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.service.AccountFinderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountDtoFinderService {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	public AccountDto findById(Long id) {
		return entityToDtoConverter.convertAccount(
			accountFinderService.findById(id)
		);
	}
	
	public AccountDto findByUsername(String username) {
		return entityToDtoConverter.convertAccount(
			accountFinderService.findByUsername(username)
		);
	}
	
	public boolean existsByUsername(String username) {
		return accountFinderService.existsByUsername(username);
	}
	
	public List<AccountDto> list() {
		return accountFinderService.list().stream()
				.map(entityToDtoConverter::convertAccount)
				.toList();
	}
}
