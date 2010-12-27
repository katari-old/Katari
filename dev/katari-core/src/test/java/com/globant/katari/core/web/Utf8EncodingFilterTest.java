/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.classextension.EasyMock.*;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.web.context.WebApplicationContext;

public class Utf8EncodingFilterTest extends TestCase {

  /* Tests the Utf8EncodingFilter..
   */
  public final void testFilter() throws Exception {

    // Mocks the servlet response.
    HttpServletResponse response = createMock(HttpServletResponse.class);
    // response.setCharacterEncoding("UTF-8");
    replay(response);

    // Mocks the servlet request.
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    request.setCharacterEncoding("UTF-8");
    replay(request);

    // Mocks the Filter.
    FilterChain chain = createNiceMock(FilterChain.class);
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
    Utf8EncodingFilter filter = new Utf8EncodingFilter();
    filter.init(filterConfig);
    filter.doFilter(request, response, chain);
    filter.destroy();
    verify(request);
    verify(response);
  }
}

