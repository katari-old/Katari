package com.globant.katari.monitoring;

import javax.servlet.ServletContext;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Container for the spring module application context.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SpringTestUtils {

  private static final String MODULE = "classpath:applicationContext.xml";

  private static final SpringTestUtils INSTANCE = new SpringTestUtils();

  private final XmlWebApplicationContext appContext;

  private SpringTestUtils() {
    ServletContext sc;
    sc = new MockServletContext(".", new FileSystemResourceLoader());
    appContext = new XmlWebApplicationContext();
    appContext.setServletContext(sc);
    appContext.setConfigLocations(new String[] { MODULE });
    appContext.refresh();
  }

  /**
   * @return {@link XmlWebApplicationContext} the spring application context.
   */
  public static final XmlWebApplicationContext getContext() {
    return INSTANCE.appContext;
  }
}