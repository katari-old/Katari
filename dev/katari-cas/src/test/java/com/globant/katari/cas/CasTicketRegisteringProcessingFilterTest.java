package com.globant.katari.cas;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.easymock.classextension.EasyMock;

import com.globant.katari.cas.CasTicketRegisteringProcessingFilter;
import com.globant.katari.cas.CasTicketRegistry;

public class CasTicketRegisteringProcessingFilterTest extends TestCase {

  public void testTicketRegistration() throws IOException {

    // mock creation
    CasTicketRegistry casTicketRegistry = EasyMock
        .createMock(CasTicketRegistry.class);
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpServletResponse response = EasyMock
        .createMock(HttpServletResponse.class);
    HttpSession session = EasyMock.createMock(HttpSession.class);
    Authentication auth = EasyMock.createMock(Authentication.class);

    CasTicketRegisteringProcessingFilter filter;
    filter = new CasTicketRegisteringProcessingFilter();
    filter.setCasTicketRegistry(casTicketRegistry);

    // mock recording
    EasyMock.expect(auth.getCredentials()).andReturn("ticket1");
    EasyMock.expect(request.getSession()).andReturn(session);

    //expectations
    casTicketRegistry.registerTicket("ticket1", session);

    // mock activation
    EasyMock.replay(request);
    EasyMock.replay(session);
    EasyMock.replay(response);
    EasyMock.replay(casTicketRegistry);
    EasyMock.replay(auth);

    // invoke the method to test
    filter.onSuccessfulAuthentication(request, response, auth);

    // mock verification
    EasyMock.verify(request);
    EasyMock.verify(session);
    EasyMock.verify(response);
    EasyMock.verify(casTicketRegistry);
    EasyMock.verify(auth);
  }
}

