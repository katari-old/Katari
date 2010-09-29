/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.application;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.user.SecurityTestUtils;
import com.globant.katari.user.SpringTestUtils;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserFilter;
import com.globant.katari.user.domain.UserRepository;

/* This class represents a TestCase of the user filter command. In this class
 * we will test all the features of the user filter command.
 */
public class DeleteUserCommandTest {

  private DeleteUserCommand deleteUserCommand;

  private UserRepository userRepository;

  private String userName = "UserTest";

  /* Deletes all users except admin and fakes the login user as admin.
   */
  @Before
  public final void setUp() {
    SpringTestUtils.beginTransaction();
    userRepository = (UserRepository) SpringTestUtils.getBean(
        "user.userRepository");
    deleteUserCommand = (DeleteUserCommand) SpringTestUtils.getServletBean(
        "deleteUserCommand");
    cleanUserRepository();
    SecurityTestUtils.setContextUser(userRepository.findUserByName("admin"));
  }

  /* Removes the unneded users.
   */
  private void cleanUserRepository() {
    for (User user : userRepository.getUsers(new UserFilter())) {
      userRepository.remove(user);
    }

    // Add a user.
    User user = new User("admin", "admin@none");
    user.changePassword("admin");
    userRepository.save(user);

    user = new User(userName, "mail@none");
    user.changePassword("pass");
    userRepository.save(user);
  }

  @Test
  public final void testExecute_pass() {
    User user = userRepository.findUserByName(userName);
    deleteUserCommand.setUserId(String.valueOf(user.getId()));

    assertThat(userRepository.findUserByName(userName), notNullValue());
    deleteUserCommand.execute();
    assertThat(userRepository.findUserByName(userName), nullValue());
  }

  @Test(expected = RuntimeException.class)
  public final void testExecute_failDeleteMyself() {
    SecurityTestUtils.setContextUser(userRepository.findUserByName(userName));
    User user = userRepository.findUserByName(userName);
    deleteUserCommand.setUserId(String.valueOf(user.getId()));

    assertNotNull(userRepository.findUserByName(userName));
    deleteUserCommand.execute();
    fail();
  }
}

