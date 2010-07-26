/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig;

import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.shindig.common.PropertiesModule;

/** Defines the custom location for the shindig.properties file.
 */
public class ShindigPropertiesModule extends PropertiesModule {

  private final String contextPath;

  public ShindigPropertiesModule(final String theContextPath) {
    super("com/globant/katari/shindig/shindig.properties");

    Validate.notNull(theContextPath, "The context path cannot be null.");
    contextPath = theContextPath;

    Properties properties = getProperties();
    for (Object o : properties.keySet()) {
      String key = (String) o;
      String value;
      value = properties.getProperty(key).replaceAll("%context%", contextPath);
      properties.setProperty(key, value);
    }
  }
}

