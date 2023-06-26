
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.AccountDto;
import com.example.demo.service.repository.AccountRepository;
import com.example.demo.domain.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
						Role.USER,
						new ArrayList<>()
						//new HashSet<>()
						//new HashSet<>()
				)
			);
		}
		return !usernameExists;
	}
	
	public boolean create(AccountDto accountDto) {
		return create(accountDto.getUsername(), accountDto.getPassword());
	}
	
	public void save(Account account) {
		System.out.println("enter save");
		Optional<Account> existing = accountRepository.findById(account.getId());
		if (existing.isPresent()) {
			System.out.println("is present");
			account.setFollowing(existing.get().getFollowing());
		} else {
			System.out.println("is not present");
		}
		accountRepository.save(account);
		System.out.println("exit save");
	}
	
	public Optional<Account> findById(Long id) {
		return accountRepository.findById(id);
	}
	
	public Optional<Account> findByUsername(String username) {
		return Optional.ofNullable(accountRepository.findByUsername(username));
	}
	
	public boolean existsByUsername(String username) {
		return accountRepository.existsByUsername(username);
	}
	
	@Transactional
	public void follow(Long followerAccountId, Long followedAccounId) {
		System.out.println("enter serv.follow");
		Account sourceAccount = findById(followerAccountId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such source account id: " + followerAccountId)
		);
		Account targetAccount = findById(followedAccounId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + followedAccounId)
		);
		
		sourceAccount.getFollowing().add(targetAccount);
		//accountRepository.save(sourceAccount);
		save(sourceAccount);
		
		System.out.println("'" + sourceAccount.getUsername() + "' successfully followed '" + targetAccount.getUsername() + "'");
		System.out.println("exit serv.follow");
	}
	
	public List<Account> list() {
		return accountRepository.findAll();
	}
}
