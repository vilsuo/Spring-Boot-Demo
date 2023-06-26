
package com.example.demo.domain;

import com.example.demo.annotation.Username;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@EqualsAndHashCode(exclude = {"followers", "following"}, callSuper = false)//(onlyExplicitlyIncluded = true, callSuper = false)
public class Account extends AbstractPersistable<Long> {
	
	// IF YOU CHANGE VALIDATION OPTIONS HERE, CHANGE ALSO IN THE CLASS AccountDto!
	@Username
	private String username;
	
	/*
	The @NotEmpty annotation makes use of the @NotNull class' isValid() 
	implementation, and also checks that the size/length of the supplied object 
	is greater than zero.
	
	Length of the actual password is defined in the class AccountDto.
	*/
	@NotEmpty
	private String password;
	
	@NotNull()
	private Role role;
	
	
	// ACCEPTED ANSWER:
	// source : 
	// https://stackoverflow.com/questions/57561294/why-am-i-getting-a-unique-index-or-primary-key-violation
	// https://stackoverflow.com/questions/1656113/hibernate-recursive-many-to-many-association-with-the-same-entity
	@ManyToMany
	@JoinTable(name = "followers",
		joinColumns = @JoinColumn(name = "account_from_id"),
		inverseJoinColumns = @JoinColumn(name = "account_to_id")
	)
	private Set<Account> following;

	@ManyToMany
	@JoinTable(name = "followers",
		joinColumns = @JoinColumn(name = "account_to_id"),
		inverseJoinColumns = @JoinColumn(name = "account_from_id")
	)
	private Set<Account> followers;	
	
	
	public void addFollower(Account toFollow) {
		System.out.println("enter addFollower");
		
        following.add(toFollow);
        //toFollow.getFollowers().add(this);
		
		System.out.println("exit addFollower");
    }
	
    public void removeFollower(Account toFollow) {
		System.out.println("enter removeFollower");
		
        following.remove(toFollow);
        //toFollow.getFollowers().remove(this);
		
		System.out.println("exit removeFollower");
    }
	
}
