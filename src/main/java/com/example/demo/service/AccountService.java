
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.service.repository.AccountRepository;
import com.example.demo.domain.Role;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
						new HashSet<>(),
						new HashSet<>()
				)
			);
		}
		return !usernameExists;
	}
	
	public boolean create(AccountDto accountDto) {
		return create(accountDto.getUsername(), accountDto.getPassword());
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
    public void follow(String username, String usernameToFollow) {
        Account account = findByUsername(username).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + username)
		);
        Account accountToFollow = findByUsername(usernameToFollow).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + usernameToFollow)
		);
        account.addFollower(accountToFollow);
    }
	
	/*
	@Transactional
    public void follow(Long userId, Long toFollowId) {
        Account account = accountRepository.findById(userId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + userId)
		);
        Account accountToFollow = accountRepository.findById(toFollowId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + toFollowId)
		);
        account.addFollower(accountToFollow);
    }
	*/
	
	@Transactional
    public void unfollow(Long userId, Long toUnfollowId) {
		Account account = accountRepository.findById(userId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + userId)
		);
        Account accountToUnfollow = accountRepository.findById(toUnfollowId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + toUnfollowId)
		);
        account.removeFollower(accountToUnfollow);
    }
	
	@Transactional
    public Set<Account> getFollowers(Long userId) {
        return accountRepository.findById(userId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + userId)
		).getFollowers();
    }
    @Transactional
    public Set<Account> getFollowing(Long userId) {
        return accountRepository.findById(userId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such target account id: " + userId)
		).getFollowing();
    }
	
	public List<Account> list() {
		return accountRepository.findAll();
	}
}
