
package com.example.demo.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.service.repository.AccountRepository;
import com.example.demo.domain.Role;
import com.example.demo.error.validation.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private Validator validator;
	
	private AccountDto convertToDto(Account account) {
		if (account == null) {
			throw new NullPointerException("Tried to convert null Account param='account' to AccountDto.");
		}
		
		return new AccountDto(account.getId(), account.getUsername());
	}
	
	public Account findById(Long id) {
		if (id == null) {
			throw new NullPointerException("Tried to find Account by null param='id'.");
		}
		
		return accountRepository.findById(id).orElseThrow(
			() -> new ResourceNotFoundException("Account", "id", id.toString())
		);
	}
	
	public Account findByUsername(String username) {
		if (username == null) {
			throw new NullPointerException("Tried to find Account by null param='username'.");
		}
		
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
		if (username == null) {
			throw new NullPointerException("Tried to check if Account with null param='username' exists.");
		}
		
		return accountRepository.existsByUsername(username);
	}
	
	public Optional<AccountDto> createUSER(AccountCreationDto accountCreationDto) {
		return create(accountCreationDto, Role.USER);
	}
	
	@Secured("ADMIN")
	public Optional<AccountDto> createADMIN(AccountCreationDto accountCreationDto) {
		return create(accountCreationDto, Role.ADMIN);
	}
	
	/*
	returns an empty optional if account with the given username already exists
	*/
	@Transactional
	private Optional<AccountDto> create(AccountCreationDto accountCreationDto, Role role) {

		if (accountCreationDto == null) {
			throw new NullPointerException("Tried to create an Account with null param='accountCreationDto'.");
		}
		
		if (role == null) {
			throw new NullPointerException("Tried to create an Account with null param='role'.");
		}
		
		Set<ConstraintViolation<AccountCreationDto>> accountCreationDtoViolations
				= validator.validate(accountCreationDto);

        if (!accountCreationDtoViolations.isEmpty()) {
			throw new ConstraintViolationException(accountCreationDtoViolations);
        }
		
		String username = accountCreationDto.getUsername();
		String password = accountCreationDto.getPassword();
		
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

	@Transactional
    public void follow(Long fromId, Long toId) {
        Account fromAccount = findById(fromId);
        Account toAccount = findById(toId);
		
		fromAccount.addFollowing(toAccount);
		toAccount.addFollower(fromAccount);
    }
	
	@Transactional
    public void unfollow(Long fromId, Long toId) {
        Account fromAccount = findById(fromId);
        Account toAccount = findById(toId);
		
		fromAccount.removeFollowing(toAccount);
		toAccount.removeFollower(fromAccount);
    }
	
	/*
	Use in both ways.
	*/
	public boolean isFollowing(Long followerId, Long followedId) {
		if (followerId == null) {
			throw new NullPointerException("Account with null param='followerId' tried check if it follows other Account.");
			
		} else if (followedId == null) {
			throw new NullPointerException("Account tried to check if it follows other Account with null param='followedId'.");
		}
		
		return getFollowers(followedId).stream()
				.filter(follower -> Objects.equals(follower.getId(), followerId))
				.findFirst().isPresent();
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
