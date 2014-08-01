package it.governoedits;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestIPRange {

  @Test
  public void testCreateIPRange() {
	final String startIp = "192.168.0.1";
	final String endIp = "192.168.10.2";
	final IPRange ip = new IPRange(String.format("TESTRANGE:%s-%s", startIp,
	    endIp));
	assertNotNull(ip);
	assertEquals(startIp, ip.getStart());
	assertEquals(endIp, ip.getEnd());
	assertEquals("TESTRANGE", ip.getName());
  }

  @Test
  public void testCheckAllDisjoint() {
	List<IPRange> disjointRanges = ImmutableList.of(new IPRange("A",
	    "192.168.0.1", "192.168.10.2"),
	    new IPRange("B", "10.0.0.1", "10.0.0.3"), new IPRange("C",
	        "73.42.123.1", "73.42.123.1"));

	assertTrue(IPRange.checkAllDisjoint(disjointRanges));

	List<IPRange> nonDisjointRanges = ImmutableList.of(new IPRange("A",
	    "192.168.0.1", "192.168.10.2"),
	    new IPRange("B", "10.0.0.1", "10.0.0.3"), new IPRange("C",
	        "73.42.123.1", "73.42.123.1"), new IPRange("D", "10.0.0.2",
	        "10.1.0.255"));

	assertFalse(IPRange.checkAllDisjoint(nonDisjointRanges));

  }

  @Test
  public void testIsDisjoint() {
	IPRange[] r = buildSomeRanges();
	assertFalse(r[0].isDisjoint(r[1]));
	assertFalse(r[0].isDisjoint(r[2]));
	assertFalse(r[0].isDisjoint(r[3]));
	assertFalse(r[0].isDisjoint(r[4]));
	assertFalse(r[0].isDisjoint(r[5]));
	assertTrue(r[0].isDisjoint(r[6]));
	assertTrue(r[0].isDisjoint(r[7]));
  }

  @Test
  public void testMerge() {
	IPRange[] r = buildSomeRanges();

	assertEquals(r[0], IPRange.merge(r[0], r[1]));
	assertEquals(r[0], IPRange.merge(r[0], r[2]));
	assertEquals(r[0], IPRange.merge(r[0], r[3]));

	assertEquals(new IPRange("A", "192.168.0.1", "192.168.10.2"),
	    IPRange.merge(r[0], r[4]));

	assertEquals(new IPRange("A", "192.168.1.1", "192.168.11.255"),
	    IPRange.merge(r[0], r[5]));

	boolean success = false;
	try {
	  IPRange.merge(r[0], r[6]);
	  success = true;

	} catch (IllegalArgumentException e) {
	}
	try {
	  IPRange.merge(r[0], r[7]);
	  success = true;
	} catch (IllegalArgumentException e) {
	}
	
	assertFalse(success);
  }

  @Test
  public void testWithinRange() {
	IPRange ip0 = new IPRange("A", "192.168.1.1", "192.168.10.2");
	String ip = "192.168.1.1";
	assertTrue(ip0.withinRange(ip));
	
	ip = "192.168.10.2";
	assertTrue(ip0.withinRange(ip));
	
	ip = "192.168.5.2";
	assertTrue(ip0.withinRange(ip));
	
	ip = "192.168.22.2";
	assertFalse(ip0.withinRange(ip));
	
	ip = "192.165.1.1";
	assertFalse(ip0.withinRange(ip));
  }

  private IPRange[] buildSomeRanges() {
	IPRange ip0 = new IPRange("A", "192.168.1.1", "192.168.10.2");
	IPRange ip1 = new IPRange("leftBound", "192.168.1.1", "192.168.1.1");
	IPRange ip2 = new IPRange("rightBound", "192.168.10.2", "192.168.10.2");
	IPRange ip3 = new IPRange("inside", "192.168.2.1", "192.168.3.255");
	IPRange ip4 = new IPRange("acrossLeft", "192.168.0.1", "192.168.3.255");
	IPRange ip5 = new IPRange("acrossRight", "192.168.9.1", "192.168.11.255");
	IPRange ip6 = new IPRange("disjointLeft", "192.168.0.1", "192.168.0.24");
	IPRange ip7 = new IPRange("disjointRight", "192.168.15.1", "192.168.15.33");

	return new IPRange[] { ip0, ip1, ip2, ip3, ip4, ip5, ip6, ip7 };

  }

}
