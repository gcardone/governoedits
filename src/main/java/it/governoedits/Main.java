package it.governoedits;

import it.governoedits.handlers.SimpleLoggerHandler;
import it.governoedits.handlers.TwitterPublish;

import java.io.IOException;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.managers.BackgroundListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  // Use ranges data to filter edits (set it to false only for testing)
  private static final boolean USING_RANGES = true;

  public static void main(String[] args) {
	LOGGER.info("Starting up");
	try {
	  LOGGER.info("Starting Twitter service");

	  SimpleLoggerHandler lg = new SimpleLoggerHandler();
	  TwitterPublish tp = new TwitterPublish();

	  BackgroundListenerManager listenerManager = new BackgroundListenerManager();
	  IRCListener listener = new IRCListener(ImmutableList.of(lg, tp),
		  Main.USING_RANGES);
	  listenerManager.addListener(listener, true);

	  @SuppressWarnings("unchecked")
	  Configuration<PircBotX> config = new Configuration.Builder<PircBotX>()
		  .setName("GovernoEdits").setAutoNickChange(true)
		  .setServer("irc.wikimedia.org", 6667).setAutoReconnect(true)
		  .addAutoJoinChannel("#it.wikipedia")
		  .setListenerManager(listenerManager).buildConfiguration();

	  PircBotX bot = new PircBotX(config);
	  bot.startBot();
	} catch (IrcException | IOException e) {
	  LOGGER.error("Critical error", e);
	}
  }
}
