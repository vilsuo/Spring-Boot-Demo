
package com.example.demo.datatransfer;

import com.example.demo.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data
public class RelationDto {
	
	private Long id;
	private AccountDto source;
	private AccountDto target;
	private Status status;
}
