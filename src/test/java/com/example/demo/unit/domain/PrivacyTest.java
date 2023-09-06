
package com.example.demo.unit.domain;

import com.example.demo.domain.Privacy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class PrivacyTest {
	
	@Test
	public void hasThreePrivacyOptionsTest() {
		assertEquals(Privacy.values().length, 3);
	}
	
	@Test
	public void getNameTest() {
		assertEquals(Privacy.ALL.getName(), "ALL");
		assertEquals(Privacy.FRIENDS.getName(), "FRIENDS");
		assertEquals(Privacy.PRIVATE.getName(), "PRIVATE");
	}
	
	@Test
	public void getPrivacyTest() {
		assertEquals(Privacy.getPrivacy("ALL"), Privacy.ALL);
		assertEquals(Privacy.getPrivacy("FRIENDS"), Privacy.FRIENDS);
		assertEquals(Privacy.getPrivacy("PRIVATE"), Privacy.PRIVATE);
		
		final String invalidName = "NONEXISTENT";
		assertNull(Privacy.getPrivacy(invalidName));
		assertNull(Privacy.getPrivacy(null));
	}
}
