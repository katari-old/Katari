package com.globant.katari.registration.application;

import static com.globant.katari.registration.SpringTestUtils.*;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;

import com.globant.katari.email.application.EmailSender;
import com.globant.katari.email.model.EmailModel;
import com.globant.katari.registration.domain.EmailConfigurer;
import com.globant.katari.registration.domain.RecoverPasswordRequest;
import com.globant.katari.registration.domain.RecoverPasswordRequestFactory;
import com.globant.katari.registration.domain.RegistrationRepository;
import com.globant.katari.tools.DummySmtpServer;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class ResetPasswordCommandTest {

  private RequestForgotPasswordCommand requestCommand;
  private ResetPasswordCommand resetCommand;
  private RegisterUserCommand registrationCommand;
  private RegistrationRepository registrationRepository;
  private UserRepository userRepository;

  private EmailSender emailSender;
  private EmailConfigurer configurer;

  private DummySmtpServer smtpServer;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    smtpServer = createSmtpServer();

    emailSender = createMock(EmailSender.class);
    emailSender.send(isA(EmailModel.class), isA(String.class));
    replay(emailSender);

    configurer = new EmailConfigurer("emiliano.arango@globant.com",
        "reset", "template", "lol");

    requestCommand = (RequestForgotPasswordCommand) getContext().getBean(
        "registration.requestForgotPasswordCommand");

    registrationCommand = (RegisterUserCommand) getContext().getBean(
      "registration.registerUserCommand");

    registrationRepository = (RegistrationRepository) getContext().getBean(
      "registration.registrationRepository");

    userRepository = (UserRepository) getContext().getBean(
    "user.userRepository");

    resetCommand = new ResetPasswordCommand(registrationRepository,
        userRepository, emailSender, configurer);
  }

  @After
  public void tearDown() {
    smtpServer.stop();
  }

  @Test
  public void testExecute() {
    String email = UUID.randomUUID().toString() + "@globant.com";
    String name = "waabox_" + UUID.randomUUID().toString();
    registrationCommand.setEmail(email);
    registrationCommand.setName(name);
    registrationCommand.execute();

    Errors errors = createMock(Errors.class);
    replay(errors);

    requestCommand.setEmail(email);
    requestCommand.validate(errors);
    requestCommand.execute();

    User userFromEmail = userRepository.findUserByEmail(email);

    RecoverPasswordRequest request = null;

    request = (RecoverPasswordRequest)
      registrationRepository.getHibernateTemplate().find(
          "from RecoverPasswordRequest where userId = ?",
            userFromEmail.getId()).get(0);

    resetCommand.setUserId(userFromEmail.getId());
    resetCommand.setToken(request.getToken());
    User user = resetCommand.execute();

    assertEquals(user.getEmail(), email);
    assertEquals(user.getName(), name);
  }

  @Test
  public void testExecute_Validate_invalid_date() {
    String email = UUID.randomUUID().toString() + "@globant.com";
    String name = "waabox_" + UUID.randomUUID().toString();
    registrationCommand.setEmail(email);
    registrationCommand.setName(name);
    registrationCommand.execute();

    User user = userRepository.findUserByEmail(email);

    RecoverPasswordRequest request;
    request = RecoverPasswordRequestFactory.generate(user, new Date(
        System.currentTimeMillis() - 40 * 60 * 60 * 1000));

    registrationRepository.saveRecoverPasswordRequest(request);
    resetCommand.setUserId(user.getId());
    resetCommand.setToken(request.getToken());

    user = resetCommand.execute();
    assertNull("The user should be null!", user);
  }

}
