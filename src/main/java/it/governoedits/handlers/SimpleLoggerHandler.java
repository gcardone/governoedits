package it.governoedits.handlers;

import it.governoedits.HandleWikiEdit;
import it.governoedits.WikipediaEdit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimpleLoggerHandler implements HandleWikiEdit {
  
  private final static Logger LOGGER = LoggerFactory.getLogger(SimpleLoggerHandler.class);

  @Override
  public void handleEdit(String fromRange, WikipediaEdit wikiEdit) {
	String msg = buildMessage(fromRange, wikiEdit.getPage(), wikiEdit.getPageUrl(),
		wikiEdit.getUrl());
	LOGGER.info(msg);
  }

}
