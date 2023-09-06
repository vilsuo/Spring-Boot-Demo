
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
useless class?
*/
@Service
public class AccountRelationService {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private RelationCreatorService relationCreatorService;
	
	@Autowired
	private RelationFinderService relationFinderService;
	
	public List<Relation> getAccountRelations(final String username) {
		final Account account = accountFinderService.findByUsername(username);
		return relationFinderService.getRelationsFrom(account);
	}
	
	public List<Relation> getRelationsToAccount(final String username) {
		final Account account = accountFinderService.findByUsername(username);
		return relationFinderService.getRelationsTo(account);
	}
	
	public boolean hasRelationStatus(
			final String sourceAccountUsername,
			final String targetAccountUsername, final Status status) {
		
		final Account source = accountFinderService
			.findByUsername(sourceAccountUsername);
		
		final Account target = accountFinderService
			.findByUsername(targetAccountUsername);
		
		return relationFinderService.relationExists(source, target, status);
	}
	
	@Transactional
    public Optional<Relation> createRelationToAccount(
			final String sourceAccountUsername,
			final String targetAccountUsername, final Status status) {
		
		final Account source = accountFinderService
			.findByUsername(sourceAccountUsername);
		
		final Account target = accountFinderService
			.findByUsername(targetAccountUsername);
		
		return relationCreatorService.create(source, target, status);
    }
	
	@Transactional
	public void removeRelationFromAccount(
			final String sourceAccountUsername,
			final String targetAccountUsername, final Status status) {
		
		final Account source = accountFinderService
			.findByUsername(sourceAccountUsername);
		
		final Account target = accountFinderService
			.findByUsername(targetAccountUsername);
		
		relationCreatorService.removeRelation(source, target, status);
	}
}
