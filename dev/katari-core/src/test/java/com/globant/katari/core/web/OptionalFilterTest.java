/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.Before;

import static org.easymock.classextension.EasyMock.*;

public class OptionalFilterTest {

  private FilterConfig filterConfig;

  private HttpServletRequest request;

  private HttpServletResponse response;

  private FilterChain filterChain;

  @Before
  public final void setUp() throws Exception {

    // Mocks the filter config.
    filterConfig = createMock(FilterConfig.class);
    replay(filterConfig);

    // Mocks the servlet request.
    request = createNiceMock(HttpServletRequest.class);
    replay(request);

    // Mocks the servlet response.
    response = createNiceMock(HttpServletResponse.class);
    replay(response);

    // Mocks the filter chain.
    filterChain = createNiceMock(FilterChain.class);
    replay(filterChain);
  }

  @Test
  public final void testInit_enabled() throws Exception {

    Filter target = createMock(Filter.class);
    target.init(filterConfig);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, true);
    optionalFilter.init(filterConfig);

    verify(target);
  }

  @Test
  public final void testInit_disabled() throws Exception {

    Filter target = createMock(Filter.class);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, false);
    optionalFilter.init(filterConfig);

    verify(target);
  }

  @Test
  public final void testDoFilter_enabled() throws Exception {

    Filter target = createMock(Filter.class);
    target.doFilter(request, response, filterChain);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, true);
    optionalFilter.doFilter(request, response, filterChain);

    verify(target);
  }

  @Test
  public final void testDoFilter_disabled() throws Exception {

    Filter target = createMock(Filter.class);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, false);
    optionalFilter.doFilter(request, response, filterChain);

    verify(target);
  }

  @Test
  public final void testDestroy_enabled() throws Exception {

    Filter target = createMock(Filter.class);
    target.destroy();
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, true);
    optionalFilter.destroy();

    verify(target);
  }

  @Test
  public final void testDestroy_disabled() throws Exception {

    Filter target = createMock(Filter.class);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, false);
    optionalFilter.destroy();

    verify(target);
  }

//
//    // Mocks the filter chain.
//    FilterChain chain = new FilterChain() {
//      public void doFilter(final ServletRequest request, final
//          ServletResponse response) throws IOException {
//        log.trace("Entering doFilter");
//
//        PrintWriter writer = new PrintWriter(response.getOutputStream());
//        writer.write(
//            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
//            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
//            + " <html><head><title>aa</title></head><body>test</body></html>");
//        writer.flush();
//        log.trace("Leaving doFilter");
//      }
//    };
//
//    // Executes the test.
//    HtmlValidationFilter filter = new HtmlValidationFilter();
//    filter.init(filterConfig);
//    filter.doFilter(request, response, chain);
//    verify(response);
//  }
//
//  /* Tests that the filter throws an exception on invalid html.
//   */
//  @Test
//  public final void testDoFilter_invalidHtml() throws Exception {
//
//    // Mocks the filter chain.
//    FilterChain chain = new FilterChain() {
//      public void doFilter(final ServletRequest request, final
//          ServletResponse response) throws IOException {
//        log.trace("Entering doFilter");
//
//        PrintWriter writer = new PrintWriter(response.getOutputStream());
//        writer.write(
//            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
//            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
//            + " <html><table id='someid'><td id='someid'><banana></td></html>");
//        writer.flush();
//        log.trace("Leaving doFilter");
//      }
//    };
//
//    // Executes the test.
//    HtmlValidationFilter filter = new HtmlValidationFilter();
//    filter.init(filterConfig);
//    try {
//      filter.doFilter(request, response, chain);
//      assertTrue("Expected ServletException not thrown", true);
//    } catch (ServletException e) {
//      String message = e.getMessage();
//      assertTrue(message.matches("(?s).*ERROR: <banana> is not recognized.*"));
//    }
//  }
//
//  /* Tests that the filter ignores all errors if disabled.
//   */
//  @Test
//  public final void testDoFilter_invalidHtmlDisabled() throws Exception {
//
//    // Mocks the filter chain.
//    FilterChain chain = new FilterChain() {
//      public void doFilter(final ServletRequest request, final
//          ServletResponse response) throws IOException {
//        log.trace("Entering doFilter");
//
//        PrintWriter writer = new PrintWriter(response.getOutputStream());
//        writer.write(
//            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
//            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
//            + " <html><table id='someid'><td id='someid'><banana></td></html>");
//        writer.flush();
//        log.trace("Leaving doFilter");
//      }
//    };
//
//    // Executes the test.
//    HtmlValidationFilter filter = new HtmlValidationFilter();
//    filter.setEnabled(false);
//    filter.init(filterConfig);
//    // Would throw an exception if enabled.
//    filter.doFilter(request, response, chain);
//  }
//
//  /* Tests that the filter ignores the invalid attribute 'validator' for trails
//   * module.
//   */
//  @Test
//  public final void testDoFilter_ignoreValidator() throws Exception {
//
//    request = createNiceMock(HttpServletRequest.class);
//    expect(request.getRequestURI()).andReturn("/test");
//    expect(request.getPathInfo()).andReturn("/trails/");
//    replay(request);
//
//    // Mocks the filter chain.
//    FilterChain chain = new FilterChain() {
//      public void doFilter(final ServletRequest request, final
//          ServletResponse response) throws IOException {
//        log.trace("Entering doFilter");
//
//        PrintWriter writer = new PrintWriter(response.getOutputStream());
//        writer.write(
//            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
//            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
//            + " <html><head><title>aa</title></head><body>"
//            + "<form action='test'>"
//            + " <input type='text' validator='aa'>"
//            + "</form>"
//            + "</body></html>");
//        writer.flush();
//        log.trace("Leaving doFilter");
//      }
//    };
//
//    // Executes the test.
//    HtmlValidationFilter filter = new HtmlValidationFilter();
//    filter.setEnabled(true);
//    filter.init(filterConfig);
//    // Would throw an exception if enabled.
//    filter.doFilter(request, response, chain);
//  }
//
//  /* Tests that the filter considers the invalid attribute 'validator' if the
//   * module is not trails.
//   */
//  @Test(expected=ServletException.class)
//  public final void testDoFilter_considerValidator() throws Exception {
//
//    request = createNiceMock(HttpServletRequest.class);
//    expect(request.getPathInfo()).andReturn("/notTrails/");
//    expect(request.getRequestURI()).andReturn("/test");
//    replay(request);
//
//    // Mocks the filter chain.
//    FilterChain chain = new FilterChain() {
//      public void doFilter(final ServletRequest request, final
//          ServletResponse response) throws IOException {
//        log.trace("Entering doFilter");
//
//        PrintWriter writer = new PrintWriter(response.getOutputStream());
//        writer.write(
//            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
//            + " \"http://www.w3.org/TR/html4/strict.dtd\">"
//            + " <html><head><title>aa</title></head><body>"
//            + "<form action='test'>"
//            + " <input type='text' validator='aa'>"
//            + "</form>"
//            + "</body></html>");
//        writer.flush();
//        log.trace("Leaving doFilter");
//      }
//    };
//
//    // Executes the test.
//    HtmlValidationFilter filter = new HtmlValidationFilter();
//    filter.setEnabled(true);
//    filter.init(filterConfig);
//    // Would throw an exception if enabled.
//    filter.doFilter(request, response, chain);
//  }
}

