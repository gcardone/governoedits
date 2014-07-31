package it.governoedits.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Properties {

    private static final Logger logger = LoggerFactory.getLogger(Properties.class);
    private static final String NOT_FOUND_MSG = "Cannot find property %s. Did you try defininig"
            + " it from comand line (-D%s=<value>) or in the %s resource?";

    /**
     * Read property `key`.
     * 
     * If the key is found in the system properties, that value is returned.
     * Otherwise the property is looked up for on the `fallbackResource`
     * resource file, which is assumed to be a Java Property file
     * 
     * @param key
     *            the name of the property
     * @param fallbackResource
     *            the name of the property file resource containing for fall
     *            back
     * @return the property value
     * @throws PropertyNotFoundException
     *             if no property with the given `key` is found
     */
    public static String getProperty(String key, String fallbackResource)
            throws RequiredPropertyNotFoundException {
        Optional<String> tokenOpt = Optional.ofNullable(System.getProperties().getProperty(
                key));

        return tokenOpt.orElse(readFromResource(key, fallbackResource).orElseThrow(
                () -> new RequiredPropertyNotFoundException(String.format(NOT_FOUND_MSG, key,
                        key, fallbackResource))));

    }

    private static Optional<String> readFromResource(String key, String resource) {
        try (InputStream is = Properties.class.getResourceAsStream(resource)) {
            java.util.Properties prop = new java.util.Properties();
            prop.load(is);
            String value = prop.getProperty(key);
            return Optional.ofNullable(value);
        } catch (IOException e) {
            logger.warn("Cannot read properties file.", e);
            return Optional.empty();

        }
    }

}
