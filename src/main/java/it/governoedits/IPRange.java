package it.governoedits;

import static it.governoedits.util.IP4Utils.*;

import com.google.common.base.Preconditions;

public final class IPRange implements Comparable<IPRange> {

	final private String name;
	final private long startint;
	final private long endint;

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
		this.startint = toLong(start);
		this.endint = toLong(end);
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
		this.startint = toLong(start);
		this.endint = toLong(end);
	}
	


	@Override
	public int compareTo(IPRange o) {
		if (o == null) {
			return -1;
		}
		long diff = startint - o.startint;
		if (diff == 0) {
			return 0;
		} else if (diff > 0){
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return String.format("%s [%s .. %s]", name, fromLong(startint), fromLong(endint));
	}

	private boolean withinRange(long ip) {
		return (startint <= ip) && (ip <= endint);
	}

	public boolean withinRange(String ip) {
		return withinRange(toLong(ip));
	}

	public String getName() {
		return name;
	}

	public String getStart() {
		return fromLong(startint);
	}

	public String getEnd() {
		return fromLong(endint);
	}

	public long getStartLong() {
		return startint;
	}

	public long getEndLong() {
		return endint;
	}

}
