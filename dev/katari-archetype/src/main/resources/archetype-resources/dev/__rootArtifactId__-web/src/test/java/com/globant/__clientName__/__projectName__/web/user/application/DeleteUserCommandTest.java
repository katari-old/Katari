#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.user.application;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
 
import com.globant.${clientName}.${projectName}.web.testsupport.SecurityTestUtils;

import com.globant.${clientName}.${projectName}.web.testsupport.SpringTestUtils;
import com.globant.${clientName}.${projectName}.web.user.domain.User;
import com.globant.${clientName}.${projectName}.web.user.domain.UserRepository;

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
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory().getBean(
        "userRepository");
    deleteUserCommand = (DeleteUserCommand) SpringTestUtils
        .getBeanFactory().getBean("deleteUserCommand");
    cleanUserRepository();
    SecurityTestUtils.setContextUser(userRepository.findUserByName("admin"));
  }

  /* Removes the unneded users.
   */
  private void cleanUserRepository() {
    for (User user : userRepository.getUsers()) {
      if (!user.getName().equals("admin")) {
        userRepository.remove(user);
      }
    }

    // Add a user.
    User user = new User(userName, "mail@none");
    user.changePassword("pass");
    userRepository.save(user);
  }

  @Test
  public final void testExecute() {
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

