
package com.example.demo.datatransfer;

import com.example.demo.domain.Status;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class RelationDto {
	
	private Long id;
	private AccountDto source;
	private AccountDto target;
	private Status status;
}
