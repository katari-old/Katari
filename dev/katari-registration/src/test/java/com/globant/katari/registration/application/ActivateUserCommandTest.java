package com.globant.katari.registration.application;


import static com.globant.katari.registration.SpringTestUtils.createSmtpServer;
import static com.globant.katari.registration.SpringTestUtils.getContext;
import static org.hamcrest.CoreMatchers.is;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.globant.katari.registration.domain.RecoverPasswordRequest;
import com.globant.katari.registration.domain.RegistrationRepository;
import com.globant.katari.tools.DummySmtpServer;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/**
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class ActivateUserCommandTest {

  private UserRepository userRepository;
  private DummySmtpServer smtpServer;
  private RegistrationRepository registrationRepository;
  private ActivateUserCommand activateUserCommand;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    smtpServer = createSmtpServer();

    activateUserCommand  = (ActivateUserCommand) getContext().getBean(
        "registration.activateUserCommand");

    userRepository = (UserRepository) getContext().getBean(
    "user.userRepository");

    registrationRepository = (RegistrationRepository) getContext().getBean(
    "registration.registrationRepository");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    smtpServer.stop();
  }

  @Test
  public void testExecute() throws Exception {

    User user = activateUserCommand.execute();
    Assert.assertNull(user);

    User newUser = new User("testingActivate", "testingActivate@globant.com");

    userRepository.save(newUser);
    RecoverPasswordRequest request = new RecoverPasswordRequest(newUser);
    registrationRepository.saveRecoverPasswordRequest(request);

    request = registrationRepository.findRecoverPasswordRequest(newUser.getId(),
        request.getToken());

    activateUserCommand.setUserId(newUser.getId());
    activateUserCommand.setToken(request.getToken());

    user = activateUserCommand.execute();
    Assert.assertNotNull(user);

    Assert.assertThat(user.getName(), is(newUser.getName()));
    Assert.assertThat(user.getEmail(), is(newUser.getEmail()));

  }

}
