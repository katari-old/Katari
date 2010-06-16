/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

/* Tests the module request wrapper.
 */
public class ModuleRequestWrapperTest extends TestCase {

  public final void testGetContextPath() {
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getContextPath()).andReturn("/katari");
    expect(request.getServletPath()).andReturn("/module");
    expect(request.getPathInfo()).andReturn(null);
    replay(request);

    ModuleRequestWrapper wrapper;
    wrapper = new ModuleRequestWrapper(request, "user", "/welcome.do");
    String contextPath = wrapper.getContextPath();
    assertEquals("/katari/module/user", contextPath);
  }

  public final void testGetPathInfoReturnsNull() {
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getContextPath()).andReturn("/katari");
    expect(request.getServletPath()).andReturn("/module");
    expect(request.getPathInfo()).andReturn("/user/welcome.do");
    replay(request);

    ModuleRequestWrapper wrapper;
    wrapper = new ModuleRequestWrapper(request, "user", "/welcome.do");
    String pathInfo = wrapper.getPathInfo();
    assertNull(pathInfo);
  }

  public final void testGetPathInfoNotNull() {
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getContextPath()).andReturn("/katari");
    expect(request.getServletPath()).andReturn("/module");
    expect(request.getPathInfo()).andReturn("/user/welcome.do/pathinfo");
    replay(request);

    ModuleRequestWrapper wrapper;
    wrapper = new ModuleRequestWrapper(request, "user", "/welcome.do");
    String pathInfo = wrapper.getPathInfo();
    assertEquals("/pathinfo", pathInfo);
  }

  public final void testGetServletPath() {
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getContextPath()).andReturn("/katari");
    expect(request.getServletPath()).andReturn("/module");
    expect(request.getPathInfo()).andReturn("/user/welcome.do");
    replay(request);

    ModuleRequestWrapper wrapper;
    wrapper = new ModuleRequestWrapper(request, "user", "/welcome.do");
    String servletPath = wrapper.getServletPath();
    assertEquals("/welcome.do", servletPath);
  }

  public final void testForward() throws Exception {
    ServletResponse response = createMock(ServletResponse.class);
    replay(response);

    RequestDispatcher delegate = createNiceMock(RequestDispatcher.class);
    replay(delegate);

    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getContextPath()).andReturn("/katari");
    expect(request.getServletPath()).andReturn("/module");
    expect(request.getPathInfo()).andReturn("/user/welcome.do");
    expect(request.getRequestDispatcher("/welcome.do")).andReturn(delegate);
    replay(request);

    ModuleRequestWrapper wrapper;
    wrapper = new ModuleRequestWrapper(request, "user", "/welcome.do");
    RequestDispatcher dispatcher = wrapper.getRequestDispatcher("/welcome.do");

    dispatcher.forward(request, null);
  }
}

