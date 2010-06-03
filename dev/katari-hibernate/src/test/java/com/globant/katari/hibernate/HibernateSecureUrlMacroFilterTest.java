/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

import com.globant.katari.hibernate.DevelopmentDataBaseChecker;

import com.globant.katari.core.security.SecureUrlAccessHelper;

/** Test case for {@link HibernateSecureUrlMacroFilter}.
 */
public class HibernateSecureUrlMacroFilterTest extends TestCase {

  private SecureUrlAccessHelper helper;
  private final HttpServletResponse response = null;
  private HttpSession session;
  private HttpServletRequest request;
  private DevelopmentDataBaseChecker checker;
  private FilterChain chain;

  @Override
  protected void setUp() throws Exception {
    helper = EasyMock.createMock(SecureUrlAccessHelper.class);
    EasyMock.replay(helper);

    checker = EasyMock.createMock(DevelopmentDataBaseChecker.class);
    EasyMock.expect(checker.checkForDevelopmentDatabase()).andReturn(true)
        .anyTimes();
    EasyMock.replay(checker);
  }

  /* Tests if securityDebug is added to the session if the request includes a
   * securityDebug attribute in true.
   */
  public void testDoFilter_securityDebug_enable() throws Exception{
    HibernateSecureUrlMacroFilter filter;
    filter = new HibernateSecureUrlMacroFilter(helper, checker);

    session = EasyMock.createMock(HttpSession.class);
    session.setAttribute("securityDebug", "true");
    EasyMock.replay(session);

    initRequestMock("true");

    filter.doFilter(request, response, chain);
  }

  /* Checks that the securityDebug request parameter is ignored if we are not
   * working with a development database.
   */
  public void testDoFilter_securityDebug_NotDev_enable() throws Exception {
    checker = EasyMock.createMock(DevelopmentDataBaseChecker.class);
    EasyMock.expect(checker.checkForDevelopmentDatabase()).andReturn(false)
        .anyTimes();
    EasyMock.replay(checker);

    HibernateSecureUrlMacroFilter filter;
    filter = new HibernateSecureUrlMacroFilter(helper, checker);

    session = EasyMock.createMock(HttpSession.class);
    EasyMock.replay(session);

    request = EasyMock.createMock(HttpServletRequest.class);
    request.setAttribute("secureUrlHelper", helper);
    EasyMock.expect(request.getParameter("securityDebug")).andReturn("true");
    EasyMock.replay(request);

    // init chain
    chain = EasyMock.createMock(FilterChain.class);
    chain.doFilter(request, response);
    EasyMock.replay(chain);

    filter.doFilter(request, response, chain);
  }

  /* Checks if the securityDebug session attribute is correctly removed from
   * the session if the request includes a securityDebug attribute in false.
   */
  public void testDoFilter_securityDebug_disable() throws Exception{
    final HibernateSecureUrlMacroFilter filter;
    filter = new HibernateSecureUrlMacroFilter(helper, checker);

    session = EasyMock.createMock(HttpSession.class);
    session.setAttribute("securityDebug", "true");
    session.removeAttribute("securityDebug");
    EasyMock.replay(session);

    initRequestMock("true");
    filter.doFilter(request, response, chain);
    EasyMock.verify(request);

    initRequestMock("false");
    filter.doFilter(request, response, chain);
  }

  /* Checks if the securityDebug session attribute is correctly removed from
   * the session if the request includes a securityDebug attribute with a
   * non-recognized value.
   */
  public void testDoFilter_securityDebug_anyValue_disable() throws Exception{
    HibernateSecureUrlMacroFilter filter;
    filter = new HibernateSecureUrlMacroFilter(helper, checker);

    session = EasyMock.createMock(HttpSession.class);
    session.setAttribute("securityDebug", "true");
    session.removeAttribute("securityDebug");
    EasyMock.replay(session);

    initRequestMock("true");

    filter.doFilter(request, response, chain);
    EasyMock.verify(request);

    initRequestMock("unknow value");

    filter.doFilter(request, response, chain);
  }

  /**
   * Initialize request field.
   * @param securityDebugValue the param value in the request.
   */
  private void initRequestMock(final String securityDebugValue)
      throws Exception {
    request = EasyMock.createMock(HttpServletRequest.class);
    EasyMock.expect(request.getSession()).andReturn(session);
    request.setAttribute("secureUrlHelper", helper);
    EasyMock.expect(request.getParameter("securityDebug")).andReturn(
        securityDebugValue);
    EasyMock.replay(request);

    // init chain
    chain = EasyMock.createMock(FilterChain.class);
    chain.doFilter(request, response);
    EasyMock.expectLastCall().anyTimes();
    EasyMock.replay(chain);
  }

  @Override
  protected void tearDown() throws Exception {
    EasyMock.verify(request);
    EasyMock.verify(session);
  }
}

