package com.globant.katari.cas;

import javax.servlet.http.HttpSession;

import org.easymock.classextension.EasyMock;

import com.globant.katari.cas.CasTicketRegistry;

import junit.framework.TestCase;

public class CasTicketRegistryTest extends TestCase {

  public void testRegistry() {
    HttpSession session1 = EasyMock.createMock(HttpSession.class);
    HttpSession session2 = EasyMock.createMock(HttpSession.class);
    EasyMock.expect(session1.getId()).andReturn("s1").times(5);
    EasyMock.expect(session2.getId()).andReturn("s2").times(2);
    EasyMock.replay(session1);
    EasyMock.replay(session2);
    String ticket1 = "ticket1";
    String ticket2 = "ticket2";
    CasTicketRegistry reg = new CasTicketRegistry();
    reg.registerTicket(ticket1, session1);
    reg.registerTicket(ticket2, session2);
    assertEquals(reg.getSession(ticket1), session1);
    assertEquals(reg.getSession(ticket2), session2);
    assertNull(reg.getSession("ticket3"));
    reg.removeSession(session1);
    assertNull(reg.getSession("ticket1"));
    EasyMock.verify(session1);
    EasyMock.verify(session2);
  }


}
