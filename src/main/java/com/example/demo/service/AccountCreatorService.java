
package com.example.demo.service;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Role;
import com.example.demo.service.repository.AccountRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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
			final AccountCreationDto accountCreationDto, final Role role) {
		
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
		
		final Set<ConstraintViolation<AccountCreationDto>> violations
			= validator.validate(accountCreationDto);

        if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
        }
		
		final boolean usernameIsTaken = accountFinderService
			.existsByUsername(accountCreationDto.getUsername());
		
		if (!usernameIsTaken) {
			final Account createdAccount = accountRepository.save(
				makeAccount(accountCreationDto, role)
			);
			return Optional.of(createdAccount);
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
			final AccountCreationDto accountCreationDto, final Role role) 
			throws IllegalArgumentException {
		
		if (accountCreationDto == null) {
			throw new IllegalArgumentException(
				"Tried to create an Account from null AccountCreationDto"
			);
		}
		
		return new Account(
				accountCreationDto.getUsername(),
				encodePassword(accountCreationDto.getPassword()),
				role
		);
	}
	
	private String encodePassword(final String password) {
		if (password == null) {
			throw new IllegalArgumentException(
				"Can not encode a null password"
			);
		}
		
		return passwordEncoder.encode(password);
	}
	
}
