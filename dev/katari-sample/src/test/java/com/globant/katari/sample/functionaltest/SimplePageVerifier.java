/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.functionaltest;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/** Utility test to verify pages.
 *
 * The test can log to an application, perform the request provided, verify
 * that the result contains an html page with the specified title, and verify
 * the presence and absence of the provided java regular expressions.
 *
 * @author nicolas.frontini
 */
public final class SimplePageVerifier extends TestCase {

  /** The base url where the application is deployed.
   */
  private static final String BASE_URL =
      "http://localhost:8099/katari-sample";

  /** The form submition name.
   */
  private static final String FORM_SUBMITION = "login";

  /** The form submit button name.
   */
  private static final String SUBMIT_BUTTON_NAME = "loginButton";

  /** The input username name.
   */
  private static final String INPUT_USERNAME = "username";

  /** The input username name for the acegi built in filter.
   */
  private static final String INPUT_USERNAME_ACEGI_FILTER = "j_username";

  /** The input password name.
   */
  private static final String INPUT_PASSWORD = "password";

  /** The input password name for the acegi built in filter.
   */
  private static final String INPUT_PASSWORD_ACEGI_FILTER = "j_password";

  /** The user name.
   */
  private static final String USERNAME = "admin";

  /** The password of the user name.
   */
  private static final String PASSWORD = "admin";

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(SimplePageVerifier.class);

  /** A private constructor so no instances are created.
   */
  private SimplePageVerifier() {
  }

  /** Logs a user in to the application and returns the web client.
   *
   * The name of the form submition is 'login'. The names of the inputs
   * elements are 'username' and 'password'. The logged user name is 'admin'
   * and the password is 'admin'.
   *
   * @param url The relative url. It cannot be null.
   *
   * @return The <code>WebClient</code> with the user logged.
   *
   * @exception Exception If there is an error.
   */
  public static WebClient login(final String url) throws Exception {
    Validate.notNull(url, "The relative url cannot be null.");
    log.trace("Entering login");

    URL fullUrl = new URL(BASE_URL + url);
    WebClient webClient = new WebClient();
    HtmlPage loginPage = (HtmlPage) webClient.getPage(fullUrl);

    log.debug("Page {}: \n{}\n", fullUrl, loginPage.asXml());

    HtmlForm loginForm = loginPage.getFormByName(FORM_SUBMITION);
    assertNotNull(loginForm);

    HtmlInput usernameInput = null;
    try {
      usernameInput = loginForm.getInputByName(INPUT_USERNAME);
    } catch (ElementNotFoundException ex) {
      usernameInput = loginForm.getInputByName(INPUT_USERNAME_ACEGI_FILTER);
    }
    assertNotNull(usernameInput);

    HtmlInput passwordInput = null;
    try {
      passwordInput = loginForm.getInputByName(INPUT_PASSWORD);
    } catch (ElementNotFoundException ex) {
      passwordInput = loginForm.getInputByName(INPUT_PASSWORD_ACEGI_FILTER);
    }
    assertNotNull(passwordInput);

    usernameInput.setValueAttribute(USERNAME);
    passwordInput.setValueAttribute(PASSWORD);

    HtmlInput loginButton = loginForm.getInputByName(SUBMIT_BUTTON_NAME);
    HtmlPage homePage = (HtmlPage) loginButton.click();
    log.debug("Login button click: \n{}\n", homePage.asXml());
    assertNotNull(homePage);

    log.trace("Leaving login");
    return webClient;
  }

  /** Verify a page.
   *
   * Perform the request provided, verify that the result contains an html page
   * with the specified title, and verify the presence and absence of the
   * provided java regular expressions.
   *
   * @param webClient The web client. It cannot be null.
   *
   * @param url The relative url. It cannot be null.
   *
   * @param requestParameters The parameters of the request. It cannot be null.
   *
   * @param submitMethod The submit method (GET/POST). It cannot be null.
   *
   * @param title The page title regexp. It cannot be null.
   *
   * @param matchRegExp A <code>String[]</code> of Java regular expression to
   * match. It cannot be null.
   *
   * @param notMatchRegExp A <code>String[]</code> of Java regular expression
   * to not match. It cannot be null.
   *
   * @throws Exception If there is an error.
   */
  public static void verifyPage(final WebClient webClient, final String url,
      final String requestParameters, final HttpMethod httpMethod,
      final String titleRegExp, final String[] matchRegExp,
      final String[] notMatchRegExp) throws Exception {
    Validate.notNull(webClient, "The web client cannot be null.");
    Validate.notNull(url, "The url cannot be null.");
    Validate.notNull(requestParameters, "The request parameters cannot "
        + "be null.");
    Validate.notNull(httpMethod, "The http method cannot be null.");
    Validate.notNull(titleRegExp, "The title regexp cannot be null.");
    Validate.notNull(matchRegExp, "The regular expression cannot be null.");
    Validate.notNull(notMatchRegExp, "The regular expression cannot be null.");

    log.trace("Entering verifyPage");

    String location = BASE_URL + url + requestParameters;
    WebRequestSettings webRequestSettings;
    webRequestSettings = new WebRequestSettings(new URL(location), httpMethod);

    // Verify the title page.
    HtmlPage page = (HtmlPage) webClient.getPage(webRequestSettings);
    log.debug("Page {}: \n{}\n", location, page.asXml());
    assertNotNull(page);
    assertTrue("The regular expression '" + titleRegExp
        + "' does not matches the page title: \n" + page.getTitleText(), page
        .getTitleText().matches(titleRegExp));

    String pageText = page.asXml();

    // Verify that the regular expression match with the content.
    for (String regExp : matchRegExp) {
      assertTrue("The regular expression '" + regExp
          + "' is not in the page: \n" + pageText, pageText.matches(regExp));
    }

    // Verify that the regular expression not match with the content.
    for (String regExp : notMatchRegExp) {
      assertFalse("The regular expression '" + regExp
          + "' is in the page: \n" + pageText, pageText.matches(regExp));
    }
    log.trace("Leaving verifyPage");
  }

  /** Verify a page.
   *
   * Perform the request provided, verify that the result contains an html page
   * with the specified title.
   *
   * @param webClient The web client. It cannot be null.
   *
   * @param url The url. It cannot be null.
   *
   * @param requestParameters The parameters of the request. It cannot be null.
   *
   * @param submitMethod The submit method (GET/POST). It cannot be null.
   *
   * @param title The page title. It cannot be null.
   *
   * @throws Exception If there is an error.
   */
  public static void verifyPage(final WebClient webClient, final String url,
      final String requestParameters, final HttpMethod httpMethod,
      final String title) throws Exception {
    Validate.notNull(webClient, "The web client cannot be null.");
    Validate.notNull(url, "The url cannot be null.");
    Validate.notNull(requestParameters, "The request parameters cannot"
        + " be null.");
    Validate.notNull(httpMethod, "The http method cannot be null.");
    Validate.notNull(title, "The title cannot be null.");

    log.trace("Entering verifyPage");

    String location = BASE_URL + url + requestParameters;
    WebRequestSettings webRequestSettings;
    webRequestSettings = new WebRequestSettings(new URL(location), httpMethod);

    // Verify the title page.
    HtmlPage page = (HtmlPage) webClient.getPage(webRequestSettings);
    log.debug("Page {}: \n{}\n", location, page.asXml());
    assertNotNull(page);
    assertEquals(title, page.getTitleText());
    log.trace("Leaving verifyPage");
  }
}

