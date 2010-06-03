/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.tools.ListFactory;

public class HtmlValidationFilterTest {

  private static Logger log =
    LoggerFactory.getLogger(HtmlValidationFilterTest.class);

  private FilterConfig filterConfig;

  private HttpServletRequest request;

  private HttpServletResponse response;

  private  ByteArrayOutputStream output;

  @Before
  public final void setUp() throws Exception {
    // Mocks the context
    ServletContext context = createMock(ServletContext.class);
    replay(context);

    // Mocks the filter config
    filterConfig = createMock(FilterConfig.class);
    expect(filterConfig.getServletContext()).andReturn(context);
    replay(filterConfig);

    // Mocks the servlet request.
    request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn("/test");
    replay(request);

    output = new ByteArrayOutputStream();
    // Creates a ServletOutputStream
    ServletOutputStream servletStream = new ServletOutputStream() {
      public void write(final int theByte) {
        output.write(theByte);
      }
    };

    // Mocks the servlet response.
    response = createNiceMock(HttpServletResponse.class);
    expect(response.getContentType()).andReturn("text/html");
    expect(response.getOutputStream()).andReturn(servletStream);
    replay(response);
  }

  /* Tests that the filter succeeds on valid html.
   */
  @Test
  public final void testDoFilter_validHtml() throws Exception {

    // Mocks the filter chain.
    FilterChain chain = new FilterChain() {
      public void doFilter(final ServletRequest request, final
          ServletResponse response) throws IOException {
        log.trace("Entering doFilter");

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
            + " <html><head><title>aa</title></head><body>test</body></html>");
        writer.flush();
        log.trace("Leaving doFilter");
      }
    };

    // Executes the test.
    HtmlValidationFilter filter = new HtmlValidationFilter();
    filter.init(filterConfig);
    filter.doFilter(request, response, chain);
    verify(response);
  }

  /* Tests that the filter throws an exception on invalid html.
   */
  @Test
  public final void testDoFilter_invalidHtml() throws Exception {

    // Mocks the filter chain.
    FilterChain chain = new FilterChain() {
      public void doFilter(final ServletRequest request, final
          ServletResponse response) throws IOException {
        log.trace("Entering doFilter");

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
            + " <html><table id='someid'><td id='someid'>"
            + " <non_existing_attribute></td></html>");
        writer.flush();
        log.trace("Leaving doFilter");
      }
    };

    // Executes the test.
    HtmlValidationFilter filter = new HtmlValidationFilter();
    filter.init(filterConfig);
    try {
      filter.doFilter(request, response, chain);
      assertTrue("Expected ServletException not thrown", true);
    } catch (ServletException e) {
      String message = e.getMessage();
      assertTrue(message.matches("(?s).*<non_existing_attribute>(?s).*"));
    }
  }

  /* Tests that the filter ignores all errors if disabled.
   */
  @Test
  public final void testDoFilter_invalidHtmlDisabled() throws Exception {

    // Mocks the filter chain.
    FilterChain chain = new FilterChain() {
      public void doFilter(final ServletRequest request, final
          ServletResponse response) throws IOException {
        log.trace("Entering doFilter");

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
            + " <html><table id='someid'><td id='someid'><banana></td></html>");
        writer.flush();
        log.trace("Leaving doFilter");
      }
    };

    // Executes the test.
    HtmlValidationFilter filter = new HtmlValidationFilter();
    filter.setEnabled(false);
    filter.init(filterConfig);
    // Would throw an exception if enabled.
    filter.doFilter(request, response, chain);
  }

  /* Tests that the filter ignores the invalid attribute 'validator' for trails
   * module.
   */
  @Test
  public final void testDoFilter_ignoreValidator() throws Exception {

    request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn("/test");
    expect(request.getPathInfo()).andReturn("/trails/");
    replay(request);

    // Mocks the filter chain.
    FilterChain chain = new FilterChain() {
      public void doFilter(final ServletRequest request, final
          ServletResponse response) throws IOException {
        log.trace("Entering doFilter");

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
            + " <html><head><title>aa</title></head><body>"
            + "<form action='test'>"
            + " <input type='text' validator='aa'>"
            + "</form>"
            + "</body></html>");
        writer.flush();
        log.trace("Leaving doFilter");
      }
    };

    // Executes the test.
    HtmlValidationFilter filter = new HtmlValidationFilter();
    filter.setEnabled(true);
    filter.init(filterConfig);
    // Would throw an exception if enabled.
    filter.doFilter(request, response, chain);
  }

  /* Tests that the filter ignores based on url pattern list.
   */
  @Test
  public final void testDoFilter_ignoredUrlPatternList() throws Exception {

    request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn("/ignoredPage/test");
    expect(request.getPathInfo()).andReturn("/notTrails/");
    replay(request);

    // Mocks the filter chain.
    FilterChain chain = new FilterChain() {
      public void doFilter(final ServletRequest request, final
          ServletResponse response) throws IOException {
        log.trace("Entering doFilter");

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
            + " <html><head><title>aa</title></head><body>"
            + "<form action='test'>"
            + " <input type='text' validator='aa'>"
            + "</form>"
            + "</body></html>");
        writer.flush();
        log.trace("Leaving doFilter");
      }
    };

    // Executes the test.
    HtmlValidationFilter filter = new HtmlValidationFilter();
    filter.setEnabled(true);
    filter.init(filterConfig);
    filter.setIgnoredUrlpatterns(ListFactory.create(".*/ignoredPage/.*"));
    // Would throw an exception if enabled.
    filter.doFilter(request, response, chain);
  }

  /* Tests that the filter considers the invalid attribute 'validator' if the
   * module is not trails.
   */
  @Test(expected=ServletException.class)
  public final void testDoFilter_considerValidator() throws Exception {

    request = createNiceMock(HttpServletRequest.class);
    expect(request.getPathInfo()).andReturn("/notTrails/");
    expect(request.getRequestURI()).andReturn("/test");
    replay(request);

    // Mocks the filter chain.
    FilterChain chain = new FilterChain() {
      public void doFilter(final ServletRequest request, final
          ServletResponse response) throws IOException {
        log.trace("Entering doFilter");

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
            + " <html><head><title>aa</title></head><body>"
            + "<form action='test'>"
            + " <input type='text' validator='aa'>"
            + "</form>"
            + "</body></html>");
        writer.flush();
        log.trace("Leaving doFilter");
      }
    };

    // Executes the test.
    HtmlValidationFilter filter = new HtmlValidationFilter();
    filter.setEnabled(true);
    filter.init(filterConfig);
    // Would throw an exception if enabled.
    filter.doFilter(request, response, chain);
  }
}

