#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.globant.${clientName}.${projectName}.web.functionaltest.selenium;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** This class represents a TestCase of the users list. In this class we will
 * test that a list of users are shown in the web page.
 */
public class UserListTest extends GlbTemplateSelenium {

  /** A logger.
   */
  private static Log log = LogFactory.getLog(UserListTest.class);

  /** Verify that a list of users are shown in the web page.
   */
  public final void testUserList() {
    log.info("Start the testUserList test.");
    getSelenium().open(BASE_URL + APPLICATION_URL);
    assertEquals("Users", getSelenium().getTitle());
    getSelenium().click("//input[@id='showUsers']");
    getSelenium().waitForPageToLoad(TIMEOUT);
    assertTrue(getSelenium().isVisible("//table[@id='userList']"));
  }
}

