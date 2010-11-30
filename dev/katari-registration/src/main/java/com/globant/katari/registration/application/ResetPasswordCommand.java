package com.globant.katari.registration.application;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;

import com.globant.katari.core.application.Command;
import com.globant.katari.email.application.EmailSender;
import com.globant.katari.email.model.EmailModel;
import com.globant.katari.registration.domain.EmailConfigurer;
import com.globant.katari.registration.domain.RecoverPasswordRequest;
import com.globant.katari.registration.domain.RegistrationRepository;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;
import com.globant.katari.user.integration.DomainUserDetails;

/** Resets the password for the given user id and token.
 *
 * Passwords can be reset only for tokens that are 12 hours old or less.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class ResetPasswordCommand implements Command<User> {

  /** The class logger. */
  private static final Logger LOG = getLogger(
      ResetPasswordCommand.class);

  /** The token life. */
  private static final long TIME_TO_LIVE = TimeUnit.HOURS.toMillis(12);

  /** The registration repository. It's never null */
  private final RegistrationRepository registrationRepository;

  /** The user repository. It's never null. */
  private final UserRepository userRepository;

  /** The email sender, never null.*/
  private final EmailSender emailSender;

  /** The generated token for the reset password operation.*/
  private String token;

  /** The user id.*/
  private long userId;

  /** The email configurer. */
  private final EmailConfigurer emailConfigurer;

  /** The password lenght. */
  private static final int PASSWORD_LENGHT = 20;

  /** Builds the command.
   * @param theRegistrationRepository The reg repository. Cannot be null.
   * @param theUserRepository The user repository. Cannot be null.
   * @param theEmailSender The email sender. Cannot be null.
   * @param theEmailConfigurer The email configurer. Cannot be null.
   */
  public ResetPasswordCommand(
      final RegistrationRepository theRegistrationRepository,
      final UserRepository theUserRepository, final EmailSender theEmailSender,
      final EmailConfigurer theEmailConfigurer) {
    Validate.notNull(theRegistrationRepository,
        "RegistrationRepository cannot be null");
    Validate.notNull(theUserRepository, "UserRepository cannot be null");
    Validate.notNull(theEmailSender, "EmailSender cannot be null");
    Validate.notNull(theEmailConfigurer, "EmailConfigurer cannot be null");
    registrationRepository = theRegistrationRepository;
    userRepository = theUserRepository;
    emailSender = theEmailSender;
    emailConfigurer = theEmailConfigurer;
  }

  /** Retrieve recover password request by the userId + token and also
   * checks that the token is active.
   *
   * double check that the userId and token are not null, else, will raise
   * an IllegalArgumentException.
   *
   * @return the user or null if the recover password token do not exist, or
   * the token is expired.
   *
   * @see com.globant.katari.core.application.Command#execute()
   */
  public User execute() {

    Validate.notNull(userId, "The userId cannot be null");
    Validate.notNull(token, "The token cannot be null");

    RecoverPasswordRequest request;
    request = registrationRepository.findRecoverPasswordRequest(userId, token);

    if(request == null) {
      return null;
    }

    Date creationDate = request.getCreationDate();
    Date expiration = new Date(System.currentTimeMillis() - TIME_TO_LIVE);

    if(expiration.after(creationDate)) {
      return null;
    }

    String newPassword = generatePassword();

    User user = userRepository.findUser(request.getUserId());
    user.changePassword(newPassword);
    userRepository.save(user);

    LOG.debug("Returning the user: {}", user.getEmail());

    Map<String, Object> values = new HashMap<String, Object>(1);
    values.put("newPassword", newPassword);

    EmailModel emailModel = new EmailModel(emailConfigurer.getEmailFrom(),
        user.getEmail(), values, emailConfigurer.getPlainMessage(),
        emailConfigurer.getSubject());
    emailSender.send(emailModel, emailConfigurer.getTemplate());

    LOG.debug("Authenticating the user: " + user.getEmail());
    DomainUserDetails details = new DomainUserDetails(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        details, "", details.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    return user;
  }

  /** Generate a new password for the user.
   * @return the new password.
   */
  private String generatePassword() {
    return UUID.randomUUID().toString().substring(0, PASSWORD_LENGHT);
  }

  /** Sets the user token.
   * @param userToken the token to set.
   */
  public void setToken(final String userToken) {
    token = userToken;
  }

  /** Sets the userId.
   * @param theUserId the user id.
   */
  public void setUserId(final Long theUserId) {
    userId = theUserId;
  }

}
