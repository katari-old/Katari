/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.springframework.util.Assert;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilterEntryPoint;

/** Determines the entry point url that shows the login form, considering if
 * the user must be challenged with a captcha or not.
 *
 * This filter checks if the ip of the client attempting the login has failed
 * once. If that is the case, then the ip is considered blacklisted for login
 * without a captcha, and any further attempt will challenge the user with a
 * captcha.
 */
public class AuthenticationWithCaptchaEntryPoint extends
  AuthenticationProcessingFilterEntryPoint {

  /** The context relative url that will show the login form with a captcha
   * challenge.
   *
   * This is never null.
   */
  private String loginWithCaptchaFormUrl;

  /** The IP BlackList, cannot be null.
  */
  private IpBlacklist blackList;

  /** {@inheritDoc}
   *
   * Validates the invariants, called by the container (spring) after setting
   * all properties.
   */
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    Assert.notNull(loginWithCaptchaFormUrl,
        "loginWithCaptchaFormUrl required");
    Assert.notNull(blackList, "ipBlacklist required");
  }

  /** {@inheritDoc}
   */
  @Override
  protected String determineUrlToUseForThisRequest(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException exception) {
    if (blackList.isBlacklisted(request.getRemoteAddr())) {
      return loginWithCaptchaFormUrl;
    } else {
      return super.determineUrlToUseForThisRequest(request, response,
          exception);
    }
  }

  /** Sets the url to present the user the login form with the captcha
   * challenge.
   *
   * @param url the url of the login page with the captcha. It cannot be null.
   */
  public void setLoginWithCaptchaFormUrl(final String url) {
    Validate.notNull(url, "the captcha login form url cannot be null");
    loginWithCaptchaFormUrl = url;
  }

  /** Sets the blacklist used to decide over an invocation.
   *
   * @param theBlackList the blacklist, cannot be null
   */
  public void setIpBlacklist(final IpBlacklist theBlackList) {
    Validate.notNull(theBlackList, "Blacklist cannot be null");
    blackList = theBlackList;
  }
}

