/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.functionaltest;

import junit.framework.TestCase;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.user.domain.User;
import com.globant.katari.sample.user.domain.UserRepository;

/** Test the user module welcome page.
 *
 * @author nicolas.frontini
 */
public class UsersTest extends TestCase {

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
  protected final void setUp() throws Exception {
    repository = (UserRepository) SpringTestUtils.getBeanFactory().getBean(
        "userRepository");
    addUsers();
  }

  /** Adds a pair of users to be used in the tests.
   */
  private void addUsers() {
    // Removes the unneeded users.
    for (Object element : repository.getUsers()) {
      User user = (User) element;
      if (!user.getName().equals("admin")) {
        repository.remove(user);
      }
    }
    User user = new User("User-2", "email-2");
    user.changePassword("Pass-2");
    repository.save(user);
  }

  /** Tests the hello world servlet.
   *
   * It checks for the title and that both users are found.
   *
   * @throws Exception when the test fails.
   */
  public final void testListOfUsers() throws Exception {
    WebClient webClient = SimplePageVerifier.login(USERS_PATH);
    String [] valid = new String[] {
      "(?s).*admin.*",
      "(?s).*User-2.*",
      "(?s).*/.*/module/user/userView.do.*"
    };
    String [] invalid = new String[] {".*Exception.*", ".*Not Found.*"};
    SimplePageVerifier.verifyPage(webClient, USERS_PATH, "", HttpMethod.GET,
        "Users", valid, invalid);
  }
}

