
package com.example.demo.service.repository;

import com.example.demo.domain.AccountWithRelation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountWithRelationRepository extends JpaRepository<AccountWithRelation, Long> {
	boolean existsByUsername(String username);
	
	Optional<Long> findIdByUsername(String username);
	Optional<AccountWithRelation> findByUsername(String username);
}
