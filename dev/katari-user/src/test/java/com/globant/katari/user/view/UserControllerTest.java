/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.view;

import java.util.Map;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.user.SpringTestUtils;
import com.globant.katari.user.SecurityTestUtils;

import com.globant.katari.user.application.SaveUserCommand;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserFilter;
import com.globant.katari.user.domain.UserRepository;

 /* Tests for the UserController class.
 */
public class UserControllerTest {

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
  private UserRepository userRepository;

  /** The role repository.
   */
  private RoleRepository roleRepository;

  /* This is a set up method of this TestCase. It creates a new user named
   * UserTest. If a user with that name exists, it deletes it.
   */
  @Before
  public final void setUp() {
    
    SpringTestUtils.beginTransaction();
    
    editUserController = (UserController) SpringTestUtils
        .getServletBean("/userEdit.do");
    userRepository = (UserRepository) SpringTestUtils.getBean(
        "user.userRepository");
    roleRepository = (RoleRepository) SpringTestUtils.getBean(
        "coreuser.roleRepository");

    for (Role role : roleRepository.getRoles()) {
      roleRepository.remove(role);
    }
    roleRepository.save(new Role("ADMINISTRATOR"));

    for (User user : userRepository.getUsers(new UserFilter())) {
      userRepository.remove(user);
    }
    // Add a user.
    User user = new User("admin", "admin@none");
    user.changePassword("admin");
    user.addRole(roleRepository.findRoleByName("ADMINISTRATOR"));
    userRepository.save(user);

    // A user with full privileges.
    user = userRepository.findUserByName("admin");
    SecurityTestUtils.setContextUser(user);

    // Add a user.
    user = new User("UserTest", "mail@none");
    user.changePassword("pass");
    userRepository.save(user);
    userId = String.valueOf(userRepository.findUserByName("UserTest").getId());
  }

  /* Simulates a GET request and tests the result.
   */
  @Test
  public final void testGet() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.addParameter("userId", userId);

    MockHttpServletResponse response = new MockHttpServletResponse();

    ModelAndView modelAndView;
    modelAndView = editUserController.handleRequest(request, response);

    Map<?, ?> model = modelAndView.getModel();
    SaveUserCommand command = (SaveUserCommand) model.get("command");
    assertThat(command, notNullValue());
    assertThat(command.getProfile().getName(), is("UserTest"));
    assertThat(command.getProfile().getEmail(), is("mail@none"));
  }

  /* Simulates a POST request and tests the result.
   */
  @Test
  public final void testPost() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("POST");
    request.addParameter("userId", userId);
    request.addParameter("profile.name", "UserTest");
    request.addParameter("profile.email", "new@none");

    MockHttpServletResponse response = new MockHttpServletResponse();

    ModelAndView modelAndView;
    modelAndView = editUserController.handleRequest(request, response);

    Map<?, ?> model = modelAndView.getModel();
    SaveUserCommand command = (SaveUserCommand) model.get("command");
    assertThat(command, notNullValue());
    assertThat(command.getProfile().getName(), is("UserTest"));
    assertThat(command.getProfile().getEmail(), is("new@none"));
  }
}

