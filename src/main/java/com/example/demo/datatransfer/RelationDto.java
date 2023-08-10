
package com.example.demo.datatransfer;

import com.example.demo.domain.Status;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
public class RelationDto {
	
	private Long id;
	private AccountDto source;
	private AccountDto target;
	private Status status;
}
