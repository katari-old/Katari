/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig;

import java.util.LinkedList;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Test;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceInitializerListenerTest {

  private ServletContextEvent event = null;

  private ServletContext context = null;

  private ApplicationContext applicationContext
      = new GenericApplicationContext();

  @Before
  public void setUp() throws Exception {
    context = createMock(ServletContext.class);
    expect(context.getAttribute(
        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
        .andReturn(applicationContext);
    expect(context.getAttribute("guice-injector")).andReturn(null);
    context.setAttribute(eq("guice-injector"), isA(Injector.class));
    replay(context);

    event = createMock(ServletContextEvent.class);
    expect(event.getServletContext()).andReturn(context);
    replay(event);
  }

  @Test
  public void testContextInitialized() throws Exception {
    GuiceInitializerListener listener;
    listener = new GuiceInitializerListener(new LinkedList<Module>());
    listener.contextInitialized(event);
    verify(event);
  }

  @Test(expected = RuntimeException.class)
  public void testContextInitialized_attributeExists() throws Exception {
    reset(context);
    expect(context.getAttribute("guice-injector")).andReturn("something");
    replay(context);

    GuiceInitializerListener listener;
    listener = new GuiceInitializerListener(new LinkedList<Module>());
    listener.contextInitialized(event);
  }

  @Test
  public void testContextDestroyed() throws Exception {
    reset(context);
    context.removeAttribute("guice-injector");
    replay(context);

    GuiceInitializerListener listener;
    listener = new GuiceInitializerListener(new LinkedList<Module>());
    listener.contextDestroyed(event);
    verify(event);
  }
}

