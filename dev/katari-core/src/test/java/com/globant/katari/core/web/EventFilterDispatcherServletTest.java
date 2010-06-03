/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.context.ApplicationContext;

public class EventFilterDispatcherServletTest {

  private boolean called = false;

  @SuppressWarnings("serial")
  @Test
  public void testOnApplicationEvent() throws Exception {

    StaticWebApplicationContext parent = new StaticWebApplicationContext();

    MockServletConfig config = new MockServletConfig();
    config.addInitParameter("contextConfigLocation",
        "classpath:/com/globant/katari/core/userApplicationContext.xml");

    EventFilterDispatcherServlet servlet = new EventFilterDispatcherServlet() {
      protected void onRefresh(final ApplicationContext context) {
        called = true;
      }
    };
    servlet.init(config);

    ContextRefreshedEvent event;
    event = new ContextRefreshedEvent(parent);
    called = false;
    servlet.onApplicationEvent(event);
    assertThat(called, is(false));

    event = new ContextRefreshedEvent(servlet.getWebApplicationContext());
    called = false;
    servlet.onApplicationEvent(event);
    assertThat(called, is(true));
  }
}

