
package com.example.demo.service.repository;

import com.example.demo.domain.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
	boolean existsByUsername(String username);
	
	Optional<Account> findByUsername(String username);
	
	// not possible without followers POJO???
	//@Query("SELECT f.account_from_id FROM followers f WHERE f.account_from_id = ?1 AND f.account_to_id = ?2")
	//List<Long> isFollowing(Long followerId, Long followedId);
	
}
