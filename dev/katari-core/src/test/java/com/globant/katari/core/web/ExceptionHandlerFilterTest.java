/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class ExceptionHandlerFilterTest {

  MockHttpServletRequest request;
  MockHttpServletResponse response;
  MockFilterConfig filterConfig = new MockFilterConfig();

  ExceptionHandlerFilter filter;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testDoFilter_errorPage() throws Exception {
    filter = new ExceptionHandlerFilter("/module/decorator/error.ftl", false);
    filter.init(filterConfig);
    FilterChain chain = new FilterChainMock("RuntimeException");
    filter.doFilter(request, response, chain);
    filter.destroy();
    assertThat(response.getIncludedUrl(), is("/module/decorator/error.ftl"));
    assertThat(request.getAttribute("exception"), is(notNullValue()));
  }

  @Test
  public void testDoFilter_noError() throws Exception {
    filter = new ExceptionHandlerFilter("/module/decorator/error.ftl", false);
    filter.init(filterConfig);
    FilterChain chain = new FilterChainMock(null);
    filter.doFilter(request, response, chain);
    filter.destroy();
    assertThat(response.getIncludedUrl(), is(nullValue()));
    assertThat(request.getAttribute("exception"), is(nullValue()));
  }

  @Test(expected = ServletException.class)
  public void testDoFilter_debugModeServlet() throws Exception {
    filter = new ExceptionHandlerFilter("/module/decorator/error.ftl", true);
    filter.init(filterConfig);
    FilterChain chain = new FilterChainMock("ServletException");
    filter.doFilter(request, response, chain);
  }

  @Test(expected = IOException.class)
  public void testDoFilter_debugModeIo() throws Exception {
    filter = new ExceptionHandlerFilter("/module/decorator/error.ftl", true);
    filter.init(filterConfig);
    FilterChain chain = new FilterChainMock("IOException");
    filter.doFilter(request, response, chain);
  }

  @Test(expected = RuntimeException.class)
  public void testDoFilter_debugModeRuntime() throws Exception {
    filter = new ExceptionHandlerFilter("/module/decorator/error.ftl", true);
    filter.init(filterConfig);
    FilterChain chain = new FilterChainMock("RuntimeException");
    filter.doFilter(request, response, chain);
  }

  @Test
  public void testDoFilter_debugModeAndPreview() throws Exception {
    filter = new ExceptionHandlerFilter("/module/decorator/error.ftl", true);
    filter.init(filterConfig);
    request.setParameter("previewErrorPage", "1");
    FilterChain chain = new FilterChainMock("IOException");
    filter.doFilter(request, response, chain);
    assertThat(response.getIncludedUrl(), is("/module/decorator/error.ftl"));
    assertThat(request.getAttribute("exception"), is(notNullValue()));
  }

  private static class FilterChainMock implements FilterChain {

    private String exceptionName;

    public FilterChainMock(final String theExceptionName) {
      exceptionName = theExceptionName;
    }

    public void doFilter(final ServletRequest request,
        final ServletResponse response) throws IOException, ServletException {
      if ("RuntimeException".equals(exceptionName)) {
        throw new RuntimeException("1 - Message");
      } else if ("ServletException".equals(exceptionName)) {
        throw new ServletException("2 - Message");
      } else if ("IOException".equals(exceptionName)) {
        throw new IOException("3 - Message");
      }
    }
  }
}

