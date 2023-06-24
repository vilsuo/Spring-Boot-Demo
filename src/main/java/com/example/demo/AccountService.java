
package com.example.demo;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/*
	Creates a new Account with role 'ROLE.USER'
	
	if username is already taken does not create account and then return false
	*/
	@Transactional
	public boolean create(String username, String password) {
		boolean usernameExists = accountRepository.existsByUsername(username);
		if (!usernameExists) {
			accountRepository.save(
				new Account(
						username,
						passwordEncoder.encode(password),
						Role.USER
				)
			);
		}
		return !usernameExists;
	}
	
	public boolean create(AccountDto accountDto) {
		return create(accountDto.getUsername(), accountDto.getPassword());
	}
	
	public Optional<Account> findByUsername(String username) {
		return Optional.ofNullable(accountRepository.findByUsername(username));
	}
	
	public boolean existsByUsername(String username) {
		return accountRepository.existsByUsername(username);
	}
	
	public Optional<Account> findById(Long id) {
		return accountRepository.findById(id);
	}
	
	public List<Account> list() {
		return accountRepository.findAll();
	}
}
