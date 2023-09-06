
package com.example.demo.service.datatransfer;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Status;
import com.example.demo.service.RelationCreatorService;
import com.example.demo.service.RelationFinderService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelationDtoService {
	
	@Autowired
	private RelationCreatorService relationCreatorService;
	
	@Autowired
	private RelationFinderService relationFinderService;
	
	@Autowired
	private EntityToDtoConverter entityToDtoConverter;
	
	public List<RelationDto> getRelationsFrom(final Account account) {
		return relationFinderService.getRelationsFrom(account)
				.stream()
				.map(entityToDtoConverter::convertRelation)
				.toList();
	}
	
	public List<RelationDto> getRelationsTo(final Account account) {
		return relationFinderService.getRelationsTo(account)
				.stream()
				.map(entityToDtoConverter::convertRelation)
				.toList();
	}
	
	public boolean relationExists(
			final Account source, final Account target, final Status status) {
		
		return relationFinderService.relationExists(source, target, status);
	}
	
	public Optional<RelationDto> create(
			final Account source, final Account target, final Status status) {
		
		return entityToDtoConverter.convertOptionalRelation(
			relationCreatorService.create(source, target, status)
		);
	}
	
	public void removeRelation(final Account source, final Account target,
			final Status status) {
		
		relationFinderService.relationExists(source, target, status);
	}
}
