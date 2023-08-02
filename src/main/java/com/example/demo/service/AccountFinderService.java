
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.error.validation.ResourceNotFoundException;
import com.example.demo.service.repository.AccountRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountFinderService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	public Account findById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException(
				"Can not check if Account with null id exists"
			);
		}
		
		return accountRepository.findById(id).orElseThrow(
			() -> new ResourceNotFoundException("Account", "id", id.toString())
		);
	}
	
	public Account findByUsername(String username) {
		if (username == null) {
			throw new IllegalArgumentException(
				"Can not check if Account with null username exists"
			);
		}
		
		return accountRepository.findByUsername(username).orElseThrow(
			() -> new ResourceNotFoundException(
				"AccountWithRelation", "username", username
			)
		);
	}
	
	public boolean existsByUsername(String username) {
		if (username == null) {
			throw new IllegalArgumentException(
				"Tried to check if Account with null username exists"
			);
		}
		
		return accountRepository.existsByUsername(username);
	}
	
	
	public List<Account> list() {
		return accountRepository.findAll();
	}
}
