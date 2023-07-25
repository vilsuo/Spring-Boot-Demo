
package com.example.demo.datatransfer;

import com.example.demo.annotation.Username;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/*
add Role here?
*/
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class AccountDto {
	
	@NotNull
	private final Long id;
	
	@Username
	private final String username;
}
