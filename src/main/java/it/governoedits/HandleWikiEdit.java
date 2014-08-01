package it.governoedits;

public interface HandleWikiEdit {

  static final String MSG_FORMAT = "New edit on article %s (%s) "
	  + "from %s [diff: %s]";

  void handleEdit(String fromRange, WikipediaEdit wikiEdit);
  
  default public String buildMessage(String range, String page, 
	  String url, String diffUrl) {
	return String.format(TwitterPublish.MSG_FORMAT, page,
	    url, range, diffUrl);
  }
}
