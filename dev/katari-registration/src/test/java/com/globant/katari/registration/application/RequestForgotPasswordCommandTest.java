package com.globant.katari.registration.application;

import static org.easymock.EasyMock.*;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;

import com.globant.katari.registration.domain.RecoverPasswordRequest;
import com.globant.katari.registration.domain.RegistrationRepository;
import com.globant.katari.tools.DummySmtpServer;
import com.globant.katari.user.domain.User;

import static org.junit.Assert.*;

import static com.globant.katari.registration.SpringTestUtils.*;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class RequestForgotPasswordCommandTest {

  private RequestForgotPasswordCommand command;
  private RegisterUserCommand registrationCommand;
  private RegistrationRepository repository;
  private DummySmtpServer smtpServer;

  @Before
  public void setUp() throws Exception {

    get().beginTransaction();

    smtpServer = createSmtpServer();

    command = (RequestForgotPasswordCommand) get().getBean(
        "registration.requestForgotPasswordCommand");

    registrationCommand = (RegisterUserCommand) get().getBean(
      "registration.registerUserCommand");

    repository = (RegistrationRepository) get().getBean(
        "registration.registrationRepository");

    repository.getSession().createQuery("delete from User").executeUpdate();
    repository.getSession().createQuery(
        "delete from RecoverPasswordRequest").executeUpdate();

  }

  @After
  public void tearDown() {
    smtpServer.stop();
    get().endTransaction();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExecute() throws Exception {
    String email = UUID.randomUUID().toString() + "@globant.com";
    registrationCommand.setEmail(email);
    registrationCommand.setName("emiliano");

    Errors errors = createMock(Errors.class);
    replay(errors);

    User user = registrationCommand.execute(); // generate one token

    command.setEmail(email);
    command.setBaseUrl("http://somewhere");
    command.validate(errors);
    command.execute(); // generate another token.

    List<RecoverPasswordRequest> requests;
    requests = (List<RecoverPasswordRequest>) repository
        .find("from RecoverPasswordRequest where userId=?", user.getId());
    assertEquals(2, requests.size());

    RecoverPasswordRequest request = requests.get(0);
    assertNotNull(request);
    assertTrue(StringUtils.isNotBlank(request.getToken()));
  }

  @Test
  public void testExecute_fail() throws Exception {
    try {
      command.execute();
      fail("Should fail because the user is not setted yet.");
    } catch (RuntimeException e) {
    }
  }

  @Test
  public void testExecute_empty_email() throws Exception {
    Errors errors = createMock(Errors.class);
    errors.rejectValue("email", "forgotpassword.email.null",
          "The email cannot be null");
    replay(errors);
    command.validate(errors);
    verify(errors);
  }

  @Test
  public void testExecute_user_do_not_exist() throws Exception {
    command.setEmail("foo@globant.com");
    Errors errors = createMock(Errors.class);
    errors.rejectValue("email", "forgotpassword.email.notExist",
        "The email does not exist");
    replay(errors);
    command.validate(errors);
    verify(errors);
  }

}
