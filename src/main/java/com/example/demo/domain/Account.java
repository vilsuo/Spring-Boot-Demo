
package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
public class Account extends AbstractPersistable<Long>{
	
	/*
	a String field constrained with @NotBlank must be not null, and the trimmed 
	length must be greater than zero.
	
	IF YOU CHANGE VALIDATION OPTIONS HERE, CHANGE ALSO IN THE CLASS AccountDto!
	*/
	@NotBlank
	@Size(min = 1, max = 20)
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
}
