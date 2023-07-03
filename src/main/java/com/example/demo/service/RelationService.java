
package com.example.demo.service;

import com.example.demo.domain.Account;
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
	
	
	public boolean relationExists(Account source, Account target, Status status) {
		return !relationRepository
				.findBySourceAndTarget(source, target)
					.stream()
					.filter(relation -> relation.getStatus() == status)
					.toList()
					.isEmpty();
	}
	
	@Transactional
	public Optional<Relation> create(Account source, Account target, Status status) {
		if (status == null) {
			throw new NullPointerException("Can not create a Relation with null Status");
		}
		
		if (!relationExists(source, target, status)) {
			return Optional.of(
				relationRepository.save(new Relation(source, target, status))
			);
		} else {
			return Optional.empty();
		}
	}
	
	// Removes all relations with 'status' from source to target
	@Transactional
	public void removeRelation(Account source, Account target, Status status) {
		if (status == null) {
			throw new NullPointerException("Can not remove a null Status");
		}
		
		relationRepository.deleteAll(
			relationRepository.findBySourceAndTarget(source, target).stream()
					.filter(relation -> relation.getStatus() == status)
					.toList()
		);
	}
}
