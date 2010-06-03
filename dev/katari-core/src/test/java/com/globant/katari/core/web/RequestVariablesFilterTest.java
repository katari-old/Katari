/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.web.context.WebApplicationContext;

public class RequestVariablesFilterTest extends TestCase {

  /* Tests the filter.
   */
  public final void testDoFilter() throws Exception {

    // Mocks the servlet response.
    HttpServletResponse response = createMock(HttpServletResponse.class);
    replay(response);

    // Mocks the servlet request.
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getAttribute("baseweb"))
      .andReturn("Scheme://ServerName:666/");
    expectLastCall().anyTimes();
    expect(request.getAttribute("request")).andReturn(request);
    expectLastCall().anyTimes();
    expect(request.getAttribute("response")).andReturn(response);
    expectLastCall().anyTimes();
    expect(request.getScheme()).andReturn("Scheme");
    expectLastCall().anyTimes();
    expect(request.getServerName()).andReturn("ServerName");
    expectLastCall().anyTimes();
    expect(request.getServerPort()).andReturn(666);
    expectLastCall().anyTimes();
    expect(request.getContextPath()).andReturn("/");
    expectLastCall().anyTimes();
    request.setAttribute("baseweb", "Scheme://ServerName:666/");
    expectLastCall().anyTimes();
    request.setAttribute("request", request);
    expectLastCall().anyTimes();
    request.setAttribute("response", response);
    expectLastCall().anyTimes();
    replay(request);

    // Mocks the Filter.
    FilterChain chain = createMock(FilterChain.class);
    chain.doFilter(request, response);
    expectLastCall().anyTimes();
    replay(chain);

    // Mocks the Web Application Context
    WebApplicationContext wac = createMock(WebApplicationContext.class);
    replay(wac);

    // Mocks the HttpServletContext
    ServletContext servletContext = createNiceMock(ServletContext.class);
    expect(servletContext.getAttribute(
          WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
          )).andReturn(wac);
    expectLastCall().anyTimes();
    replay(servletContext);

    // Mocks the FilterConfig
    FilterConfig filterConfig = createMock(FilterConfig.class);
    expect(filterConfig.getServletContext()).andReturn(servletContext);
    expectLastCall().anyTimes();
    replay(filterConfig);

    // Execute the test.
    RequestVariablesFilter filter = new RequestVariablesFilter();
    filter.init(filterConfig);
    filter.doFilter(request, response, chain);
    assertEquals(request.getAttribute("baseweb"), "Scheme://ServerName:666/");
    assertEquals(request.getAttribute("request"), request);
    assertEquals(request.getAttribute("response"), response);

    filter.destroy();
  }
}

