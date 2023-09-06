
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.repository.RelationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
TODO
- common relations between accounts?
*/
@Service
public class RelationFinderService {
	
	@Autowired
	private RelationRepository relationRepository;
	
	public boolean relationExists(
			final Account source, final Account target, final Status status) {
		
		if (status == null) {
			throw new IllegalArgumentException(
				"Can not check if a Relation with null Status exists"
			);
		}
		
		return relationRepository
			.existsBySourceAndTargetAndStatus(source, target, status);
	}
	
	public boolean relationExistsAtleastOneWay(final Account first, 
			final Account second, final Status status) {
		
		return relationExists(first, second, status)
			|| relationExists(second, first, status);
	}
	
	public boolean relationExistsBothWays(final Account first, 
			final Account second, final Status status) {
		
		return relationExists(first, second, status)
			&& relationExists(second, first, status);
	}
	
	public List<Relation> getRelationsFrom(final Account account) {
		return relationRepository.findBySource(account);
	}
	
	public List<Relation> getRelationsTo(final Account account) {
		return relationRepository.findByTarget(account);
	}
	
	public List<Relation> getRelationsFromSourceToTarget(
			final Account source, final Account target) {
		
		return relationRepository
			.findBySourceAndTarget(source, target);
	}
	
}
