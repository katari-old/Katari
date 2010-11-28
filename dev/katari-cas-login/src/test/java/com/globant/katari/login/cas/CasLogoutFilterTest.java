/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.easymock.classextension.EasyMock;

public class CasLogoutFilterTest {

  @Test
  public void testDoFilter_logout() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = EasyMock.createMock(FilterChain.class);

    CasTicketRegistry casTicketRegistry = new CasTicketRegistry();
    ServicesUrlBuilder builder;
    builder = new ServicesUrlBuilder("http://casurl", "service");

    request.setRequestURI("http://blah/cas/logout");
    request.setContextPath("cas");
    request.setParameter("service", "serviceUrl");

    CasLogoutFilter filter = new CasLogoutFilter(casTicketRegistry, builder);
    filter.doFilter(request, response, chain);
    
    assertThat(response.getRedirectedUrl(),
        is("http://casurl/logout?service=serviceUrl"));
  }

  @Test
  public void testDoFilter() throws IOException, ServletException {

    //  mock creation
    CasTicketRegistry casTicketRegistry = EasyMock
        .createMock(CasTicketRegistry.class);
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    ServletResponse response = EasyMock.createMock(HttpServletResponse.class);
    FilterChain chain = EasyMock.createMock(FilterChain.class);
    HttpSession session = EasyMock.createMock(HttpSession.class);

    ServicesUrlBuilder builder;
    builder = new ServicesUrlBuilder("http://casurl", "service");
    CasLogoutFilter filter = new CasLogoutFilter(casTicketRegistry, builder);

    // mock recording
    EasyMock.expect(casTicketRegistry.getSession("ticket1")).andReturn(session);

    EasyMock.expect(request.getRequestURI()).andReturn(
        "http://myrequest.com/context/j_acegi_cas_security_check").anyTimes();
    EasyMock.expect(request.getContextPath()).andReturn( "/context").anyTimes();

    EasyMock.expect(request.getMethod()).andReturn("POST");
    EasyMock
        .expect(request.getParameter("logoutRequest"))
        .andReturn(
            "<samlp:LogoutRequest ID=\"111\" Version=\"2.0\""
           + " IssueInstant=\"dateandtime\">"
           + " <saml:NameID>@NOT_USED@</saml:NameID>"
           + " <samlp:SessionIndex>ticket1</samlp:SessionIndex>"
           + "</samlp:LogoutRequest>");

    session.invalidate();

    //mock activation
    EasyMock.replay(request);
    EasyMock.replay(session);
    EasyMock.replay(chain);
    EasyMock.replay(response);
    EasyMock.replay(casTicketRegistry);

    // invoke the method to test
    filter.doFilter(request, response, chain);

    //mock verification
    EasyMock.verify(request);
    EasyMock.verify(session);
    EasyMock.verify(response);
    EasyMock.verify(chain);
    EasyMock.verify(casTicketRegistry);
  }
}

