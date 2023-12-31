
package com.example.demo.converter;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityToDtoConverter {
	
	@Autowired
	private ModelMapper modelMapper;
	
	public AccountDto convertAccount(final Account account) {
		if (account == null) {
			throw new NullPointerException(
				"Tried to convert null Account to AccountDto."
			);
		}
		
		return modelMapper.map(account, AccountDto.class);
	}
	
	public Optional<AccountDto> convertOptionalAccount(
			final Optional<Account> opt) {
		
		if (opt.isPresent()) {
			return Optional.ofNullable(
				convertAccount(opt.get())
			);
		} else {
			return Optional.empty();
		}
	}
	
	public RelationDto convertRelation(final Relation relation) {
		if (relation == null) {
			throw new NullPointerException(
				"Tried to convert null Relation to RelationDto."
			);
		}
		
		return modelMapper.map(relation, RelationDto.class);
	}
	
	public Optional<RelationDto> convertOptionalRelation(
			final Optional<Relation> opt) {
		
		if (opt.isPresent()) {
			return Optional.ofNullable(
				convertRelation(opt.get())
			);
		} else {
			return Optional.empty();
		}
	}
}
