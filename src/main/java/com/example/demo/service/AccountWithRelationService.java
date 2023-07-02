
package com.example.demo.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.AccountWithRelation;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.error.validation.ResourceNotFoundException;
import com.example.demo.service.repository.AccountWithRelationRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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

@Service
public class AccountWithRelationService {
	
	@Autowired
	private AccountWithRelationRepository accountRepository;
	
	@Autowired
	private RelationService relationService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private Validator validator;
	
	
	private AccountDto convertToDto(AccountWithRelation account) {
		if (account == null) {
			throw new NullPointerException("Tried to convert null AccountWithRelation param='account' to AccountDto.");
		}
		
		return new AccountDto(account.getId(), account.getUsername());
	}
	
	// only used by findByDto
	public AccountWithRelation findById(Long id) {
		if (id == null) {
			throw new NullPointerException("Tried to find Account by null param='id'.");
		}
		
		return accountRepository.findById(id).orElseThrow(
			() -> new ResourceNotFoundException("AccountWithRelation", "id", id.toString())
		);
	}
	
	// used by custom user details service
	public AccountWithRelation findByUsername(String username) {
		System.out.println("Enter: AccountService.findByUsername");
		if (username == null) {
			throw new NullPointerException("Tried to find AccountWithRelation by null param='username'.");
		}
		
		System.out.println("Enter: AccountRepository.findByUsername");
		System.out.println("Exit: AccountRepository.findByUsername");
		System.out.println("Exit: AccountService.findByUsername");
		return accountRepository.findByUsername(username).orElseThrow(
			() -> new ResourceNotFoundException("AccountWithRelation", "username", username)
		);
	}
	
	// only used in testing
	public AccountDto findDtoById(Long id) {
		return convertToDto(findById(id));
	}
	
	public AccountDto findDtoByUsername(String username) {
		return convertToDto(findByUsername(username));
	}
	
	public boolean existsByUsername(String username) {
		if (username == null) {
			throw new NullPointerException("Tried to check if AccountWithRelation with null param='username' exists.");
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
	
	// returns an empty optional if account with the given username already exists
	@Transactional
	private Optional<AccountDto> create(AccountCreationDto accountCreationDto, Role role) {
		if (accountCreationDto == null) {
			throw new NullPointerException("Tried to create an Account from null AccountCreationDto.");
		}
		
		if (role == null) {
			throw new NullPointerException("Tried to create an Account with null Role.");
		}
		
		Set<ConstraintViolation<AccountCreationDto>> accountCreationDtoViolations
				= validator.validate(accountCreationDto);

        if (!accountCreationDtoViolations.isEmpty()) {
			throw new ConstraintViolationException(accountCreationDtoViolations);
        }
		
		String username = accountCreationDto.getUsername();
		String password = accountCreationDto.getPassword();
		
		if (!existsByUsername(username)) {
			AccountWithRelation createdAccount = accountRepository.save(
				new AccountWithRelation(
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
	
	public Set<Relation> getAccountsRelations(String username) {
		System.out.println("Enter AccountService.getAccountsRelations");
		System.out.println("Exit AccountService.getAccountsRelations");
		return findByUsername(username).getRelationsTo();
	}
	
	public Set<Relation> getRelationsToAccount(String username) {
		System.out.println("Enter AccountService.getRelationsToAccount");
		System.out.println("Exit AccountService.getRelationsToAccount");
		return findByUsername(username).getRelationsFrom();
	}
	
	@Transactional
    public void addRelationToAccount(String sourceAccountUsername, String targetAccountUsername, Status status) {
		System.out.println("Enter: AccountService.addRelationToAccount");
		AccountWithRelation source = findByUsername(sourceAccountUsername);
		AccountWithRelation target = findByUsername(targetAccountUsername);
		Optional<Relation> opt = relationService.create(source, target, status);
		
		if (opt.isPresent()) {
			// why are these needed? Service Tests does not pass otherwise
			Relation relation = opt.get();
			source.getRelationsTo().add(relation);
			target.getRelationsFrom().add(relation);
		}
		
		System.out.println("Exit: AccountService.addRelationToAccount");
    }
	
	@Transactional
	public void removeRelationFromAccount(String sourceAccountUsername, String targetAccountUsername, Status status) {
		AccountWithRelation source = findByUsername(sourceAccountUsername);
		AccountWithRelation target = findByUsername(targetAccountUsername);
		
		relationService.removeRelation(source, target, status);
	}
	
	public List<AccountDto> list() {
		return accountRepository.findAll().stream()
				.map(account -> convertToDto(account))
				.collect(Collectors.toList());
	}
}
