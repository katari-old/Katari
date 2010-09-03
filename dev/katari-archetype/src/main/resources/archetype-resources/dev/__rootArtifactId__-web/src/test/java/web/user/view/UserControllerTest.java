#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.web.user.view;

import java.util.Map;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import ${package}.web.testsupport.SpringTestUtils;
import ${package}.web.testsupport.SecurityTestUtils;

import ${package}.web.user.application.SaveUserCommand;
import ${package}.web.user.domain.User;
import ${package}.web.user.domain.UserRepository;

 /* Tests for the UserController class.
 */
public class UserControllerTest extends TestCase {

  /* The controller to test.
   *
   * This is initialized in setUp.
  */
  private UserController editUserController;

  /* The user Id.
   */
  private String userId;

  /* The user repository, obtained from spring.
   */
  UserRepository userRepository;

  /* This is a set up method of this TestCase. It creates a new user named
   * UserTest. If a user with that name exists, it deletes it.
   */
  protected final void setUp() {
    editUserController = (UserController) SpringTestUtils.getBeanFactory()
        .getBean("/userEdit.do");
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory().getBean(
        "userRepository");

    String userName = "UserTest";
    User user = userRepository.findUserByName(userName);
    if (user != null) {
      userRepository.remove(user);
    }

    // Add a user.
    user = new User(userName, "mail@none");
    user.changePassword("pass");
    userRepository.save(user);
    userId = String.valueOf(userRepository.findUserByName(userName).getId());
  }

  /* Simulates a GET request and tests the result.
   */
  public final void testGet() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.addParameter("userId", userId);

    MockHttpServletResponse response = new MockHttpServletResponse();

    ModelAndView modelAndView;
    modelAndView = editUserController.handleRequest(request, response);

    Map model = modelAndView.getModel();
    SaveUserCommand command = (SaveUserCommand) model.get("command");
    assertNotNull("Unexpected null command", command);
    assertEquals("UserTest", command.getProfile().getName());
    assertEquals("mail@none", command.getProfile().getEmail());
  }

  /* Simulates a POST request and tests the result.
   */
  public final void testPost() throws Exception {

    // A user with full privileges.
    User user = userRepository.findUserByName("admin");
    SecurityTestUtils.setContextUser(user);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("POST");
    request.addParameter("userId", userId);
    request.addParameter("profile.name", "UserTest");
    request.addParameter("profile.email", "new@none");

    MockHttpServletResponse response = new MockHttpServletResponse();

    ModelAndView modelAndView;
    modelAndView = editUserController.handleRequest(request, response);

    Map model = modelAndView.getModel();
    SaveUserCommand command = (SaveUserCommand) model.get("command");
    assertNotNull("Unexpected null command", command);
    assertEquals("UserTest", command.getProfile().getName());
    assertEquals("new@none", command.getProfile().getEmail());
  }
}

