
package com.example.demo.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.error.validation.ResourceNotFoundException;
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
import com.example.demo.service.repository.AccountRepository;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/*
- split services better?
- in which methods to add annotation @Transactional?

- relationDto and FileObjectDto?
	- implement with accountDto
*/
@Service
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private RelationService relationService;
	
	@Autowired
	private FileObjectService fileObjectService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private Validator validator;
	
	
	private AccountDto convertToDto(Account account) {
		if (account == null) {
			throw new NullPointerException(
				"Tried to convert null Account param='account' to AccountDto."
			);
		}
		
		return new AccountDto(account.getId(), account.getUsername());
	}
	
	// only used by findByDto
	public Account findById(Long id) {
		if (id == null) {
			throw new NullPointerException(
				"Tried to find Account by null param='id'."
			);
		}
		
		return accountRepository.findById(id).orElseThrow(
			() -> new ResourceNotFoundException("Account", "id", id.toString())
		);
	}
	
	// used by custom user details service
	public Account findByUsername(String username) {
		if (username == null) {
			throw new NullPointerException(
				"Tried to find AccountWithRelation by null param='username'."
			);
		}
		
		return accountRepository.findByUsername(username).orElseThrow(
			() -> new ResourceNotFoundException(
				"AccountWithRelation", "username", username
			)
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
			throw new NullPointerException(
				"Tried to check if Account with null param='username' exists."
			);
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
	private Optional<AccountDto> create(
			AccountCreationDto accountCreationDto, Role role) {
		
		if (accountCreationDto == null) {
			throw new NullPointerException(
				"Tried to create an Account from null AccountCreationDto."
			);
		}
		
		if (role == null) {
			throw new NullPointerException(
				"Tried to create an Account with null Role."
			);
		}
		
		Set<ConstraintViolation<AccountCreationDto>> accountCreationDtoViolations
				= validator.validate(accountCreationDto);

        if (!accountCreationDtoViolations.isEmpty()) {
			throw new ConstraintViolationException(accountCreationDtoViolations);
        }
		
		String username = accountCreationDto.getUsername();
		String password = accountCreationDto.getPassword();
		
		if (!existsByUsername(username)) {
			Account createdAccount = accountRepository.save(
				new Account(
						username,
						passwordEncoder.encode(password),
						role,
						new HashSet<>(),
						new HashSet<>(),
						new HashSet<>()
				)
			);
			return Optional.ofNullable(convertToDto(createdAccount));
		}
		return Optional.empty();
	}
	
	public Set<Relation> getAccountsRelations(String username) {
		return findByUsername(username).getRelationsTo();
	}
	
	public Set<Relation> getRelationsToAccount(String username) {
		return findByUsername(username).getRelationsFrom();
	}
	
	public boolean hasRelationStatus(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		Account source = findByUsername(sourceAccountUsername);
		Account target = findByUsername(targetAccountUsername);
		
		return relationService.relationExists(source, target, status);
	}
	
	@Transactional
    public void createRelationToAccount(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		Account source = findByUsername(sourceAccountUsername);
		Account target = findByUsername(targetAccountUsername);
		Optional<Relation> opt = relationService.create(source, target, status);
		
		if (opt.isPresent()) {
			// why are these needed? Service Tests does not pass otherwise
			Relation relation = opt.get();
			source.getRelationsTo().add(relation);
			target.getRelationsFrom().add(relation);
		}
    }
	
	@Transactional
	public void removeRelationFromAccount(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		Account source = findByUsername(sourceAccountUsername);
		Account target = findByUsername(targetAccountUsername);
		
		relationService.removeRelation(source, target, status);
	}
	
	@Transactional
	public void createImageToAccount(String username, MultipartFile file) throws IOException {
		fileObjectService.create(findByUsername(username), file);
	}
	
	@Transactional
	public List<FileObject> getAccountImages(String username) {
		return fileObjectService.getAccountImages(findByUsername(username));
	}
	
	public List<AccountDto> list() {
		return accountRepository.findAll().stream()
				.map(account -> convertToDto(account))
				.collect(Collectors.toList());
	}
}
