
package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

//@Entity
@AllArgsConstructor @NoArgsConstructor @Data
public class AccountRelation extends AbstractPersistable<Long> {
	/*
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_account_fk")
	*/
	private Account sourceAccount;
	
	/*
	//@MapsId
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_account_fk")
	*/
	private Account targetAccount;
}
