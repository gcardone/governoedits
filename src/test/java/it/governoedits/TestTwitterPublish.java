package it.governoedits;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

import org.junit.Test;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.Authorization;

public class TestTwitterPublish {
  
  private final static String BITLY_PATTERN = "http://t.co/[a-zA-Z0-9]{10}";
  private final static String EXPECTED_PATTERN = 
	  String.format(TwitterPublish.MSG_FORMAT.replace("[", "\\[").replace("]", "\\]").replace("(", "\\(")
		  .replace(")","\\)"), "%s", BITLY_PATTERN , "%s",
		  BITLY_PATTERN);
  
  private Twitter tw;
  private BitlyShortener btly;
  private TwitterPublish underTest;
  
  
  @Test
  // It would have been better to test this on a 
  // test twitter account
  public void testHandlEditLive() throws Exception {
	setUpRealClients();
	underTest = new TwitterPublish(tw, btly);
	
	WikipediaEdit edit = createEdit();

	underTest.handleEdit("ME", edit);
	
	// verify that the stautus has been published
	
	ResponseList<Status> stati = tw.getHomeTimeline();
	Status latest = stati.get(0);
	String msg = latest.getText();
	
	// Delete the test tweet
	tw.destroyStatus(latest.getId());
	
	String expect = String.format(EXPECTED_PATTERN, "Test Page", "ME");
	assertTrue(msg.matches(expect));

  }


  @Test
  public void testHandleEditMocks() throws Exception {
	setUpMockClients();
	underTest = new TwitterPublish(tw,btly);

	WikipediaEdit edit = createEdit();
	
	underTest.handleEdit("ME", edit);
	
	String expect = String.format(TwitterPublish.MSG_FORMAT, 
		edit.getPage(), "<SHORTURL>", "ME", "<SHORTURL>");
	verify(tw).updateStatus(expect);
	
  }
  
  private WikipediaEdit createEdit() {
	WikipediaEdit.Builder bld = new WikipediaEdit.Builder();
	bld.setAnonymous(true);
	bld.setPage("Test Page");
	bld.setPageUrl("http://wiki.com/Test_Page");
	bld.setUrl("http://wiki.com/Test_Page/diff?old=0&new=1");
	return bld.build();
	
  }

  private void setUpMockClients() throws Exception {
	tw = mock(Twitter.class);
	Authorization twAuth = mock(Authorization.class);
	when(twAuth.isEnabled()).thenReturn(true);
	when(tw.getAuthorization()).thenReturn(twAuth);
	
	btly = mock(BitlyShortener.class);
	when(btly.shorten(anyString())).thenReturn("<SHORTURL>");
	
  }

  private void setUpRealClients() {
	tw = TwitterFactory.getSingleton();
	btly = new BitlyShortener();
	
  }
}
