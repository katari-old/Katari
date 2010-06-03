/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.AuthenticationEntryPoint;

import static org.easymock.classextension.EasyMock.*;

import junit.framework.TestCase;

public class DelegatingEntryPointTest extends TestCase {

  public void testCommence_ok() throws Exception {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);
    AuthenticationException exception;
    exception = createMock(AuthenticationException.class);

    AuthenticationEntryPoint delegate;
    delegate = createMock(AuthenticationEntryPoint.class);
    delegate.commence(request, response, exception);
    replay(delegate);

    DelegatingEntryPoint entryPoint = new DelegatingEntryPoint();
    entryPoint.setDelegate(delegate);
    entryPoint.commence(request, response, exception);
    verify(delegate);
  }

  public void testCommence_notInit() throws Exception {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);
    AuthenticationException exception;
    exception = createMock(AuthenticationException.class);

    DelegatingEntryPoint entryPoint = new DelegatingEntryPoint();
    try {
      entryPoint.commence(request, response, exception);
      fail("commence did not throw the illegal state exception");
    } catch (IllegalStateException e) {
      // Test passed
    }
  }
}

