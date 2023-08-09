
package com.example.demo.service.datatransfer;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Status;
import com.example.demo.service.RelationService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelationDtoService {
	
	@Autowired
	private RelationService relationService;
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	public List<RelationDto> getRelationsFrom(Account account) {
		return relationService.getRelationsFrom(account)
				.stream()
				.map(entityToDtoConverter::convertRelation)
				.toList();
	}
	
	public List<RelationDto> getRelationsTo(Account account) {
		return relationService.getRelationsTo(account)
				.stream()
				.map(entityToDtoConverter::convertRelation)
				.toList();
	}
	
	public boolean relationExists(
			Account source, Account target, Status status) {
		
		return relationService.relationExists(source, target, status);
	}
	
	public Optional<RelationDto> create(
			Account source, Account target, Status status) {
		
		return entityToDtoConverter.convertOptionalRelation(
			relationService.create(source, target, status)
		);
	}
	
	public void removeRelation(Account source, Account target, Status status) {
		relationService.relationExists(source, target, status);
	}
}
