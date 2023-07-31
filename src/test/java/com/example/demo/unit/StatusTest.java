
package com.example.demo.unit;

import com.example.demo.domain.Status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class StatusTest {
	
	@Test
	public void hasTwoStatuOptionsTest() {
		assertEquals(Status.values().length, 2);
	}
	
	@Test
	public void getNameTest() {
		assertEquals(Status.FRIEND.getName(), "FRIEND");
		assertEquals(Status.BLOCKED.getName(), "BLOCKED");
	}
	
	@Test
	public void getStatusTest() {
		String friend = "FRIEND";
		String blocked = "BLOCKED";
		String invalidName = "NONEXISTENT";
		
		assertEquals(Status.getStatus(friend), Status.FRIEND);
		assertEquals(Status.getStatus(blocked), Status.BLOCKED);
		assertNull(Status.getStatus(invalidName));
		assertNull(Status.getStatus(null));
	}	
}
