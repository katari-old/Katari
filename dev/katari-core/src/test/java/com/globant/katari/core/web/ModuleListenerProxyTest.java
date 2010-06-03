/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.classextension.EasyMock.*;

import java.util.List;
import java.util.LinkedList;
import java.util.EventListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextAttributeEvent;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionBindingEvent;

import junit.framework.TestCase;

import org.springframework.web.context.WebApplicationContext;

public class ModuleListenerProxyTest extends TestCase {

  /* Creates a servlet context with a spring application context that has the
   * listener object as a bean named beanName.
   */
  public ServletContext createServletContext(final Object listener) {

    // Mocks the Web Application Context
    WebApplicationContext wac = createMock(WebApplicationContext.class);
    expect(wac.getBean("listenerDelegate")).andReturn(listener);
    expectLastCall().anyTimes();
    replay(wac);

    // Mocks the servlet context.
    ServletContext servletContext = createNiceMock(ServletContext.class);
    expect(servletContext.getAttribute(
          WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)
        ).andReturn(wac);
    expectLastCall().anyTimes();
    // Under some conditions, the init method asks context to log the call.
    servletContext.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(servletContext);

    return servletContext;
  }

  /* This method tests if the ModuleListenerProxy correctly forwards the events
   * to the target listeners. It is implemented in one method for convenience.
   */
  public void testDispatching() {

    // Context listener related mocks.

    // ServletContextListener
    ServletContextEvent contextEvent = createMock(ServletContextEvent.class);
    replay(contextEvent);

    ServletContextListener context = createMock(ServletContextListener.class);
    context.contextInitialized(contextEvent);
    context.contextDestroyed(contextEvent);
    replay(context);

    // ServletContextAttributeListener
    ServletContextAttributeEvent contextAttributeEvent;
    contextAttributeEvent = createMock(ServletContextAttributeEvent.class);
    replay(contextAttributeEvent);

    ServletContextAttributeListener contextAttribute;
    contextAttribute = createMock(ServletContextAttributeListener.class);
    contextAttribute.attributeAdded(contextAttributeEvent);
    contextAttribute.attributeRemoved(contextAttributeEvent);
    contextAttribute.attributeReplaced(contextAttributeEvent);
    replay(contextAttribute);

    // HttpSessionActivationListener
    HttpSessionEvent sessionEvent = createMock(HttpSessionEvent.class);
    replay(sessionEvent);

    HttpSessionActivationListener sessionActivation;
    sessionActivation = createMock(HttpSessionActivationListener.class);
    sessionActivation.sessionDidActivate(sessionEvent);
    sessionActivation.sessionWillPassivate(sessionEvent);
    replay(sessionActivation);

    // HttpSessionAttributeListener
    HttpSessionBindingEvent sessionBindingEvent;
    sessionBindingEvent = createMock(HttpSessionBindingEvent.class);
    replay(sessionBindingEvent);

    HttpSessionAttributeListener sessionAttribute;
    sessionAttribute = createMock(HttpSessionAttributeListener.class);
    sessionAttribute.attributeAdded(sessionBindingEvent);
    sessionAttribute.attributeRemoved(sessionBindingEvent);
    sessionAttribute.attributeReplaced(sessionBindingEvent);
    replay(sessionAttribute);

    // HttpSessionListener
    HttpSessionListener session = createMock(HttpSessionListener.class);
    session.sessionCreated(sessionEvent);
    session.sessionDestroyed(sessionEvent);
    replay(session);

    // HttpSessionBindingListener
    HttpSessionBindingListener sessionBinding;
    sessionBinding = createMock(HttpSessionBindingListener.class);
    sessionBinding.valueBound(sessionBindingEvent);
    sessionBinding.valueUnbound(sessionBindingEvent);
    replay(sessionBinding);

    List<EventListener> initial = new LinkedList<EventListener>();
    initial.add(context);
    ModuleListenerProxy proxy = new ModuleListenerProxy(initial);
    List<EventListener> additional = new LinkedList<EventListener>();
    additional.add(contextAttribute);
    additional.add(sessionActivation);
    additional.add(sessionAttribute);
    additional.add(session);
    additional.add(sessionBinding);
    proxy.addListeners(additional);

    proxy.contextInitialized(contextEvent);
    proxy.contextDestroyed(contextEvent);
    proxy.attributeAdded(contextAttributeEvent);
    proxy.attributeRemoved(contextAttributeEvent);
    proxy.attributeReplaced(contextAttributeEvent);
    proxy.sessionDidActivate(sessionEvent);
    proxy.sessionWillPassivate(sessionEvent);
    proxy.attributeAdded(sessionBindingEvent);
    proxy.attributeRemoved(sessionBindingEvent);
    proxy.attributeReplaced(sessionBindingEvent);
    proxy.sessionCreated(sessionEvent);
    proxy.sessionDestroyed(sessionEvent);
    proxy.valueBound(sessionBindingEvent);
    proxy.valueUnbound(sessionBindingEvent);

    verify(context);
    verify(contextAttribute);
    verify(sessionActivation);
    verify(sessionAttribute);
    verify(session);
    verify(sessionBinding);
  }
}

