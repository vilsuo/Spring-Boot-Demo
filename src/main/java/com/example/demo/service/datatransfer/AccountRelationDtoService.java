
package com.example.demo.service.datatransfer;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountRelationService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountRelationDtoService {
	
	@Autowired
	private AccountRelationService accountRelationService;
	
	public List<RelationDto> getAccountRelationDtos(String username) {
		return accountRelationService.getAccountRelations(username)
				.stream()
				.map(EntityToDtoConverter::convertRelation)
				.toList();
	}
	
	public List<RelationDto> getRelationDtosToAccount(String username) {
		return accountRelationService.getRelationsToAccount(username)
				.stream()
				.map(EntityToDtoConverter::convertRelation)
				.toList();
	}
	
	public boolean hasRelationStatus(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		return accountRelationService.hasRelationStatus(
			sourceAccountUsername, targetAccountUsername, status
		);
	}
	
	@Transactional
    public Optional<RelationDto> createRelationToAccount(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		return EntityToDtoConverter.convertOptionalRelation(
			accountRelationService.createRelationToAccount(
				sourceAccountUsername, targetAccountUsername, status
			)
		);
    }
	
	@Transactional
	public void removeRelationFromAccount(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		accountRelationService.removeRelationFromAccount(
			sourceAccountUsername, targetAccountUsername, status
		);
	}
}
