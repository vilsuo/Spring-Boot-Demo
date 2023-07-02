
package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/*
no getters/setters?
*/
@Entity
@AllArgsConstructor @NoArgsConstructor @Data
public class Relation extends AbstractPersistable<Long> {
	
	@ManyToOne
	@JoinColumn(name = "account_source_fk")
	private AccountWithRelation source;
	
	@ManyToOne
	@JoinColumn(name = "account_target_fk")
	private AccountWithRelation target;
	
	private Status status;
}
