
package com.example.demo.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.service.repository.AccountRepository;
import com.example.demo.domain.Role;
import com.example.demo.error.validation.ResourceNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
- throw something else instead of null pointer exception?
*/
@Service
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private AccountDto convertToDto(Account account) {
		if (account == null) {
			return null;
		}
		
		return new AccountDto(account.getId(), account.getUsername());
	}
	
	public Account findById(Long id) {
		if (id == null) { throw new NullPointerException(); }
		
		return accountRepository.findById(id).orElseThrow(
			() -> new ResourceNotFoundException("Account", "id", id.toString())
		);
	}
	
	public Account findByUsername(String username) {
		if (username == null) { throw new NullPointerException(); }
		
		return accountRepository.findByUsername(username).orElseThrow(
			() -> new ResourceNotFoundException("Account", "username", username)
		);
	}
	
	public AccountDto findDtoById(Long id) {
		return convertToDto(findById(id));
	}
	
	public AccountDto findDtoByUsername(String username) {
		return convertToDto(findByUsername(username));
	}
	
	public boolean existsByUsername(String username) {
		if (username == null) { throw new NullPointerException(); }
		
		return accountRepository.existsByUsername(username);
	}
	
	public Optional<AccountDto> createUSER(AccountCreationDto accountCreationDto) {
		return create(accountCreationDto.getUsername(), accountCreationDto.getPassword(), Role.USER);
	}
	
	@Secured("ADMIN")
	public Optional<AccountDto> createADMIN(AccountCreationDto accountCreationDto) {
		return create(accountCreationDto.getUsername(), accountCreationDto.getPassword(), Role.ADMIN);
	}
	
	// check for null Role?
	@Transactional
	private Optional<AccountDto> create(String username, String password, Role role) {
		if (!accountRepository.existsByUsername(username)) {
			Account createdAccount = accountRepository.save(
				new Account(
						username,
						passwordEncoder.encode(password),
						role,
						new HashSet<>(),
						new HashSet<>()
				)
			);
			return Optional.ofNullable(convertToDto(createdAccount));
		}
		return Optional.empty();
	}
	
	/*
	// remove?
	@Transactional
    public void follow(String fromUsername, String toUsername) {
        Account fromAccount = findByUsername(fromUsername);
        Account toAccount = findByUsername(toUsername);
		
        fromAccount.addFollower(toAccount);
    }
	*/
	
	@Transactional
    public void follow(Long fromId, Long toId) {
        Account fromAccount = findById(fromId);
        Account toAccount = findById(toId);
		
        fromAccount.addFollower(toAccount);
    }
	
    public Set<AccountDto> getFollowers(Long accountId) {
		return findById(accountId).getFollowers().stream()
				.map(account -> convertToDto(account))
				.collect(Collectors.toSet());
		
    }
	
    public Set<AccountDto> getFollowing(Long accountId) {
        return findById(accountId).getFollowing().stream()
				.map(account -> convertToDto(account))
				.collect(Collectors.toSet());
    }
	
	public List<AccountDto> list() {
		return accountRepository.findAll().stream()
				.map(account -> convertToDto(account))
				.collect(Collectors.toList());
	}
}
