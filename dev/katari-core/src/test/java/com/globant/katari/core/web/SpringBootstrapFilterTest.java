/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.web.context.WebApplicationContext;

public class SpringBootstrapFilterTest extends TestCase {

  HttpServletRequest request = null;

  HttpServletResponse response = null;

  FilterChain chain = null;

  Filter filter = null;

  public void setUp() throws Exception {
    // Mocks the servlet request.
    request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn(
        "/module/user/static/test_image.gif");
    expectLastCall().anyTimes();
    expect(request.getRequestURL()).andReturn(new StringBuffer());
    expectLastCall().anyTimes();
    expect(request.getServletPath()).andReturn("/static/test_image.gif");
    expectLastCall().anyTimes();
    expect(request.getDateHeader("If-Modified-Since")).andReturn(0l);
    expect(request.getContextPath()).andReturn("/katari-web");
    expectLastCall().anyTimes();
    expect(request.getMethod()).andReturn("GET");
    expectLastCall().anyTimes();
    expect(request.getProtocol()).andReturn("http");
    expectLastCall().anyTimes();
    expect(request.getPathInfo()).andReturn("/user/welcome.do");
    expectLastCall().anyTimes();
    replay(request);

    // Mocks the Response's output stream
    ServletOutputStream mockOutputStream = createMock(ServletOutputStream.class);
    mockOutputStream.write(isA(byte[].class), eq(0), eq(776));
    mockOutputStream.flush();
    replay(mockOutputStream);

    // Mocks the servlet response.
    response = createMock(HttpServletResponse.class);
    expect(response.getOutputStream()).andReturn(mockOutputStream);
    expectLastCall().anyTimes();
    response.setContentType("image/gif");
    expectLastCall().anyTimes();
    response.setDateHeader(same("Date"), anyLong());
    expectLastCall().anyTimes();
    response.setDateHeader(same("Expires"), anyLong());
    expectLastCall().anyTimes();
    response.setDateHeader(same("Retry-After"), anyLong());
    expectLastCall().anyTimes();
    response.setHeader(same("Cache-Control"), (String) anyObject());
    expectLastCall().anyTimes();
    response.setDateHeader(same("Last-Modified"), anyLong());
    expectLastCall().anyTimes();
    replay(response);

    chain = createMock(FilterChain.class);

    // A sample filter.
    filter = createMock(Filter.class);
    filter.init(isA(FilterConfig.class));
    filter.doFilter(isA(ServletRequest.class), isA(ServletResponse.class),
        isA(FilterChain.class));
    filter.destroy();
    replay(filter);
  }

  private final FilterConfig createConfig(final String beanName) throws
    Exception {

    // Mocks the Web Application Context
    WebApplicationContext wac = createMock(WebApplicationContext.class);
    if (beanName == null) {
      expect(wac.getBean("katari.moduleFilterProxy")).andReturn(filter);
    } else {
      expect(wac.getBean(beanName)).andReturn(filter);
    }
    expectLastCall().anyTimes();
    replay(wac);

    // Mocks the servlet context.
    ServletContext context = createMock(ServletContext.class);
    expect(context.getServletContextName()).andReturn("/module/user");
    expect(context.getAttribute(
          WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)
        ).andReturn(wac);
    expectLastCall().anyTimes();
    // Under some conditions, the init method asks context to log the call.
    context.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(context);

    // Mocks the servlet config.
    FilterConfig config = createMock(FilterConfig.class);
    expect(config.getServletContext()).andReturn(context);
    expect(config.getInitParameter("filterBeanName")).andReturn(beanName);
    expectLastCall().anyTimes();
    replay(config);

    return config;
  }

  /*
   * Tests if doFilter correctly dispatches the request.
   */
  public final void testDoFilterDefaulBeanName() throws Exception {

    FilterConfig config = createConfig(null);

    SpringBootstrapFilter springBootstrapFilter = new SpringBootstrapFilter();
    springBootstrapFilter.init(config);
    springBootstrapFilter.doFilter(request, response, chain);
    springBootstrapFilter.destroy();
    verify(filter);
  }

  /*
   * Tests if doFilter correctly dispatches the request.
   */
  public final void testDoFilterSpecificBeanName() throws Exception {

    FilterConfig config = createConfig("SomeBeanName");

    SpringBootstrapFilter springBootstrapFilter = new SpringBootstrapFilter();
    springBootstrapFilter.init(config);
    springBootstrapFilter.doFilter(request, response, chain);
    springBootstrapFilter.destroy();
    verify(filter);
  }
}

