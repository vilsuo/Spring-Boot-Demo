
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
			AccountWithRelation source, AccountWithRelation target, Status status) {
		
		System.out.println("Enter: RelationService.relationExists");
		
		System.out.println("Enter: RelationRepository,findBySourceAndTarget");
		System.out.println("Exit: RelationRepository,findBySourceAndTarget");
		
		System.out.println("Exit: RelationService.relationExists");
		return !relationRepository
				.findBySourceAndTarget(source, target)
					.stream()
					.filter(relation -> relation.getStatus() == status)
					.toList()
					.isEmpty();
	}
	
	@Transactional
	public Optional<Relation> create(
			AccountWithRelation source, AccountWithRelation target, 
			Status status) {
		
		System.out.println("Enter: RelationService.create");
		if (status == null) {
			throw new NullPointerException("Can not create a Relation with null Status");
		}
		
		if (!relationExists(source, target, status)) {
			System.out.println("Enter: RelationRepository.save");
			System.out.println("Exit: RelationRepository.save");
			System.out.println("Exit: RelationService.create");
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
			AccountWithRelation source, AccountWithRelation target, 
			Status status) {
		
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
