/**
 * 
 */
package com.globant.katari.registration.application;

import static com.globant.katari.registration.SpringTestUtils.createSmtpServer;
import static com.globant.katari.registration.SpringTestUtils.getContext;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;

import com.dumbster.smtp.SmtpMessage;
import com.globant.katari.registration.domain.RecoverPasswordRequest;
import com.globant.katari.registration.domain.RegistrationRepository;
import com.globant.katari.tools.DummySmtpServer;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class RegisterUserCommandTest {

  private RegisterUserCommand command;
  private UserRepository userRepository;
  private DummySmtpServer smtpServer;
  private RegistrationRepository registrationRepository;

  @Before
  public void setUp() throws Exception {

    smtpServer = createSmtpServer();

    command = (RegisterUserCommand) getContext().getBean(
        "registration.registerUserCommand");

    userRepository = (UserRepository) getContext().getBean(
        "user.userRepository");

    registrationRepository = (RegistrationRepository) getContext().getBean(
        "registration.registrationRepository");

    userRepository.getHibernateTemplate().bulkUpdate("delete from User");
  }

  @After
  public void tearDown() {
    smtpServer.stop();
  }

  @Test
  public void testExecute() throws Exception {
    command.setEmail("emiliano.arango@globant.com");
    command.setName("emiliano");
    command.execute();
    User user = userRepository.findUserByEmail(command.getEmail());
    assertEquals(command.getName(), user.getName());

    assertThat(smtpServer.getReceivedEmailSize(), is(1));

    RecoverPasswordRequest request;
    request = (RecoverPasswordRequest) registrationRepository
      .getHibernateTemplate().find(
        "from RecoverPasswordRequest where userId=?", user.getId()).get(0);

    SmtpMessage message = smtpServer.iterator().next();
    String mailBody = message.getBody();
    String subject = message.getHeaderValue("Subject");

    assertThat(mailBody, containsString(request.getToken()));
    assertThat(subject, is("Katari Registration"));
  }

  @Test
  public void testValidate_validate_parameters() throws Exception {
    Errors errors = createMock(Errors.class);
    errors.rejectValue(isA(String.class), isA(String.class), isA(String.class));
    expectLastCall().times(2);
    replay(errors);
    command.setEmail("");
    command.setName("");
    command.validate(errors);
    verify(errors);
  }

  @Test
  public void testValidate_validate_user_exist() throws Exception {

    command.setEmail("emiliano.arango@globant.com");
    command.setName("emiliano");
    command.execute();

    User user = userRepository.findUserByEmail(command.getEmail());
    assertEquals(command.getName(), user.getName());

    Errors errors = createMock(Errors.class);
    errors.reject("existing.name", "A user with that namd alredy exists");
    replay(errors);

    command.setEmail("emiliano.arango2@globant.com");
    command.setName("emiliano");
    command.validate(errors);

    verify(errors);
  }

  @Test
  public void testValidate_validate_user_exist_with_same_email() 
    throws Exception {

    command.setEmail("emiliano.arango@globant.com");
    command.setName("emiliano");
    command.execute();

    User user = userRepository.findUserByEmail(command.getEmail());
    assertEquals(command.getName(), user.getName());

    Errors errors = createMock(Errors.class);
    errors.reject("existing.email", "A user with that email alredy exists");
    replay(errors);

    command.setEmail("emiliano.arango@globant.com");
    command.setName("emilianoPepe");
    command.validate(errors);

    verify(errors);
  }

}
