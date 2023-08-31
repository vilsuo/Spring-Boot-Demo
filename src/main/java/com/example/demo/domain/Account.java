
package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@EqualsAndHashCode(
	exclude = {"relationsTo", "relationsFrom", "images"}, 
	callSuper = false
)
@ToString(
	exclude = {"relationsTo", "relationsFrom", "images"}
)
public class Account extends AbstractPersistable<Long> {
	
	private String username;
	private String password;
	private Role role;
	
	@OneToMany(mappedBy = "source")
	private Set<Relation> relationsTo = new HashSet<>();
	
	@OneToMany(mappedBy = "target")
	private Set<Relation> relationsFrom = new HashSet<>();
	
	@OneToMany(mappedBy = "account")
	private Set<FileObject> images = new HashSet<>();

	public Account(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}
}
