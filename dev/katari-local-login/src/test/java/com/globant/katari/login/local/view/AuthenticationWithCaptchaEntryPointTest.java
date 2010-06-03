/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import static org.easymock.classextension.EasyMock.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import static org.junit.Assert.*;

import org.acegisecurity.AuthenticationException;

public class AuthenticationWithCaptchaEntryPointTest {

  @Test
  public final void testAfterPropertiesSet_success() throws Exception {

    AuthenticationWithCaptchaEntryPoint entryPoint;
    entryPoint = new AuthenticationWithCaptchaEntryPoint();

    /* From parent class. */
    entryPoint.setLoginFormUrl("something");

    entryPoint.setLoginWithCaptchaFormUrl("something");
    entryPoint.setIpBlacklist(new IpBlacklist(1000, true, false));
    entryPoint.afterPropertiesSet();
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testAfterPropertiesSet_failure() throws Exception {
    AuthenticationWithCaptchaEntryPoint entryPoint;
    entryPoint = new AuthenticationWithCaptchaEntryPoint();

    entryPoint.afterPropertiesSet();
  }

  @Test
  public final void testDetermineUrlToUseForThisRequest_noCaptcha() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpServletResponse response = createMock(HttpServletResponse.class);

    AuthenticationException exception;
    exception = createMock(AuthenticationException.class);

    IpBlacklist blacklist = createMock(IpBlacklist.class);

    AuthenticationWithCaptchaEntryPoint entryPoint;
    entryPoint = new AuthenticationWithCaptchaEntryPoint();

    entryPoint.setLoginFormUrl("no-captcha");
    entryPoint.setLoginWithCaptchaFormUrl("captcha");
    entryPoint.setIpBlacklist(blacklist);

    assertEquals("no-captcha", entryPoint.determineUrlToUseForThisRequest(
          request, response, exception));
  }

  @Test
  public final void testDetermineUrlToUseForThisRequest_captcha() {
    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpServletResponse response = createMock(HttpServletResponse.class);

    AuthenticationException exception;
    exception = createMock(AuthenticationException.class);

    // Force a blacklisted ip response.
    IpBlacklist blacklist = createMock(IpBlacklist.class);
    expect(blacklist.isBlacklisted(null)).andReturn(true);
    replay(blacklist);

    AuthenticationWithCaptchaEntryPoint entryPoint;
    entryPoint = new AuthenticationWithCaptchaEntryPoint();

    entryPoint.setLoginFormUrl("no-captcha");
    entryPoint.setLoginWithCaptchaFormUrl("captcha");
    entryPoint.setIpBlacklist(blacklist);

    assertEquals("captcha", entryPoint.determineUrlToUseForThisRequest(
          request, response, exception));
  }
}

