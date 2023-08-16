
package com.example.demo.testhelpers.helpers;

import com.example.demo.domain.Account;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Status;

public class RelationWithSettableId extends Relation {
		
	public RelationWithSettableId(final Long id, final Account source,
			final Account target, final Status status) {
		
		super(source, target, status);
		super.setId(id);
	}
}
