
package com.example.demo.service;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelationDtoService {
	
	@Autowired
	private RelationService relationService;
	
	public List<RelationDto> getRelationsFrom(Account account) {
		return relationService.getRelationsFrom(account)
				.stream()
				.map(EntityToDtoConverter::convertRelation)
				.toList();
	}
	
	public List<RelationDto> getRelationsTo(Account account) {
		return relationService.getRelationsTo(account)
				.stream()
				.map(EntityToDtoConverter::convertRelation)
				.toList();
	}
	
	public boolean relationExists(
			Account source, Account target, Status status) {
		
		return relationService.relationExists(source, target, status);
	}
	
	public Optional<RelationDto> create(
			Account source, Account target, Status status) {
		
		Optional<Relation> optional = relationService.create(
			source, target, status
		);
		
		if (optional.isPresent()) {
			return Optional.of(
				EntityToDtoConverter.convertRelation(optional.get())
			);
		} else {
			return Optional.empty();
		}
	}
	
	public void removeRelation(Account source, Account target, Status status) {
		relationService.relationExists(source, target, status);
	}
}
