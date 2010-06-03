#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.functionaltest.selenium;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/** Define a common functionality usefull to implement a test cases for Globant
 * Templage Web application.
 */
public class GlbTemplateSelenium extends TestCase {

  /** A logger.
   */
  private static Log log = LogFactory.getLog(GlbTemplateSelenium.class);

  /** The selenium.
   */
  private DefaultSelenium selenium;

  /** Represent the timeout in selenium.
   */
  protected static final String TIMEOUT = "20000";

  /** The base url where the application is deployed.
   */
  protected static final String BASE_URL = "http://localhost:8089";

  /** The base url where the application is deployed.
   */
  protected static final String APPLICATION_URL = "/${clientName}$-{projectName}/";

  /** Get the selenium instance.
   *
   * @return Returns the selenium instance.
   */
  protected final Selenium getSelenium() {
    return selenium;
  }

  /** Login into the web application.
   */
  private void login() {
    log.info("Start login.");
    try {
      selenium.open(BASE_URL + APPLICATION_URL);
      assertEquals("Login", selenium.getTitle());
      selenium.type("username", "admin");
      assertEquals("admin", selenium.getValue("username"));
      selenium.type("password", "admin");
      assertEquals("admin", selenium.getValue("password"));
      selenium.click("//input[@value='LOGIN']");
      selenium.waitForPageToLoad(TIMEOUT);
    } catch (SeleniumException ex) {
      fail(ex.getMessage());
      throw ex;
    }
    log.info("End login.");
  }

  /** This is a set up method of this TestCase. This method is called before
   * a test is executed.
   */
  @Override
  public final void setUp() {
    try {
      super.setUp();
      selenium = createSeleniumClient(BASE_URL);
      selenium.start();
    } catch (Exception e) {
      log.error("Selenium Error: " + e);
    }
    login();
  }

  /** Get the default selenium client.
   *
   * @param url The base url to start the application web.
   * @return Returns the default selenium client.
   */
  protected final DefaultSelenium createSeleniumClient(final String url) {
    return new DefaultSelenium("localhost", 4444, "*firefox", url);
  }

  /** Tears down the fixture, for example, close a network connection.
   * This method is called after a test is executed.
   */
  @Override
  public final void tearDown() {
    log.info("Selenium: tearDown.");
    try {
      selenium.stop();
      super.tearDown();
    } catch (Exception e) {
      log.error("Selenium Error: " + e);
    }
  }
}
