package it.governoedits;

import com.google.common.base.Preconditions;

public class IPRange implements Comparable<IPRange> {

    final private String name;
    final private String start;
    final private String end;
    final private int startint;
    final private int endint;

    public IPRange(String line) {
        String[] parts = line.split(":");
        String name = parts[0];
        String[] ranges = parts[1].split("-");
        String start = ranges[0];
        String end = ranges[1];
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(start);
        Preconditions.checkNotNull(end);
        Preconditions.checkArgument(sun.net.util.IPAddressUtil.isIPv4LiteralAddress(start));
        Preconditions.checkArgument(sun.net.util.IPAddressUtil.isIPv4LiteralAddress(start));
        this.name = name;
        this.start = start;
        this.end = end;
        this.startint = toInt(this.start);
        this.endint = toInt(this.end);
    }

    public IPRange(String name, String start, String end) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(start);
        Preconditions.checkNotNull(end);
        Preconditions.checkArgument(sun.net.util.IPAddressUtil.isIPv4LiteralAddress(start));
        Preconditions.checkArgument(sun.net.util.IPAddressUtil.isIPv4LiteralAddress(start));
        this.name = name;
        this.start = start;
        this.end = end;
        this.startint = toInt(this.start);
        this.endint = toInt(this.end);
    }

    private static int toInt(String ip) {
        String[] parts = ip.split("\\.");
        int result = 0;
        for (String part : parts) {
            result = (result << 8) | Integer.parseInt(part);
        }
        return result;
    }

    @Override
    public int compareTo(IPRange o) {
        if (o == null) {
            return -1;
        }
        return startint - o.startint;
    }

    @Override
    public String toString() {
        return String.format("%s [%s .. %s]", name, start, end);
    }

    public boolean withinRange(int ip) {
        return (startint <= ip) && (ip <= endint);
    }

    public boolean withinRange(String ip) {
        return withinRange(toInt(ip));
    }

    public String getName() {
        return name;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public int getStartint() {
        return startint;
    }

    public int getEndint() {
        return endint;
    }

}
