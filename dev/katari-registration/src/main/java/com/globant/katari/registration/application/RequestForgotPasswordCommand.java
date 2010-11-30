package com.globant.katari.registration.application;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.validation.Errors;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Validatable;
import com.globant.katari.email.application.EmailSender;
import com.globant.katari.email.model.EmailModel;
import com.globant.katari.registration.domain.RecoverPasswordRequest;
import com.globant.katari.registration.domain.RegistrationRepository;
import com.globant.katari.registration.domain.EmailConfigurer;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** Creates a new forgot password request, and sends an email to the given
 * user.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class RequestForgotPasswordCommand implements Command<Void>,
  Validatable {

  /** The user email.*/
  private String email;

  /** The user repository. It's never null.*/
  private final UserRepository userRepository;

  /** The registration repository. It's never null.*/
  private final RegistrationRepository registrationRepository;

  /** The email sender. It's never null.*/
  private final EmailSender emailSender;

  /** The email configuration, parameters etc.*/
  private final EmailConfigurer emailConfigurer;

  /** The user that forgot his password.
   *
   * This is used only for caching, so the user is not retrieved twice. This is
   * set in the validate operation.*/
  private User user;

  /** Builds the command.
   * @param theUserRepository The user repository. It cannot be null.
   * @param theRegistrationRepository The registration repository. It cannot be
   * null.
   * @param theEmailSender The email sender. It cannot be null.
   * @param theEmailConfigurer The email configurer.It cannot be null.
   */
  public RequestForgotPasswordCommand(final UserRepository theUserRepository,
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

  /** Generates a forgot password token and sends it to the user by email.
   * @return null
   */
  public Void execute() {
    if(user == null) {
      throw new RuntimeException(
         "The user should be here, please validate before execute the command");
    }
    RecoverPasswordRequest request = new RecoverPasswordRequest(user);
    registrationRepository.saveRecoverPasswordRequest(request);
    Map<String, Object> values = new HashMap<String, Object>();
    values.put("token", request.getToken());
    values.put("userId", request.getUserId());
    EmailModel model;
    model = new EmailModel(emailConfigurer.getEmailFrom(), user.getEmail(),
        values, emailConfigurer.getPlainMessage(),
        emailConfigurer.getSubject());
    emailSender.send(model, emailConfigurer.getTemplate());
    return null;
  }

  /** Validates that the email is not null, and also that the given
   * email exists in the DB.
   *
   * {@inheritDoc}
   */
  public void validate(final Errors errors) {
    if(StringUtils.isBlank(email)) {
      errors.rejectValue("email", "forgotpassword.email.null",
          "The email cannot be null");
    } else {
      user = userRepository.findUserByEmail(email);
      if(user == null) {
        errors.rejectValue("email", "forgotpassword.email.notExist",
          "The email does not exist");
      }
    }
  }

  /** Returns the user's email.
   * @return the email.
   */
  public String getEmail() {
    return email;
  }

  /** Sets the user's email.
   * @param userEmail the email to set.
   */
  public void setEmail(final String userEmail) {
    email = userEmail;
  }
}
