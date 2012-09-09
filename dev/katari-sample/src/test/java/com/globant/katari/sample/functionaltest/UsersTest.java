/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.functionaltest;

import junit.framework.TestCase;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserFilter;
import com.globant.katari.user.domain.UserRepository;

import org.junit.Before;
import org.junit.Test;

/** Test the user module welcome page.
 */
public class UsersTest {

  /** The path relative to BASE_URL of the login page plus username and
   * password.
   */
  private static final String USERS_PATH = "/module/user/users.do";

  /** This is the implementation of the repository of the user.
   */
  private UserRepository repository;

  /** This is a set up method of this TestCase.
   *
   * Creates two users for the tests.
   *
   * @throws Exception when setup fails.
   */
  @Before
  protected final void setUp() throws Exception {
    repository = (UserRepository) SpringTestUtils.get().getBeanFactory()
    		.getBean("user.userRepository");
  }

  /** Tests that the app does returns a list of users.
   *
   * It checks for the title and that both users are found.
   *
   * @throws Exception when the test fails.
   */
  @Test
  public final void testListOfUsers() throws Exception {
    WebClient webClient = SimplePageVerifier.login(USERS_PATH);
    String [] valid = new String[] {
      "(?s).*admin.*",
      "(?s).*/.*/module/user/userView.do.*"
    };
    String [] invalid = new String[] {".*Exception.*", ".*Not Found.*"};
    SimplePageVerifier.verifyPage(webClient, USERS_PATH, "", HttpMethod.GET,
        "Users", valid, invalid);
  }
}

