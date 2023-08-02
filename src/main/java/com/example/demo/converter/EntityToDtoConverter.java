
package com.example.demo.converter;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import java.util.Optional;

public final class EntityToDtoConverter {
	
	public static AccountDto convertAccount(final Account account) {
		if (account == null) {
			throw new NullPointerException(
				"Tried to convert null Account to AccountDto."
			);
		}
		
		return new AccountDto(account.getId(), account.getUsername());
	}
	
	public static Optional<AccountDto> convertOptionalAccount(
			final Optional<Account> opt) {
		
		if (opt.isPresent()) {
			return Optional.ofNullable(
				EntityToDtoConverter.convertAccount(opt.get())
			);
		} else {
			return Optional.empty();
		}
	}
	
	public static RelationDto convertRelation(final Relation relation) {
		if (relation == null) {
			throw new NullPointerException(
				"Tried to convert null Relation to RelationDto."
			);
		}
		
		return new RelationDto(
			relation.getId(),
			convertAccount(relation.getSource()),
			convertAccount(relation.getTarget()),
			relation.getStatus()
		);
	}
	
	public static Optional<RelationDto> convertOptionalRelation(
			final Optional<Relation> opt) {
		
		if (opt.isPresent()) {
			return Optional.ofNullable(
				EntityToDtoConverter.convertRelation(opt.get())
			);
		} else {
			return Optional.empty();
		}
	}
}
