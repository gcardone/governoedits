package it.governoedits;

import it.governoedits.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterPublish implements HandleWikiEdit {

  private static final Logger LOGGER = LoggerFactory
	  .getLogger(TwitterPublish.class);
  private static final String CONSUMER_TOKEN = "twitter.consumer.token";
  private static final String CONSUMER_TOKEN_SECRET = "twitter.consumer.token.secret";
  private static final String USER_TOKEN = "twitter.user.token";
  private static final String USER_TOKEN_SECRET = "twitter.user.token.secret";
  private static final String TOKEN_RESOURCE = "/tokens.properties";

  private final Twitter twitter;
  private final BitlyShortener bitly;

  public TwitterPublish() {
	this(TwitterFactory.getSingleton(), new BitlyShortener());
  }

  protected TwitterPublish(Twitter twitter, BitlyShortener bitly) {
	Preconditions.checkNotNull(twitter);
	Preconditions.checkNotNull(bitly);
	this.twitter = twitter;
	this.bitly = bitly;
	authenticate();
	if (!this.twitter.getAuthorization().isEnabled()) {
	  LOGGER
		  .error("No Twitter authorization tokens available, please configure governoedits");
	  throw new IllegalStateException();
	}
  }

  @Override
  public void handleEdit(String fromRange, WikipediaEdit wikiEdit) {
	Preconditions.checkNotNull(wikiEdit);
	Preconditions.checkNotNull(fromRange);

	if (wikiEdit.getPage().isEmpty() || wikiEdit.getPageUrl().isEmpty()) {
	  LOGGER.warn("Skipping publication: "
		  + "cannot publish edit with empty page or URL: {}", wikiEdit);
	  return;
	}
	if (fromRange.isEmpty()) {
	  LOGGER.warn("Skipping publication: "
		  + "Cannot publish edit with empty author: {}", fromRange);
	  return;
	}

	try {
	  String shortPageUrl = bitly.shorten(wikiEdit.getPageUrl());
	  String diffPageUrl = bitly.shorten(wikiEdit.getUrl());
	  String tweet = buildMessage(fromRange, wikiEdit.getPage(), shortPageUrl,
		  diffPageUrl);
	  twitter.updateStatus(tweet);
	} catch (TwitterException ex) {
	  String errorMsg = String.format(
		  "Skipping edit %s: twitter error while publishig.", wikiEdit);
	  LOGGER.error(errorMsg, ex);
	} catch (BitlyException ex) {
	  String errorMsg = String.format(
		  "Skipping edit %s: bitly error while publishig.", wikiEdit);
	  LOGGER.error(errorMsg, ex);
	  
	}
  }

  private void authenticate() {
	String[] consumerTokens = TwitterPublish.readConsumerTokens();
	AccessToken userTokens = TwitterPublish.readUserTokens();
	twitter.setOAuthConsumer(consumerTokens[0], consumerTokens[1]);
	twitter.setOAuthAccessToken(userTokens);

  }

  private static AccessToken readUserTokens() {
	String token = Properties.getProperty(TwitterPublish.USER_TOKEN,
	    TwitterPublish.TOKEN_RESOURCE);
	String security = Properties.getProperty(TwitterPublish.USER_TOKEN_SECRET,
	    TwitterPublish.TOKEN_RESOURCE);
	return new AccessToken(token, security);
  }

  private static String[] readConsumerTokens() {
	String token = Properties.getProperty(TwitterPublish.CONSUMER_TOKEN,
	    TwitterPublish.TOKEN_RESOURCE);
	String security = Properties.getProperty(
	    TwitterPublish.CONSUMER_TOKEN_SECRET, TwitterPublish.TOKEN_RESOURCE);
	return new String[] { token, security };

  }

}
