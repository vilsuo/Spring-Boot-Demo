
package com.example.demo.service;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountRelationService {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private RelationService relationService;
	
	public List<RelationDto> getAccountsRelations(String username) {
		return accountFinderService.findByUsername(username).getRelationsTo()
				.stream()
				.map(EntityToDtoConverter::convertRelation)
				.toList();
	}
	
	public List<RelationDto> getRelationsToAccount(String username) {
		return accountFinderService.findByUsername(username).getRelationsFrom()
				.stream()
				.map(EntityToDtoConverter::convertRelation)
				.toList();
	}
	
	public boolean hasRelationStatus(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		Account source = accountFinderService.findByUsername(sourceAccountUsername);
		Account target = accountFinderService.findByUsername(targetAccountUsername);
		return relationService.relationExists(source, target, status);
	}
	
	@Transactional
    public Optional<RelationDto> createRelationToAccount(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		Account source = accountFinderService.findByUsername(sourceAccountUsername);
		Account target = accountFinderService.findByUsername(targetAccountUsername);
		return relationService.create(source, target, status);
    }
	
	@Transactional
	public void removeRelationFromAccount(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		Account source = accountFinderService.findByUsername(sourceAccountUsername);
		Account target = accountFinderService.findByUsername(targetAccountUsername);
		relationService.removeRelation(source, target, status);
	}
}
