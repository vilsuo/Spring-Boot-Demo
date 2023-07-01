
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

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@EqualsAndHashCode(exclude = {"relationsTo", "relationsFrom"}, callSuper = false)
public class AccountWithRelation extends AbstractPersistable<Long> {
	
	private String username;
	
	private String password;
	
	private Role role;
	
	@OneToMany(mappedBy = "source")
	private Set<Relation> relationsTo = new HashSet<>();
	
	@OneToMany(mappedBy = "target")
	private Set<Relation> relationsFrom = new HashSet<>();
	
	
	public void addRelation(Relation relation) {
		getRelationsTo().add(relation);
	}
	
	public void removeRelation(Relation relation) {
		getRelationsTo().remove(relation);
	}
	
}
