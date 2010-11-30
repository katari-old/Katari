package com.globant.katari.registration.application;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.springframework.validation.Errors;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Validatable;
import com.globant.katari.email.application.EmailSender;
import com.globant.katari.email.model.EmailModel;
import com.globant.katari.registration.domain.EmailConfigurer;
import com.globant.katari.registration.domain.RecoverPasswordRequest;
import com.globant.katari.registration.domain.RegistrationRepository;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** Create a new user, checking that the user is not already registered.
 *
 * There must no be any other user with the same name or email address.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class RegisterUserCommand implements Command<User>, Validatable {

  /** The password length. */
  private static final int PASSWORD_LENGTH = 20;

  /** The user email.*/
  private String email;

  /** The user name.*/
  private String name;

  /** The user repository, never null.*/
  private final UserRepository userRepository;

  /** The registration repository, never null. */
  private final RegistrationRepository registrationRepository;

  /** The email sender, never null. */
  private final EmailSender emailSender;

  /** The email configurer, never null. */
  private final EmailConfigurer emailConfigurer;

  /** Builds the command.
   * @param theUserRepository The user repository. Cannot be null.
   * @param theRegistrationRepository The registration repository.
   *  Cannot be null.
   * @param theEmailSender The email sender. Cannot be null.
   * @param theEmailConfigurer The email configurer. Cannot be null.
   */
  public RegisterUserCommand(final UserRepository theUserRepository,
      final RegistrationRepository theRegistrationRepository,
      final EmailSender theEmailSender,
      final EmailConfigurer theEmailConfigurer) {
    Validate.notNull(theUserRepository, "UserRepository cannot be null");
    Validate.notNull(theRegistrationRepository,
        "RegistrationRepository cannot be null");
    Validate.notNull(theEmailSender, "EmailSender cannot be null");
    Validate.notNull(theEmailConfigurer, "EmailConfigurer cannot be null");
    userRepository = theUserRepository;
    registrationRepository = theRegistrationRepository;
    emailSender = theEmailSender;
    emailConfigurer = theEmailConfigurer;
  }

  /** Creates a new User.
   * @return the new user, never null.
   */
  public User execute() {
    User user = new User(name, email);
    user.changePassword(UUID.randomUUID().toString().substring(0,
        PASSWORD_LENGTH));
    userRepository.save(user);

    RecoverPasswordRequest request = new RecoverPasswordRequest(user);
    request.getToken();

    registrationRepository.saveRecoverPasswordRequest(request);

    Map<String, Object> values = new HashMap<String, Object>(2);
    values.put("userId", user.getId());
    values.put("token", request.getToken());

    EmailModel model = new EmailModel(emailConfigurer.getEmailFrom(),
        user.getEmail(), values, emailConfigurer.getPlainMessage(),
        emailConfigurer.getSubject());
    emailSender.send(model, emailConfigurer.getTemplate());

    return user;

  }

  /**
   * Validate that all required fields were provided by the user and that no
   * other user with the same name or email exists.
   *
   * @param errors the spring errors.
   */
  public void validate(final Errors errors) {
    if(isBlank(email)) {
      errors.rejectValue("email", "email.notNull", "the email cannot be null");
    }
    if(isBlank(name)) {
      errors.rejectValue("name", "name.notNull", "the name cannot be null");
    }
    User user = userRepository.findUserByEmail(email);
    if(user != null) {
      errors.reject("existing.email", "A user with that email alredy exists");
    }
    user = userRepository.findUserByName(name);
    if(user != null) {
      errors.reject("existing.name", "A user with that namd alredy exists");
    }
  }

  /** Returns the user's email.
   * @return the user email, as provided by the user.
   */
  public String getEmail() {
    return email;
  }

  /** Set the email to the command instance.
   * @param theUserEmail the user email.
   */
  public void setEmail(final String theUserEmail) {
    email = theUserEmail;
  }

  /** Returns the user's name.
   * @return the setted user name.
   */
  public String getName() {
    return name;
  }

  /** Set the user name to the command instance.
   * @param userName the user name.
   */
  public void setName(final String userName) {
    name = userName;
  }

}
