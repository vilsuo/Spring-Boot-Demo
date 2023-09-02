package com.example.demo.service.repository;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
	
	List<Relation> findBySourceAndTarget(Account source, Account target);
	
	void deleteBySourceAndTargetAndStatus(
			Account source, Account target, Status status);
	
	boolean existsBySourceAndTargetAndStatus(
			Account source, Account target, Status status);
	
	List<Relation> findBySource(Account account);
	List<Relation> findByTarget(Account account);
}
