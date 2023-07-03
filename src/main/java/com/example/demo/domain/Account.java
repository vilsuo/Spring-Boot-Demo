
package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/*
no getters/setters?
*/
@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@EqualsAndHashCode(exclude = {"relationsTo", "relationsFrom"}, callSuper = false)
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
	
}
