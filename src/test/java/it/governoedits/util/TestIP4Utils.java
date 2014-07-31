package it.governoedits.util;

import static it.governoedits.util.IP4Utils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestIP4Utils {

	@Test
	public void testToInt() {
		String ipStr = "0.0.0.0";
		long expect = 0;
		assertEquals(expect, toLong(ipStr));
		
		ipStr = "127.0.0.1";
		expect = 2130706433;
		assertEquals(expect, toLong(ipStr));

		ipStr = "173.194.40.0";
		expect = 2915182592L;
		assertEquals(expect, toLong(ipStr));
		
	}

	@Test
	public void testFromInt() {
		long ip  = 2915182592L;
		String expect = "173.194.40.0";
		assertEquals(expect, fromLong(ip));
	}

}
