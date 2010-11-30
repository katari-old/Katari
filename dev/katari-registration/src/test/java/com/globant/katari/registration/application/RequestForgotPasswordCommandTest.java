package com.globant.katari.registration.application;

import static org.easymock.classextension.EasyMock.*;

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
    
    smtpServer = createSmtpServer();
    
    command = (RequestForgotPasswordCommand) getContext().getBean(
        "registration.requestForgotPasswordCommand");

    registrationCommand = (RegisterUserCommand) getContext().getBean(
      "registration.registerUserCommand");

    repository = (RegistrationRepository) getContext().getBean(
        "registration.registrationRepository");

    repository.getHibernateTemplate().bulkUpdate("delete from User");
    repository.getHibernateTemplate().bulkUpdate(
        "delete from RecoverPasswordRequest");

  }

  @After
  public void tearDown() {
    smtpServer.stop();
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
    command.validate(errors);
    command.execute(); // generate another token.

    List<RecoverPasswordRequest> requests = repository.getHibernateTemplate()
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
