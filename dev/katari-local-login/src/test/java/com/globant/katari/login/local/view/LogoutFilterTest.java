/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.acegisecurity.ui.logout.LogoutHandler;

/**
 * Test cases for the Logout filter class.
 *
 * @author mariano.nardi
 */
public class LogoutFilterTest extends TestCase {

  private LogoutHandler logoutHandler;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  @Override
  public void setUp() throws Exception {

    filterChain = createNiceMock(FilterChain.class);
    replay(filterChain);

    request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn("context/filterUrl");
    expect(request.getContextPath()).andReturn("context").anyTimes();

    response = createMock(HttpServletResponse.class);

    logoutHandler = createMock(LogoutHandler.class);
    logoutHandler.logout(request, response, null);
    replay(logoutHandler);
  }

  /** Tests a succesfull logout.
   *
   * @throws ServletException
   * @throws IOException
   */
  public void testSendRedirect_contextPath() throws Exception {

    expect(request.getQueryString()).andReturn(
        "param2=value2&param1=value1").times(2);
    replay(request);

    expect(response.encodeRedirectURL(
          "context/succcessURL?param2=value2&param1=value1"))
      .andReturn("context/succcessURL?param2=value2&param1=value1");
    response.sendRedirect("context/succcessURL?param2=value2&param1=value1");
    replay(response);

    LogoutFilter logoutFilter =
        new LogoutFilter("/succcessURL", new LogoutHandler[] { logoutHandler });
    logoutFilter.setFilterProcessesUrl("/filterUrl");
    logoutFilter.doFilter(request, response, filterChain);
    verify(response);
  }

  /* Tests if we correctly redirect to an http location.
   */
  public void testSendRedirect_http() throws Exception {
    expect(request.getQueryString()).andReturn(
        "param2=value2&param1=value1").times(2);
    replay(request);

    expect(response.encodeRedirectURL(
          "http://localhost/url?param2=value2&param1=value1"))
      .andReturn("http://localhost/url?param2=value2&param1=value1");
    response.sendRedirect("http://localhost/url?param2=value2&param1=value1");
    replay(response);

    LogoutFilter logoutFilter = new LogoutFilter("http://localhost/url", new
        LogoutHandler[] { logoutHandler });

    logoutFilter.setFilterProcessesUrl("/filterUrl");

    logoutFilter.doFilter(request, response, filterChain);
    verify(response);
  }

  /* Tests if we correctly redirect to an https location.
   */
  public void testSendRedirect_https() throws Exception {
    expect(request.getQueryString()).andReturn(
        "param2=value2&param1=value1").times(2);
    replay(request);

    expect(response.encodeRedirectURL(
          "https://localhost/url?param2=value2&param1=value1"))
      .andReturn("https://localhost/url?param2=value2&param1=value1");
    response.sendRedirect("https://localhost/url?param2=value2&param1=value1");
    replay(response);

    LogoutFilter logoutFilter = new LogoutFilter("https://localhost/url", new
        LogoutHandler[] { logoutHandler });

    logoutFilter.setFilterProcessesUrl("/filterUrl");

    logoutFilter.doFilter(request, response, filterChain);
    verify(response);
  }

  /* Tests if we correctly redirect to a location without parameters.
   */
  public void testSendRedirect_noParameters() throws Exception {
    expect(request.getQueryString()).andReturn(null);
    replay(request);

    expect(response.encodeRedirectURL("https://localhost/url"))
      .andReturn("https://localhost/url");
    response.sendRedirect("https://localhost/url");
    replay(response);

    LogoutFilter logoutFilter = new LogoutFilter("https://localhost/url", new
        LogoutHandler[] { logoutHandler });

    logoutFilter.setFilterProcessesUrl("/filterUrl");

    logoutFilter.doFilter(request, response, filterChain);
    verify(response);
  }

  /* Tests if the request parameters do no override url parameters.
   */
  public void testSendRedirect_ignoreRequestParametrs() throws Exception {
    expect(request.getQueryString()).andReturn(
        "param2=value2&param1=value1").anyTimes();
    replay(request);

    expect(response.encodeRedirectURL("https://localhost/url?p=v"))
      .andReturn("https://localhost/url?p=v");
    response.sendRedirect("https://localhost/url?p=v");
    replay(response);

    LogoutFilter logoutFilter = new LogoutFilter("https://localhost/url?p=v", new
        LogoutHandler[] { logoutHandler });

    logoutFilter.setFilterProcessesUrl("/filterUrl");
    logoutFilter.doFilter(request, response, filterChain);

    verify(request);
    verify(response);
  }

  /* Tests if the request parameters are passed along to the redirect url, if
   * the redirect url has no parameters.
   */
  public void testSendRedirect_passRequestParametrs() throws Exception {
    expect(request.getQueryString()).andReturn(
        "param2=value2&param1=value1").anyTimes();
    replay(request);

    expect(response.encodeRedirectURL(
          "https://localhost/url?param2=value2&param1=value1"))
      .andReturn("https://localhost/url?param2=value2&param1=value1");
    response.sendRedirect("https://localhost/url?param2=value2&param1=value1");
    replay(response);

    LogoutFilter logoutFilter = new LogoutFilter("https://localhost/url", new
        LogoutHandler[] { logoutHandler });

    logoutFilter.setFilterProcessesUrl("/filterUrl");
    logoutFilter.doFilter(request, response, filterChain);

    verify(request);
    verify(response);
  }
}

