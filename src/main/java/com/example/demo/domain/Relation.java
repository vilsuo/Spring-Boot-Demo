
package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class Relation {
	
	@NotNull
	private Account account;
	
	private String status;
}
