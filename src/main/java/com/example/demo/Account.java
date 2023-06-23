
package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
public class Account extends AbstractPersistable<Long>{
	
	private String username;
	private String password;
	
	private Role role;
	/*
	@ManyToMany
	@Enumerated(EnumType.STRING)
	@JoinTable( 
        name = "accounts_roles", 
        joinColumns = @JoinColumn(
			name = "account_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
			name = "role_id", referencedColumnName = "id")
	) 
	private List<Role> roles;
	*/
}
