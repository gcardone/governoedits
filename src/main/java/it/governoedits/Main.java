package it.governoedits;

import java.io.IOException;
import java.util.Scanner;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.managers.BackgroundListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting up");
        try {
            BackgroundListenerManager listenerManager = new BackgroundListenerManager();
            IRCListener listener = new IRCListener();
//            listenerManager.addListener(listener, true);
//
//            @SuppressWarnings("unchecked")
//            Configuration<PircBotX> config = new Configuration.Builder<PircBotX>()
//                    .setName("GovernoEdits").setAutoNickChange(true)
//                    .setServer("irc.wikimedia.org", 6667).setAutoReconnect(true)
//                    .addAutoJoinChannel("#it.wikipedia").setListenerManager(listenerManager)
//                    .buildConfiguration();
//
//            PircBotX bot = new PircBotX(config);
//            bot.startBot();
        } catch (IOException e) {
            logger.error("Critical error", e);
        }
    }
}
