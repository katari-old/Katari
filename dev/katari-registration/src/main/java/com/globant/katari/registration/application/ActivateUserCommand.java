package com.globant.katari.registration.application;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.lang.StringUtils;
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

/**
 * Activate the given user. This belongs to the registration workflow,
 * the user recives an email with a token. This object takes that token
 * and if it's match with the given userId will activate the given user.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class ActivateUserCommand implements Command<User> {

  /** The class logger. */
  private static final Logger LOG = getLogger(ActivateUserCommand.class);

  /** The user id.*/
  private Long userId;

  /** The user token.*/
  private String token;

  /** The registration repository, never null.*/
  private final RegistrationRepository registrationRepository;

  /** The user repository, never null.*/
  private final UserRepository userRepository;

  /** The email sender, never null. */
  private final EmailSender emailSender;

  /** The email configurer, never null. */
  private final EmailConfigurer emailConfigurer;

  /** Create a new instance of the command.
   * @param theUserRepository the user repository. Cannot be null.
   * @param theRegistrationRepository the registration repository.
   * @param theEmailSender the email sender. Cannot be null.
   * @param theEmailConfigurer the email configurer. Cannot be null.
   * Cannot be null.
   */
  public ActivateUserCommand(final UserRepository theUserRepository,
      final RegistrationRepository theRegistrationRepository,
      final EmailSender theEmailSender,
      final EmailConfigurer theEmailConfigurer) {
    Validate.notNull(theUserRepository, "UserRepository cannot be null");
    Validate.notNull(theRegistrationRepository,
        "RegistrationRepository cannot be null");
    Validate.notNull(theEmailSender, "EmailSender cannot be null");
    Validate.notNull(theEmailConfigurer, "EmailConfigurer cannot be null");
    registrationRepository = theRegistrationRepository;
    userRepository = theUserRepository;
    emailSender = theEmailSender;
    emailConfigurer = theEmailConfigurer;
  }

  /** Activate the given user.
   *
   * {@inheritDoc}
   *
   * @see com.globant.katari.core.application.Command#execute()
   */
  public User execute() {
    if(StringUtils.isBlank(token) || userId == null) {
      return null;
    }
    RecoverPasswordRequest request;
    request = registrationRepository.findRecoverPasswordRequest(userId, token);
    if(request != null) {
      long id = request.getUserId();
      User user = userRepository.findUser(id);
      user.activate();
      userRepository.save(user);
      Map<String, Object> values = new HashMap<String, Object>(2);
      values.put("newPassword", user.getPassword());

      EmailModel model = new EmailModel(emailConfigurer.getEmailFrom(),
          user.getEmail(), values, emailConfigurer.getPlainMessage(),
          emailConfigurer.getSubject());
      emailSender.send(model, emailConfigurer.getTemplate());

      LOG.debug("Authenticating the user: {}", user.getEmail());
      DomainUserDetails details = new DomainUserDetails(user);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          details, "", details.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      return user;
    }
    return null;
  }

  /** Sets the user id.
   * @param id the userId to set.
   */
  public void setUserId(final Long id) {
    userId = id;
  }

  /** Sets the user token.
   * @param registrationToken the token to set.
   */
  public void setToken(final String registrationToken) {
    token = registrationToken;
  }

}
