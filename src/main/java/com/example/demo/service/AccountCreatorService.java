
package com.example.demo.service;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.service.repository.AccountRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
IMPLEMENT POSSIBLY IN FUTURE:
	- delete account
*/

@Service
public class AccountCreatorService {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private Validator validator;
	
	public Optional<AccountDto> createAndGetDto(
			AccountCreationDto accountCreationDto, Role role) {
		
		Optional<Account> opt = create(accountCreationDto, role);
		if (opt.isPresent()) {
			return Optional.ofNullable(
				EntityToDtoConverter.convertAccount(opt.get())
			);
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Create an Account in the Database
	 * 
	 * @param accountCreationDto
	 * 
	 * @param role
	 * 
	 * @return 
	 * An empty optional if an Account with the given username already exists, 
	 * else the Optional containing the created Account converted to Data 
	 * Transfer Object
	 */
	@Transactional
	public Optional<Account> create(
			AccountCreationDto accountCreationDto, Role role) {
		
		if (accountCreationDto == null) {
			throw new IllegalArgumentException(
				"Tried to create an Account from null AccountCreationDto"
			);
		}
		
		if (role == null) {
			throw new IllegalArgumentException(
				"Tried to create an Account with null Role"
			);
		}
		
		Set<ConstraintViolation<AccountCreationDto>> violations
			= validator.validate(accountCreationDto);

        if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
        }
		
		boolean usernameIsTaken = accountFinderService.existsByUsername(
			accountCreationDto.getUsername()
		);
		if (!usernameIsTaken) {
			Account createdAccount = accountRepository.save(
				makeAccount(accountCreationDto, role)
			);
			return Optional.of(createdAccount);
			/*
			return Optional.ofNullable(
				EntityToDtoConverter.convertAccount(createdAccount)
			);
			*/
		}
		return Optional.empty();
	}
	
	/**
	 * Creates an Account from AccountCreationDto and Role. DOES NOT SAVE
	 * CREATED ACCOUNT TO THE DATABASE!
	 * 
	 * @param accountCreationDto
	 * Created Account will receive the username and password from this 
	 * parameter. This method will encrypt the password.
	 * 
	 * @param role 
	 * A Role to be granted to the created Account.
	 * 
	 * @return 
	 * The created Account.
	 * 
	 * @throws IllegalArgumentException	
	 * If AccountCreationDto is null or the password to be encoded is null.
	 */
	private Account makeAccount(
			AccountCreationDto accountCreationDto, Role role) 
			throws IllegalArgumentException {
		
		if (accountCreationDto == null) {
			throw new IllegalArgumentException(
				"Tried to create an Account from null AccountCreationDto"
			);
		}
		
		return new Account(
				accountCreationDto.getUsername(),
				encodePassword(accountCreationDto.getPassword()),
				role,
				new HashSet<>(),
				new HashSet<>(),
				new HashSet<>()
		);
	}
	
	public String encodePassword(String password) {
		if (password == null) {
			throw new IllegalArgumentException("Can not encode a null password");
		}
		
		return passwordEncoder.encode(password);
	}
	
}
