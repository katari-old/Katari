/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import static org.easymock.classextension.EasyMock.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.support.StaticMessageSource;

import org.acegisecurity.AuthenticationManager;
import com.octo.captcha.service.CaptchaService;
import org.junit.Test;

public class AuthenticationWithCaptchaProcessingFilterTest {

  @Test
  public final void testAfterPropertiesSet_success() throws Exception {

    AuthenticationWithCaptchaProcessingFilter filter;
    filter = new AuthenticationWithCaptchaProcessingFilter();

    /* From parent class. */
    filter.setDefaultTargetUrl("/");
    filter.setAuthenticationFailureUrl("login.do");
    filter.setAuthenticationManager(createMock(AuthenticationManager.class));

    filter.setCaptchaFailureUrl("something");
    filter.setCaptchaService(createMock(CaptchaService.class));
    filter.setIpBlacklist(new IpBlacklist(1000, true, false));
    filter.setMessageSource(new StaticMessageSource());

    filter.afterPropertiesSet();
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testAfterPropertiesSet_failure() throws Exception {

    AuthenticationWithCaptchaProcessingFilter filter;
    filter = new AuthenticationWithCaptchaProcessingFilter();

    /* From parent class. */
    filter.setDefaultTargetUrl("/");
    filter.setAuthenticationFailureUrl("login.do");
    filter.setAuthenticationManager(createMock(AuthenticationManager.class));

    filter.afterPropertiesSet();
  }

  @Test
  public final void testAttemptAuthentication_captcha() throws Exception {

    AuthenticationWithCaptchaProcessingFilter filter;
    filter = new AuthenticationWithCaptchaProcessingFilter();

    IpBlacklist blacklist = createMock(IpBlacklist.class);
    expect(blacklist.isBlacklisted("1")).andReturn(true);
    replay(blacklist);

    HttpSession session = createNiceMock(HttpSession.class);
    expect(session.getId()).andReturn("00");;
    replay(session);

    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getRemoteAddr()).andReturn("1").anyTimes();
    expect(request.getParameter("j_username")).andReturn("1");
    expect(request.getParameter("j_password")).andReturn("1");
    expect(request.getParameter("_captcha_parameter")).andReturn("00")
      .anyTimes();
    expect(request.getSession()).andReturn(session).anyTimes();
    expect(request.getSession(false)).andReturn(session);
    replay(request);

    CaptchaService captchaService = createMock(CaptchaService.class);
    expect(captchaService.validateResponseForID("00", "00")).andReturn(true);
    replay(captchaService);

    filter.setDefaultTargetUrl("/");
    filter.setAuthenticationFailureUrl("login.do");
    filter.setAuthenticationManager(createMock(AuthenticationManager.class));
    filter.setCaptchaFailureUrl("something");
    filter.setCaptchaService(captchaService);
    filter.setIpBlacklist(blacklist);

    filter.attemptAuthentication(request);

    verify(request);
    verify(captchaService);
  }

  @Test
  public final void testAttemptAuthentication_captchaNotBlacklisted() throws
    Exception {

    AuthenticationWithCaptchaProcessingFilter filter;
    filter = new AuthenticationWithCaptchaProcessingFilter();

    IpBlacklist blacklist = createMock(IpBlacklist.class);
    expect(blacklist.isBlacklisted("1")).andReturn(false);
    replay(blacklist);

    HttpSession session = createNiceMock(HttpSession.class);
    expect(session.getId()).andReturn("00");;
    replay(session);

    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getRemoteAddr()).andReturn("1").anyTimes();
    expect(request.getParameter("j_username")).andReturn("1");
    expect(request.getParameter("j_password")).andReturn("1");
    expect(request.getParameter("_captcha_parameter")).andReturn("00")
      .anyTimes();
    expect(request.getSession()).andReturn(session).anyTimes();
    expect(request.getSession(false)).andReturn(session);
    replay(request);

    CaptchaService captchaService = createMock(CaptchaService.class);
    expect(captchaService.validateResponseForID("00", "00")).andReturn(true);
    replay(captchaService);

    filter.setDefaultTargetUrl("/");
    filter.setAuthenticationFailureUrl("login.do");
    filter.setAuthenticationManager(createMock(AuthenticationManager.class));
    filter.setCaptchaFailureUrl("something");
    filter.setCaptchaService(captchaService);
    filter.setIpBlacklist(blacklist);

    filter.attemptAuthentication(request);

    verify(request);
    verify(captchaService);
  }
}

