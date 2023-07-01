
package com.example.demo.service;

import com.example.demo.domain.AccountWithRelation;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import com.example.demo.service.repository.RelationRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelationService {
	
	@Autowired
	private RelationRepository relationRepository;
	
	public boolean relationExists(
			String sourceUsername, String targetUsername, Status status) {
		
		return !relationRepository
				.findBySource_UsernameAndTarget_Username(sourceUsername, targetUsername)
					.stream()
					.filter(relation -> relation.getStatus() == status)
					.toList()
					.isEmpty();
	}
	
	@Transactional
	public Optional<Relation> create(
			AccountWithRelation source, AccountWithRelation target, 
			Status status) {
		
		if (status == null) {
			throw new NullPointerException("Can save a Relation with null Status");
		}
		
		if (!relationExists(source.getUsername(), target.getUsername(), status)) {
			return Optional.of(
				relationRepository.save(new Relation(source, target, status))
			);
		} else {
			return Optional.empty();
		}
	}
	
	// Removes all relations with 'status' from source to target
	@Transactional
	public void removeRelation(
			String sourceAccountUsername, String targetAccountUsername, 
			Status status) {
		
		if (status == null) {
			throw new NullPointerException("Can not remove a null Status");
		}
		
		relationRepository.deleteAll(
			relationRepository.findBySource_UsernameAndTarget_Username(
					sourceAccountUsername, targetAccountUsername
			).stream()
			.filter(relation -> relation.getStatus() == status)
			.toList()
		);
	}
}
