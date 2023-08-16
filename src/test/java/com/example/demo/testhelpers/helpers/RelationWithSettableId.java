
package com.example.demo.testhelpers;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;

public class RelationWithSettableId extends Relation {
		
	public RelationWithSettableId(
			Long id, Account source, Account target, Status status) {
		
		super(source, target, status);
		super.setId(id);
	}
}
