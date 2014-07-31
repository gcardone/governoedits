package it.gcardone.governoedits;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class TestIRCListener {

    @Test
    public void test() throws IllegalEditException, IOException {
        // String msg =
        // "\\x0314[[07Discussioni utente:Trixma14]]4 B10 02http://it.wikipedia.org/w/index.php?diff=67299651&oldid=67299649&rcid=80675518 5* 03Bottuzzu 5* (+112) 10Benvenuto/a!)";
        String msg = "\u0003\u0031\u0034\u005b\u005b\u0003\u0030\u0037\u0053\u0074\u006f\u0072\u0069\u0061\u0020\u0064\u0069\u0020\u004e\u0061\u0070\u006f\u006c\u0069\u0003\u0031\u0034\u005d\u005d\u0003\u0034\u0020\u0021\u0003\u0031\u0030\u0020\u0003\u0030\u0032\u0068\u0074\u0074\u0070\u003a\u002f\u002f\u0069\u0074\u002e\u0077\u0069\u006b\u0069\u0070\u0065\u0064\u0069\u0061\u002e\u006f\u0072\u0067\u002f\u0077\u002f\u0069\u006e\u0064\u0065\u0078\u002e\u0070\u0068\u0070\u003f\u0064\u0069\u0066\u0066\u003d\u0036\u0037\u0033\u0030\u0030\u0031\u0034\u0037\u0026\u006f\u006c\u0064\u0069\u0064\u003d\u0036\u0037\u0032\u0039\u0030\u0036\u0038\u0036\u0026\u0072\u0063\u0069\u0064\u003d\u0038\u0030\u0036\u0037\u0036\u0032\u0035\u0032\u0003\u0020\u0003\u0035\u002a\u0003\u0020\u0003\u0030\u0033\u0037\u0039\u002e\u0035\u0031\u002e\u0031\u0039\u0039\u002e\u0031\u0039\u0036\u0003\u0020\u0003\u0035\u002a\u0003\u0020\u0028\u002b\u0034\u0034\u0029\u0020\u0003\u0031\u0030\u002f\u002a\u0020\u004c\u0061\u0020\u0066\u006f\u006e\u0064\u0061\u007a\u0069\u006f\u006e\u0065\u0020\u0064\u0069\u0020\u0050\u0061\u0072\u0074\u0065\u006e\u006f\u0070\u0065\u0020\u002a\u002f\u0003";
        IRCListener ircListener = new IRCListener();
        WikipediaEdit result = ircListener.parseEdit("#it.wikipedia", msg);
        assertNotNull(result);
    }
}
