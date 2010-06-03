/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;

import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;

/** A filter that adds a captcha validation over the normal acegi
 * authentication.
 */
public class AuthenticationWithCaptchaProcessingFilter extends
  AuthenticationProcessingFilter implements InitializingBean,
  MessageSourceAware {

  /** The service used to validate the captcha, it is never null.
   */
  private CaptchaService captchaService;

  /** The request parameter name under which the user will post the response to
   * the captcha challenge.
   *
   * It is never null.
   */
  private String captchaValidationParameter = "_captcha_parameter";

  /** The context relative url that will show the login form with a captcha
   * challenge in case the user failed to be authenticated.
   *
   * This is never null.
   */
  private String captchaFailureUrl;

  /** The IP BlackList, it is never be null.
  */
  private IpBlacklist blackList;

  /** The message source accessor to be used to access the message resources.
   *
   * This is never null.
   */
  private MessageSourceAccessor messages;

  /** {@inheritDoc}
   *
   * Validates the invariants, called by the container (spring) after setting
   * all properties.
   */
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    Assert.notNull(captchaFailureUrl, "captchaFailureUrl required");
    Assert.notNull(captchaService, "captchaService required");
    Assert.notNull(blackList, "ipBlacklist required");
    Assert.notNull(messages, "messageSource required");
  }

  /** Validates the captcha if the source ip has been blacklisted additionally
   * to the standar acegi implementation.
   *
   * {@inheritDoc}
   */
  @Override
  public Authentication attemptAuthentication(
      final HttpServletRequest request) {
    try {
      if (mustValidateCaptcha(request)) {
        validateCaptcha(request);
      }
      return super.attemptAuthentication(request);
    } catch (AuthenticationException e) {
      blackList.blacklistIp(request.getRemoteAddr());
      throw e;
    }
  }

  /** Decides if the filter must validate also the captcha.
   *
   * @param request The servlet request, it cannot be null.
   *
   * @return true if the source ip is blacklisted, so the user must pass a
   * captcha challenge, false otherwise.
   */
  private boolean mustValidateCaptcha(final HttpServletRequest request) {
    // We also validate the captcha if the user entered a captcha token. This
    // is for the case where the user failed a login, the captcha is shown, and
    // then retries to log in after the blacklisting timeout.
    if (request.getParameter(captchaValidationParameter) != null) {
      return true;
    }
    return blackList.isBlacklisted(request.getRemoteAddr());
  }

  /** Validates the captcha sent by the user.
   *
   * A failure to validate the captcha throws an exception
   * (AuthenticationException).
   *
   * @param request The servlet request, it cannot be null.
   */
  private void validateCaptcha(final HttpServletRequest request) {
    HttpSession session = request.getSession();

    if (session == null) {
      throw new AuthenticationServiceException(messages.getMessage(
            "AuthenticationWithCaptchaProcessingFilter.incorrectSecurityCode",
            "Incorrect security code"));
    }
    String id = session.getId();
    String captchaResponse = request.getParameter(captchaValidationParameter);
    try {
      if (!captchaService.validateResponseForID(id, captchaResponse)) {
        throw new AuthenticationServiceException(messages.getMessage(
              "AuthenticationWithCaptchaProcessingFilter.incorrectSecurityCode",
              "Incorrect security code"));
      }
    } catch (CaptchaServiceException e) {
      // If the session expired before validating the captcha,
      // validateResponseForID throws this exception. We just ignore this case
      // and trick the user into thinking that he misspelled the captcha.
      throw new AuthenticationServiceException(messages.getMessage(
            "AuthenticationWithCaptchaProcessingFilter.incorrectSecurityCode",
            "Incorrect security code"));
    }
  }

  /** {@inheritDoc}
   *
   * If the fail was related to the captcha, the url returned is
   * captchaFailureUrl (see setCaptchaFailureUrl).
   */
  protected String determineFailureUrl(final HttpServletRequest request,
      final AuthenticationException failed) {
    if (mustValidateCaptcha(request)) {
      return captchaFailureUrl;
    } else {
      return super.determineFailureUrl(request, failed);
    }
  }

  /** Sets the captcha service implementation.
   *
   * @param service The captcha service, used to validate the user provided
   * captcha response. It cannot be null.
   */
  public void setCaptchaService(final CaptchaService service) {
    Validate.notNull(service, "The captcha service cannot be null.");
    captchaService = service;
  }

  /** Sets the request parameter where the user will post the response to the
   * captcha challenge.
   *
   * @param parameter the request parameter name, cannot be null.
   */
  public void setCaptchaValidationParameter(final String parameter) {
    Validate.notNull(parameter,
        "The captcha request parameter name cannot be null.");
    captchaValidationParameter = parameter;
  }

  /** Sets the url to present the user the login form with the captcha
   * challenge after a failed login.
   *
   * @param url the url of the login page with the captcha. It cannot be null.
   */
  public void setCaptchaFailureUrl(final String url) {
    Validate.notNull(url, "the captcha failure url cannot be null");
    captchaFailureUrl = url;
  }

  /** Sets the blacklist used to decide over an invocation.
   *
   * @param ipBlackList the ip blacklist, cannot be null
   */
  public void setIpBlacklist(final IpBlacklist ipBlackList) {
    Validate.notNull(ipBlackList, "Blacklist cannot be null");
    blackList = ipBlackList;
  }

  /** Creates and sets the message source accessor from a message source (see
   * messages).
   *
   * @param messageSource the provided message source. It cannot be null.
   */
  @Override
  public void setMessageSource(final MessageSource messageSource) {
    Validate.notNull(messageSource, "The message source cannot be null.");
    messages = new MessageSourceAccessor(messageSource);
  }
}

