package com.example.demo.service.repository;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
	
	List<Relation> findBySourceAndTarget(Account source, Account target);
}
