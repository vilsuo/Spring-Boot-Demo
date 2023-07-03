
package com.example.demo.service.repository;

import com.example.demo.domain.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
	boolean existsByUsername(String username);
	
	Optional<Long> findIdByUsername(String username);
	Optional<Account> findByUsername(String username);
}
