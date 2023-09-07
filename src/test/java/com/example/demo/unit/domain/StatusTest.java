
package com.example.demo.unit.domain;

import com.example.demo.domain.Status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class StatusTest {
	
	@Test
	public void hasTwoStatusOptionsTest() {
		assertEquals(2, Status.values().length);
	}
	
	@Test
	public void getValueTest() {
		assertEquals("Friend", Status.FRIEND.getValue());
		assertEquals("Blocked", Status.BLOCKED.getValue());
	}
}
