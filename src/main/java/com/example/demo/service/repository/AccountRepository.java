
package com.example.demo.service.repository;

import com.example.demo.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
	boolean existsByUsername(String username);
	
	Account findByUsername(String username);
}
