package it.governoedits.util;

public interface Properties {
    
    /**
     * Read property `key`.
     * 
     * If the key is found in the system properties, that value is returned.
     * Otherwise the property is looked up for on the `fallbackResource` resource
     * file, which is assumed to be a Java Property file
     * @param key the name of the property
     * @param fallbackResource the name of the property file resource containing for fall back
     * @return the property value
     * @throws PropertyNotFoundException if no property with the given `key` is found
     */
    String getProperty(String key, String fallbackResource) throws PropertyNotFoundException;

}
