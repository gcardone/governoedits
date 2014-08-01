package it.governoedits;

import it.governoedits.util.IP4Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class IRCListener implements Listener<PircBotX> {

  private static final Logger LOGGER = LoggerFactory
	  .getLogger(IRCListener.class);

  // RegEx expression courtesy of
  // https://github.com/edsu/wikichanges/blob/master/wikichanges.js
  private static final Pattern PATTERN = Pattern
	  .compile("\\x0314\\[\\[\\x0307(.+?)\\x0314\\]\\]\\x034 (.*?)\\x0310.*\\x0302(.*?)\\x03.+\\x0303(.+?)\\x03.+\\03 (.*) \\x0310(.*)\\u0003.*");
  private static final Pattern PATTERN_INT = Pattern
	  .compile("\\(([+-]\\d+)\\)");
  private static final Pattern PATTERN_IPv4 = Pattern
	  .compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

  private static final String WIKIS_DEFAULT = "/wikis.json";
  private static final String RANGES_DEFAULT = "/ip_ranges.txt";

  private final JsonObject wikis;
  private final NavigableMap<Long, IPRange> ipranges;
  private final List<HandleWikiEdit> handlers;

  // if false does not use ranges from the ranges file to filter edits
  private final boolean useRanges;

  public IRCListener(List<HandleWikiEdit> handlers) throws IOException {
	this(IRCListener.WIKIS_DEFAULT, IRCListener.RANGES_DEFAULT, handlers, true);
  }

  public IRCListener(List<HandleWikiEdit> handlers, boolean useRanges)
	  throws IOException {
	this(IRCListener.WIKIS_DEFAULT, IRCListener.RANGES_DEFAULT, handlers,
	    useRanges);
  }

  public IRCListener(String wikisResource, String rangesResource,
	  List<HandleWikiEdit> handlers) throws IOException {
	this(wikisResource, rangesResource, handlers, true);
  }

  public IRCListener(String wikisResource, String rangesResource,
	  List<HandleWikiEdit> handlers, boolean useRanges) throws IOException {
	Preconditions.checkNotNull(wikisResource);
	Preconditions.checkNotNull(rangesResource);
	Preconditions.checkNotNull(handlers);

	this.handlers = ImmutableList.copyOf(handlers);
	this.useRanges = useRanges;

	try (InputStream is = IRCListener.class.getResourceAsStream(wikisResource);
	    JsonReader jr = new JsonReader(new InputStreamReader(is))) {
	  wikis = new JsonParser().parse(jr).getAsJsonObject();
	}
	ipranges = new TreeMap<Long, IPRange>();
	try (InputStream is = IRCListener.class.getResourceAsStream(rangesResource);
	    BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

	  List<IPRange> ranges = br.lines().filter(l -> !l.startsWith("#"))
		  .map(l -> new IPRange(l)).collect(Collectors.toList());

	  // ensure that there are no intersecting ranges
	  ranges = mergeRanges(ranges);

	  // Build the map
	  ranges.forEach(ipr -> ipranges.putIfAbsent(ipr.getStartLong(), ipr));

	  // log duplicates (actually this test should never print any warning
	  // now because ranges are merged)
	  ranges
		  .stream()
		  .collect(Collectors.groupingBy(IPRange::getStartLong))
		  .values()
		  .stream()
		  .filter(l -> l.size() > 1)
		  .forEach(
		      l -> LOGGER.warn("IP ranges {} start from the same address.", l));
	}
  }

  @Override
  public void onEvent(Event<PircBotX> event) throws Exception {
	if (event instanceof MessageEvent<?>) {
	  MessageEvent<?> messageEvent = (MessageEvent<?>) event;
	  if (messageEvent.getUser().getNick().equals("rc-pmtpa")) {
		try {
		  String channel = messageEvent.getChannel().getName();
		  String message = messageEvent.getMessage();
		  WikipediaEdit we = parseEdit(channel, message);
		  this.getRangeNameIfMatch(we).ifPresent(
			  rangeName -> handlers.forEach(h -> h.handleEdit(rangeName, we)));

		} catch (IllegalEditException e) {
		  LOGGER.warn("Illegal message format {}.", messageEvent.getMessage());
		}
	  }
	}
  }

  public WikipediaEdit parseEdit(String channel, String msg)
	  throws IllegalEditException {
	Matcher m = PATTERN.matcher(msg);
	if (m.matches()) {
	  WikipediaEdit.Builder weBuilder = new WikipediaEdit.Builder();

	  // set page
	  String page = m.group(1);
	  weBuilder.setPage(page);
	  String wikipedia = wikis.getAsJsonObject(channel).get("long")
		  .getAsString();
	  String wikipediaUrl = String
		  .format("http://%s.org", channel.substring(1));
	  if (channel == "#wikidata.wikipedia") {
		wikipediaUrl = "http://wikidata.org";
	  }
	  weBuilder.setWikipedia(wikipedia);
	  weBuilder.setWikipediaUrl(wikipediaUrl);
	  weBuilder.setPageUrl(String.format("%s/wiki/%s", wikipediaUrl,
		  page.replaceAll(" ", "_")));

	  // set flags: !NB
	  weBuilder.setFlag(m.group(2));
	  if (m.group(2).indexOf('!') != -1) {
		weBuilder.setUnpatrolled(true);
	  }
	  if (m.group(2).indexOf('B') != -1) {
		weBuilder.setRobot(true);
	  }
	  if (m.group(2).indexOf('N') != -1) {
		weBuilder.setNewPage(true);
	  }

	  // set URL
	  weBuilder.setUrl(m.group(3));

	  // parse user
	  String user = m.group(4);
	  weBuilder.setUser(user);
	  weBuilder.setAnonymous(false);
	  if (PATTERN_IPv4.matcher(user).matches()) {
		weBuilder.setAnonymous(true);
	  }
	  // IPv6 not supported at the moment, if you don't want to get
	  // published and still be anonymous, pleas use IPv6
	  // else if (sun.net.util.IPAddressUtil.isIPv6LiteralAddress(user)) {
	  // /*
	  // * Matching an IPv6 address using a regex is incredibly
	  // * complicated, so let's merrily use an internal, unsupported
	  // * function
	  // */
	  // weBuilder.setAnonymous(true);
	  // }
	  weBuilder
		  .setUserUrl(String.format("%s/wiki/User:%s", wikipediaUrl, user));

	  // parse number of changed lines
	  if (!Strings.isNullOrEmpty(m.group(5))) {
		Matcher matcherInt = PATTERN_INT.matcher(m.group(5));
		int delta = 0;
		if (matcherInt.matches()) {
		  delta = Integer.parseInt(matcherInt.group(1));
		}
		weBuilder.setDelta(delta);
	  }

	  // set comment
	  weBuilder.setComment(m.group(6));

	  LOGGER.debug("{}", weBuilder.build());
	  return weBuilder.build();
	} else {
	  throw new IllegalEditException();
	}
  }

  public Optional<String> getRangeNameIfMatch(WikipediaEdit we) {
	if (!we.isAnonymous()) {
	  return Optional.empty();
	}
	final String user = we.getUser();
	if (!useRanges) {
	  return Optional.of("ANY");
	} else {
	  return Optional
		  .ofNullable(ipranges.floorEntry(IP4Utils.toLong(user)).getValue())
		  .filter(r -> r.withinRange(user)).map(IPRange::getName);
	}
  }

  private List<IPRange> mergeRanges(List<IPRange> ranges) {
	ArrayList<IPRange> mergedList = new ArrayList<>(ranges.size());
	Iterator<IPRange> rIt = ranges.iterator();
	if (rIt.hasNext()) {
	  mergedList.add(rIt.next());
	}
	while (rIt.hasNext()) {
	  IPRange r = rIt.next();
	  boolean inserted = false;
	  for (int i = 0; i < mergedList.size() && !inserted; i++) {
		IPRange o = mergedList.get(i);
		if (o.intersects(r)) {
		  LOGGER.warn("Merging IP ranges {} and {}", o, r);
		  mergedList.set(i, IPRange.merge(o, r));
		  inserted = true;
		}
	  }
	  if (!inserted) {
		mergedList.add(r);

	  }
	}

	return mergedList;
  }

  protected List<IPRange> getRanges() {
	return ImmutableList.copyOf(ipranges.values());
  }

}
