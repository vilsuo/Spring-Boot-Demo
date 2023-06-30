
package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/*
experiment with add/remove follower -methods
*/
@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@EqualsAndHashCode(exclude = {"followers", "following"}, callSuper = false)
public class Account extends AbstractPersistable<Long> {
	
	private String username;
	
	private String password;
	
	private Role role;
	
	// source : 
	// https://stackoverflow.com/questions/57561294/why-am-i-getting-a-unique-index-or-primary-key-violation
	// https://stackoverflow.com/questions/1656113/hibernate-recursive-many-to-many-association-with-the-same-entity
	@ManyToMany
	@JoinTable(name = "followers",
		joinColumns = @JoinColumn(name = "account_from_id"),
		inverseJoinColumns = @JoinColumn(name = "account_to_id")
	)
	private Set<Account> following = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "followers",
		joinColumns = @JoinColumn(name = "account_to_id"),
		inverseJoinColumns = @JoinColumn(name = "account_from_id")
	)
	private Set<Account> followers = new HashSet<>();
	
	
	public void addFollower(Account account) {
		followers.add(account);
    }
	
	public void addFollowing(Account account) {
		following.add(account);
	}
	
    public void removeFollower(Account account) {
        followers.remove(account);
    }
	
	public void removeFollowing(Account account) {
		following.remove(account);
	}
	
}
