/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import javax.servlet.FilterChain;

import javax.servlet.ServletContext;
import javax.servlet.FilterConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.*;

public class WebletSupportFilterTest extends TestCase {

  /* Tests the weblet support filter.
   */
  public final void testDoFilter() throws Exception {

    // Mocks the context
    ServletContext context = createMock(ServletContext.class);
    replay(context);

    // Mocks the filter config
    FilterConfig filterConfig = createMock(FilterConfig.class);
    expect(filterConfig.getServletContext()).andReturn(context);
    replay(filterConfig);

    // Mocks the servlet request.
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    request.setAttribute(eq("::weblet-renderer"), isA(WebletRenderer.class));
    replay(request);

    // Mocks the servlet response.
    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    replay(response);

    // Mocks the Filter.
    FilterChain chain = createMock(FilterChain.class);
    chain.doFilter(request, response);
    expectLastCall().anyTimes();
    replay(chain);

    // Executes the test.
    WebletSupportFilter filter = new WebletSupportFilter();
    filter.init(filterConfig);
    filter.doFilter(request, response, chain);
    verify(request);
  }
}

