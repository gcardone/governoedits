package it.governoedits;

import static it.governoedits.util.IP4Utils.*;

import java.util.List;

import com.google.common.base.Preconditions;

public final class IPRange implements Comparable<IPRange> {

  public static boolean checkAllDisjoint(List<IPRange> ranges) {
	IPRange[] ar = new IPRange[ranges.size()];
	ar = ranges.toArray(ar);
	boolean allDisjoint = true;
	for (int i = 0; i < ar.length && allDisjoint; i++) {
	  for (int j = i + 1; j < ar.length && allDisjoint; j++) {
		allDisjoint = allDisjoint && ar[i].isDisjoint(ar[j]);
	  }
	}
	return allDisjoint;
  }
  /**
   * Merge two intersecting IP ranges. Throw an IllegalArgumentException if they
   * don't intersect.
   * 
   * @param a
   * @param b
   * @return
   */
  public static IPRange merge(IPRange a, IPRange b) {
	Preconditions.checkNotNull(a);
	Preconditions.checkNotNull(b);
	Preconditions.checkArgument(a.intersects(b));
	long minStart = a.start <= b.start ? a.start : b.start;
	long maxEnd = a.end >= b.end ? a.end : b.end;
	return new IPRange(a.name, minStart, maxEnd);

  }
  private final String name;

  private final long start;

  private final long end;

  /**
   * line has format name:a.b.c.d:e.f.g.h
   * 
   * @param line
   */
  public IPRange(String line) {
	Preconditions.checkNotNull(line);
	String[] parts = line.split(":");
	String name = parts[0];
	String[] ranges = parts[1].split("-");
	String start = ranges[0];
	String end = ranges[1];
	Preconditions.checkNotNull(name);
	Preconditions.checkNotNull(start);
	Preconditions.checkNotNull(end);
	Preconditions.checkArgument(sun.net.util.IPAddressUtil
	    .isIPv4LiteralAddress(start));
	Preconditions.checkArgument(sun.net.util.IPAddressUtil
	    .isIPv4LiteralAddress(start));
	this.name = name;
	this.start = toLong(start);
	this.end = toLong(end);
	Preconditions.checkArgument(this.start <= this.end,
	    String.format("%s range ends before its start", line));
  }

  public IPRange(String name, String start, String end) {
	Preconditions.checkNotNull(name);
	Preconditions.checkNotNull(start);
	Preconditions.checkNotNull(end);
	Preconditions.checkArgument(sun.net.util.IPAddressUtil
	    .isIPv4LiteralAddress(start));
	Preconditions.checkArgument(sun.net.util.IPAddressUtil
	    .isIPv4LiteralAddress(start));
	this.name = name;
	this.start = toLong(start);
	this.end = toLong(end);
  }

  private IPRange(String name, long start, long end) {
	this.name = name;
	this.start = start;
	this.end = end;

  }

  @Override
  public int compareTo(IPRange o) {
	if (o == null) {
	  return -1;
	}
	long diff = start - o.start;
	if (diff == 0) {
	  return 0;
	} else if (diff > 0) {
	  return 1;
	} else {
	  return -1;
	}
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj)
	  return true;
	if (obj == null)
	  return false;
	if (getClass() != obj.getClass())
	  return false;
	IPRange other = (IPRange) obj;
	if (end != other.end)
	  return false;
	if (name == null) {
	  if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	  return false;
	if (start != other.start)
	  return false;
	return true;
  }

  public String getEnd() {
	return fromLong(end);
  }

  public long getEndLong() {
	return end;
  }

  public String getName() {
	return name;
  }

  public String getStart() {
	return fromLong(start);
  }

  public long getStartLong() {
	return start;
  }

  @Override
  public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (end ^ (end >>> 32));
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + (int) (start ^ (start >>> 32));
	return result;
  }

  public boolean intersects(IPRange other) {
	return !isDisjoint(other);
  }

  public boolean isDisjoint(IPRange other) {
	Preconditions.checkNotNull(other);
	return (this.end < other.start) || (this.start > other.end);
  }

  @Override
  public String toString() {
	return String.format("%s [%s .. %s]", name, fromLong(start), fromLong(end));
  }

  public boolean withinRange(String ip) {
	Preconditions.checkNotNull(ip);
	return withinRange(toLong(ip));
  }

  private boolean withinRange(long ip) {
	return (start <= ip) && (ip <= end);
  }

}
