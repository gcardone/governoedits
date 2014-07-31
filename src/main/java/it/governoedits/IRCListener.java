package it.governoedits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class IRCListener implements Listener<PircBotX> {

    private static final Logger logger = LoggerFactory.getLogger(IRCListener.class);

    // RegEx expression courtesy of
    // https://github.com/edsu/wikichanges/blob/master/wikichanges.js
    private static final Pattern pattern = Pattern
            .compile("\\x0314\\[\\[\\x0307(.+?)\\x0314\\]\\]\\x034 (.*?)\\x0310.*\\x0302(.*?)\\x03.+\\x0303(.+?)\\x03.+\\03 (.*) \\x0310(.*)\\u0003.*");
    private static final Pattern patternInt = Pattern.compile("\\(([+-]\\d+)\\)");
    private static final Pattern patternIPv4 = Pattern
            .compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    private JsonObject wikis;
    private NavigableMap<Integer, IPRange> ipranges;

    public IRCListener() throws IOException {
        try (InputStream is = IRCListener.class.getResourceAsStream("/wikis.json");
                JsonReader jr = new JsonReader(new InputStreamReader(is))) {
            wikis = new JsonParser().parse(jr).getAsJsonObject();
        } catch (IOException e) {
            logger.error("Error while initializing the IRC message parser", e);
            throw e;
        }
        try (InputStream is = IRCListener.class.getResourceAsStream("/wikis.json");
                JsonReader jr = new JsonReader(new InputStreamReader(is))) {
            wikis = new JsonParser().parse(jr).getAsJsonObject();
        } catch (IOException e) {
            logger.error("Error while initializing the IRC message parser", e);
            throw e;
        }

        ipranges = new TreeMap<Integer, IPRange>();
        try (InputStream is = IRCListener.class.getResourceAsStream("/ip_ranges.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                IPRange ipRange = new IPRange(line);
                if (ipranges.containsKey(ipRange.getStartint())) {
                    logger.warn("Duplicated IP range {} (was: {}), ignored", ipRange, ipranges.get(ipRange.getStartint()));
                } else {
                    ipranges.put(ipRange.getStartint(), ipRange);
                }
            }
        } catch (IOException e) {
            logger.error("Error while parsing IP ranges", e);
            throw e;
        }
    }

    public WikipediaEdit parseEdit(String channel, String msg) throws IllegalEditException {
        Matcher m = pattern.matcher(msg);
        if (m.matches()) {
            WikipediaEdit.Builder weBuilder = new WikipediaEdit.Builder();

            // set page
            String page = m.group(1);
            weBuilder.setPage(page);
            String wikipedia = wikis.getAsJsonObject(channel).get("long").getAsString();
            String wikipediaUrl = String.format("http://%s.org", channel.substring(1));
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
            if (patternIPv4.matcher(user).matches()) {
                weBuilder.setAnonymous(true);
            } else if (sun.net.util.IPAddressUtil.isIPv6LiteralAddress(user)) {
                /*
                 * Matching an IPv6 address using a regex is incredibly
                 * complicated, so let's merrily use an internal, unsupported
                 * function
                 */
                weBuilder.setAnonymous(true);
            }
            weBuilder.setUserUrl(String.format("%s/wiki/User:%s", wikipediaUrl, user));

            // parse number of changed lines
            if (!Strings.isNullOrEmpty(m.group(5))) {
                Matcher matcherInt = patternInt.matcher(m.group(5));
                int delta = 0;
                if (matcherInt.matches()) {
                    delta = Integer.parseInt(matcherInt.group(1));
                }
                weBuilder.setDelta(delta);
            }

            // set comment
            weBuilder.setComment(m.group(6));

            logger.info("{}", weBuilder.build());
            return weBuilder.build();
        } else {
            throw new IllegalEditException();
        }
    }

    public boolean toPublish(WikipediaEdit we) {
        if (!we.isAnonymous()) {
            return false;
        } else {
            return true;
        }
    }

    public Optional<IPRange> getIpRange(String ip) {
        return null;
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
                } catch (IllegalEditException e) {
                    logger.warn("Illegal message format {}.", messageEvent.getMessage());
                }
            }
        }
    }

}
