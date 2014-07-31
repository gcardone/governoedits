package it.governoedits.util;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public final class IP4Utils {

	public static long toLong(String ipv4) {
		String[] parts = ipv4.split("\\.");
		return Stream.of(parts).map(Long::parseLong)
				.reduce(0L, (acc, p) -> acc << 8 | p);
	}

	public static String fromLong(final long ip) {
		return StringUtils.join(
				Stream.of(3, 2, 1, 0).map(
						(Integer part) -> (ip >>> part * 8) & 0xFF).toArray(), ".");

	}
}
