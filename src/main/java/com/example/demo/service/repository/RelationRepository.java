package com.example.demo.service.repository;

import com.example.demo.domain.Relation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
	/*
	List<Relation> findBySource_id(Long accountId);
	List<Relation> findByTarget_id(Long accountId);
	*/
	List<Relation> findBySource_UsernameAndTarget_Username(String sourceUsername, String targetUsername);
}
