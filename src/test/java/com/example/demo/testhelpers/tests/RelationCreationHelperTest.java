
package com.example.demo.testhelpers.tests;

import com.example.demo.domain.Role;
import static com.example.demo.testhelpers.helpers.RelationCreationHelper.accountCreationPairWithAllRoleCombinationsStream;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class RelationCreationHelperTest {
	
	private final int TOTAL_ROLES = Role.values().length;
	
	@Test
	public void accountCreationPairWithAllRoleCombinationsStreamSizeTest() {
		assertEquals(
			TOTAL_ROLES * TOTAL_ROLES,
			accountCreationPairWithAllRoleCombinationsStream().count()
		);
	}
	
	@Test
	public void accountCreationPairWithAllRoleCombinationsStreamHasExactlyOneCombinationOfEachRoleTest() {
		final Map<Role, Map<Role, Integer>> rolePairCounts = new HashMap<>();
		accountCreationPairWithAllRoleCombinationsStream()
			.forEach(pairOfPairs -> {
				final Role roleFirst = pairOfPairs.getFirst().getSecond();
				final Role roleSecond = pairOfPairs.getSecond().getSecond();
				
				rolePairCounts.putIfAbsent(roleFirst, new HashMap<>());
				
				final Map<Role, Integer> inner = rolePairCounts.get(roleFirst);
				inner.put(roleSecond, inner.getOrDefault(roleSecond, 0) + 1);
			});
		
		for (final Role roleFirst : Role.values()) {
			for (final Role roleSecond : Role.values()) {
				final int rolePairCount = rolePairCounts
						.getOrDefault(roleFirst, new HashMap<>())
						.getOrDefault(roleSecond, 0);
				
				assertEquals(
					1, rolePairCount,
					"Expected Role combination " + roleFirst.getName() + ", "
					+ roleSecond.getName() + " to appear once in the Stream, "
					+ "but it appeared " + rolePairCount + " times"
				);
			}
		}
	}
}
