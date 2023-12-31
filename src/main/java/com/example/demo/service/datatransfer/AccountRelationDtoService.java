
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
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	public List<RelationDto> getAccountRelationDtos(final String username) {
		return accountRelationService.getAccountRelations(username)
				.stream()
				.map(entityToDtoConverter::convertRelation)
				.toList();
	}
	
	public List<RelationDto> getRelationDtosToAccount(final String username) {
		return accountRelationService.getRelationsToAccount(username)
				.stream()
				.map(entityToDtoConverter::convertRelation)
				.toList();
	}
	
	public boolean hasRelationStatus(
			final String sourceAccountUsername,
			final String targetAccountUsername, final Status status) {
		
		return accountRelationService.hasRelationStatus(
			sourceAccountUsername, targetAccountUsername, status
		);
	}
	
	@Transactional
    public Optional<RelationDto> createRelationToAccount(
			final String sourceAccountUsername,
			final String targetAccountUsername, final Status status) {
		
		return entityToDtoConverter.convertOptionalRelation(
			accountRelationService.createRelationToAccount(
				sourceAccountUsername, targetAccountUsername, status
			)
		);
    }
	
	@Transactional
	public void removeRelationFromAccount(
			final String sourceAccountUsername,
			final String targetAccountUsername, final Status status) {
		
		accountRelationService.removeRelationFromAccount(
			sourceAccountUsername, targetAccountUsername, status
		);
	}
}
