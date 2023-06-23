
package com.example.demo;

import java.util.Arrays;
import java.util.Collection;
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
	
	@Transactional
	public boolean create(String username, String password) {
		boolean usernameExists = accountRepository.existsByUsername(username);
		if (!usernameExists) {
			accountRepository.save(
				new Account(username, passwordEncoder.encode(password), Role.ADMIN)//Arrays.asList(Role.USER))
			);
		}
		return !usernameExists;
	}
	
	public Optional<Account> findByUsername(String username) {
		return Optional.ofNullable(accountRepository.findByUsername(username));
	}
	
	public List<Account> list() {
		return accountRepository.findAll();
	}
}
