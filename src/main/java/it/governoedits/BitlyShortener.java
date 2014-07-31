package it.governoedits;

import it.governoedits.util.Properties;

import java.net.MalformedURLException;
import java.net.URL;

import net.swisstech.bitly.BitlyClient;
import net.swisstech.bitly.model.Response;
import net.swisstech.bitly.model.v3.ShortenResponse;

import org.apache.http.HttpStatus;

import com.google.common.base.Preconditions;

public class BitlyShortener {

    private final static String PROPERTY_NAME = "bitly.token";
    private final static String TOKEN_RESOURCE = "/tokens.properties";
    private final BitlyClient client;

    public BitlyShortener() {
        String oauthToken = readToken();
        this.client = new BitlyClient(oauthToken);

    }

    private static String readToken() {
        return Properties.getProperty(BitlyShortener.PROPERTY_NAME, BitlyShortener.TOKEN_RESOURCE);
    }

    public String shorten(String longUrl) throws BitlyException {
        Preconditions.checkNotNull(longUrl);
        Preconditions.checkArgument(BitlyShortener.validUrl(longUrl));

        // URL appears to be encoded by the BitlyCLient library
//        String urlencoded = "";
//        try {
//            urlencoded = URLEncoder.encode(longUrl, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//           LOGGER.error("UTF-8 encoding not supported in URL encoding?", e);
//           throw new RuntimeException(e);
//        }
//        
        
        Response<ShortenResponse> resp = client.shorten().setLongUrl(longUrl).call();

        if (resp.status_code == HttpStatus.SC_OK) { // ok
            return resp.data.url;
        } else {
            final String msg = String.format(
                    "Error while shortening url %s [Response code: %d; Message: %s]", longUrl,
                    resp.status_code, resp.status_txt);
            throw new BitlyException(msg);
        }

    }

    private static boolean validUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
