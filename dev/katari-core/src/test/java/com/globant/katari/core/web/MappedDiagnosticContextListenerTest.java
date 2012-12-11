package com.globant.katari.core.web;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;

import javax.servlet.ServletRequestEvent;

import org.apache.log4j.MDC;
import org.junit.Test;

/**
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class MappedDiagnosticContextListenerTest {

  @Test public void requestInitialized() {
    ServletRequestEvent event = createMock(ServletRequestEvent.class);
    replay(event);
    MappedDiagnosticContextListener listener;
    listener = new MappedDiagnosticContextListener();
    MDC.put("hello", "hello");
    assertThat((String) MDC.get("hello"), is("hello"));
    listener.requestInitialized(event);
    assertThat(MDC.get("hello"), nullValue());
  }

}
