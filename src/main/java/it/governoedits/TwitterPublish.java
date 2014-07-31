package it.governoedits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterPublish implements HandleWikiEdit {

    private static final Logger logger = LoggerFactory.getLogger(TwitterPublish.class);

    protected final Twitter twitter;
    
    public TwitterPublish() {
        twitter = TwitterFactory.getSingleton();
        if (!twitter.getAuthorization().isEnabled()) {
            logger.error("No Twitter authorization tokens available, please configure governoedits");
            throw new IllegalStateException();
        }
    }

    @Override
    public void handleEdit(WikipediaEdit wikiEdit) {
        // TODO Auto-generated method stub

    }

}
