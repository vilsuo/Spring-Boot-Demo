
package com.example.demo.datatransfer;

import com.example.demo.annotation.Username;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
add role?
*/
@Getter @Setter
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
public class AccountDto {
	
	@NotNull
	private Long id;
	
	@Username
	private String username;
}
