package it.governoedits;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestIRCListener {

  @Test
  public void testGetRangeIfMatch() throws Exception {
	IRCListener lst = new IRCListener("/wikis.json", "/test-ip_ranges.txt",
		ImmutableList.of());
	String ipInRange = "151.8.231.2";
	String ipOutOfRange = "151.8.232.2";
	WikipediaEdit we = mock(WikipediaEdit.class);
	when(we.isAnonymous()).thenReturn(true);
	when(we.getUser()).thenReturn(ipInRange);
	assertEquals(Optional.of("RANGE 1"), lst.getRangeNameIfMatch(we));
	when(we.getUser()).thenReturn(ipOutOfRange);
	assertEquals(Optional.empty(), lst.getRangeNameIfMatch(we));
	
  }
  @Test
  public void testIntersectingRanges() throws Exception {
	IRCListener lst = new IRCListener("/wikis.json", "/test-ip_ranges.txt",
		ImmutableList.of());
	List<IPRange> ranges = lst.getRanges();
	IPRange r1 = new IPRange("RANGE 1", "151.8.230.0", "151.8.231.255");
	IPRange r2 = new IPRange("RANGE 3", "10.0.0.1", "10.0.0.255");
	assertEquals(ImmutableList.of(r2, r1), ranges);
  }

  @Test
  public void test() throws Exception {
	// String msg =
	// "\\x0314[[07Discussioni utente:Trixma14]]4 B10 02http://it.wikipedia.org/w/index.php?diff=67299651&oldid=67299649&rcid=80675518 5* 03Bottuzzu 5* (+112) 10Benvenuto/a!)";
	String msg = "\u0003\u0031\u0034\u005b\u005b\u0003\u0030\u0037\u0053\u0074\u006f\u0072\u0069\u0061\u0020\u0064\u0069\u0020\u004e\u0061\u0070\u006f\u006c\u0069\u0003\u0031\u0034\u005d\u005d\u0003\u0034\u0020\u0021\u0003\u0031\u0030\u0020\u0003\u0030\u0032\u0068\u0074\u0074\u0070\u003a\u002f\u002f\u0069\u0074\u002e\u0077\u0069\u006b\u0069\u0070\u0065\u0064\u0069\u0061\u002e\u006f\u0072\u0067\u002f\u0077\u002f\u0069\u006e\u0064\u0065\u0078\u002e\u0070\u0068\u0070\u003f\u0064\u0069\u0066\u0066\u003d\u0036\u0037\u0033\u0030\u0030\u0031\u0034\u0037\u0026\u006f\u006c\u0064\u0069\u0064\u003d\u0036\u0037\u0032\u0039\u0030\u0036\u0038\u0036\u0026\u0072\u0063\u0069\u0064\u003d\u0038\u0030\u0036\u0037\u0036\u0032\u0035\u0032\u0003\u0020\u0003\u0035\u002a\u0003\u0020\u0003\u0030\u0033\u0037\u0039\u002e\u0035\u0031\u002e\u0031\u0039\u0039\u002e\u0031\u0039\u0036\u0003\u0020\u0003\u0035\u002a\u0003\u0020\u0028\u002b\u0034\u0034\u0029\u0020\u0003\u0031\u0030\u002f\u002a\u0020\u004c\u0061\u0020\u0066\u006f\u006e\u0064\u0061\u007a\u0069\u006f\u006e\u0065\u0020\u0064\u0069\u0020\u0050\u0061\u0072\u0074\u0065\u006e\u006f\u0070\u0065\u0020\u002a\u002f\u0003";
	IRCListener ircListener = new IRCListener(ImmutableList.of());
	WikipediaEdit result = ircListener.parseEdit("#it.wikipedia", msg);
	assertNotNull(result);
//	assertFalse(result.isAnonymous());
	assertTrue(result.isAnonymous());
  }
}
