
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.repository.RelationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
TODO
- common relations between accounts?

- make separate relation creator/finder services?
*/
@Service
public class RelationService {
	
	@Autowired
	private RelationRepository relationRepository;
	
	public List<Relation> getRelationsFrom(final Account account) {
		return relationRepository.findBySource(account);
	}
	
	public List<Relation> getRelationsTo(final Account account) {
		return relationRepository.findByTarget(account);
	}
	
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
	
	/**
	 * Can not create {@code Relation Relations} from {@link Account} to itself
	 * 
	 * @param source
	 * @param target
	 * @param status
	 * @return 
	 */
	@Transactional
	public Optional<Relation> create(
			final Account source, final Account target, final Status status) {
		
		if (status == null) {
			throw new IllegalArgumentException(
				"Can not create a Relation with null Status"
			);
		}
		
		if (source.equals(target)) {
			throw new IllegalArgumentException(
				"Can not create Relation from Account to itself"
			);
		}
		
		if (!relationExists(source, target, status)) {
			final Relation relation = relationRepository
				.save(new Relation(source, target, status));
			
			// not needed?
			//source.getRelationsTo().add(relation);
			//target.getRelationsFrom().add(relation);
			
			return Optional.of(relation);
			
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Removes all {@link Relation Relations} from source {@link Account} to 
	 * target {@code Account} with given {@link Status}
	 * 
	 * @param source
	 * @param target
	 * @param status 
	 */
	@Transactional
	public void removeRelation(
			final Account source, final Account target, final Status status) {
		
		if (status == null) {
			throw new IllegalArgumentException("Can not remove a null Status");
		}
		
		relationRepository
			.deleteBySourceAndTargetAndStatus(source, target, status);
	}
	
	public List<Relation> getRelationsFromSourceToTarget(
			final Account source, final Account target) {
		
		return relationRepository
				.findBySourceAndTarget(source, target);
	}
}
