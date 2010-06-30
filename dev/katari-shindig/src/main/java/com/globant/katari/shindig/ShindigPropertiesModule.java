/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig;

import org.apache.shindig.common.PropertiesModule;

/** Defines the custom location for the shindig.properties file.
 */
public class ShindigPropertiesModule extends PropertiesModule {
  public ShindigPropertiesModule() {
    super("com/globant/katari/shindig/shindig.properties");
  }
}

