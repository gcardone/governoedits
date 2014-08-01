package it.governoedits;

public interface HandleWikiEdit {

  static final String MSG_FORMAT = "New edit on article %s (%s) "
	  + "from %s [diff: %s]";

  void handleEdit(String fromRange, WikipediaEdit wikiEdit);

  default String buildMessage(String range, WikipediaEdit e) {
	return String.format(TwitterPublish.MSG_FORMAT, e.getPage(),
	    e.getPageUrl(), range, e.getUrl());
  }
}
