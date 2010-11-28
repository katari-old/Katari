package com.globant.katari.login.cas;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.*;

public class CasSessionListenerTest extends TestCase {

  public void testSessionDestroyed() {
    HttpSession session = createMock(HttpSession.class);
    HttpSessionEvent event = createMock(HttpSessionEvent.class);
    CasTicketRegistry casTicketRegistry = createMock(CasTicketRegistry.class);

    expect(event.getSession()).andReturn(session);

    casTicketRegistry.removeSession(session);

    replay(session);
    replay(event);
    replay(casTicketRegistry);

    CasSessionListener listener = new CasSessionListener(casTicketRegistry);
    listener.sessionDestroyed(event);

    verify(session);
    verify(casTicketRegistry);
    verify(event);
  }
}

