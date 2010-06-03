/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.classextension.EasyMock.*;

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

public class SpringBootstrapListenerTest extends TestCase {

  /* Creates a servlet context with a spring application context that has the
   * listener object as a bean named beanName.
   */
  public ServletContext createServletContext(final Object listener) {

    // Mocks the Web Application Context
    WebApplicationContext wac = createMock(WebApplicationContext.class);
    expect(wac.getBean("katari.moduleListenerProxy")).andReturn(listener);
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

  /* Calles contextInitialized on the listener to make the listener aware of
   * the delegate configured in the spring bean context.
   */
  private void initBootstrapListener(final SpringBootstrapListener listener,
      final ServletContext context) {

    ServletContextEvent event = createMock(ServletContextEvent.class);
    expect(event.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    replay(event);

    listener.contextInitialized(event);
  }

  /* Tests if the listener correctly forwards the request to
   * ServletContextListener implementations.
   */
  public void testContextListener() {

    ServletContextListener delegate = createMock(ServletContextListener.class);

    ServletContext context = createServletContext(delegate);
    ServletContextEvent event = createMock(ServletContextEvent.class);
    expect(event.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    replay(event);

    delegate.contextInitialized(event);
    delegate.contextDestroyed(event);
    replay(delegate);

    SpringBootstrapListener listener = new SpringBootstrapListener();

    ((ServletContextListener) listener).contextInitialized(event);
    listener.contextDestroyed(event);

    // Checks if the bootstrap called contextInitialized and contextDestroyed
    // on the delegate.
    verify(delegate);
  }

  /* Tests if the listener correctly forwards the request to
   * ServletContextAttributeListener implementations.
   */
  public void testContextAttributeListener() {

    ServletContextAttributeListener delegate;
    delegate = createMock(ServletContextAttributeListener.class);

    ServletContext context = createServletContext(delegate);
    ServletContextAttributeEvent event;
    event = createMock(ServletContextAttributeEvent.class);
    replay(event);

    delegate.attributeAdded(event);
    delegate.attributeRemoved(event);
    delegate.attributeReplaced(event);
    replay(delegate);

    SpringBootstrapListener listener = new SpringBootstrapListener();
    initBootstrapListener(listener, context);

    ((ServletContextAttributeListener) listener).attributeAdded(event);
    listener.attributeRemoved(event);
    listener.attributeReplaced(event);

    // Checks if the bootstrap called contextInitialized on the delegate.
    verify(delegate);
  }

  /* Tests if the listener correctly forwards the request to
   * HttpSessionActivationListener implementations.
   */
  public void testHttpSessionActivationListener() {

    HttpSessionActivationListener delegate;
    delegate = createMock(HttpSessionActivationListener.class);

    ServletContext context = createServletContext(delegate);
    HttpSessionEvent event = createMock(HttpSessionEvent.class);
    replay(event);

    delegate.sessionDidActivate(event);
    delegate.sessionWillPassivate(event);
    replay(delegate);

    SpringBootstrapListener listener = new SpringBootstrapListener();
    initBootstrapListener(listener, context);

    ((HttpSessionActivationListener) listener).sessionDidActivate(event);
    listener.sessionWillPassivate(event);

    // Checks if the bootstrap called contextInitialized on the delegate.
    verify(delegate);
  }

  /* Tests if the listener correctly forwards the request to
   * HttpSessionAttributeListener implementations.
   */
  public void testHttpSessionAttributeListener() {

    HttpSessionAttributeListener delegate;
    delegate = createMock(HttpSessionAttributeListener.class);

    ServletContext context = createServletContext(delegate);
    HttpSessionBindingEvent event;
    event = createMock(HttpSessionBindingEvent.class);
    replay(event);

    delegate.attributeAdded(event);
    delegate.attributeRemoved(event);
    delegate.attributeReplaced(event);
    replay(delegate);

    SpringBootstrapListener listener = new SpringBootstrapListener();
    initBootstrapListener(listener, context);

    ((HttpSessionAttributeListener) listener).attributeAdded(event);
    listener.attributeRemoved(event);
    listener.attributeReplaced(event);

    // Checks if the bootstrap called contextInitialized on the delegate.
    verify(delegate);
  }

  /* Tests if the listener correctly forwards the request to
   * HttpSessionListener implementations.
   */
  public void testHttpSessionListener() {

    HttpSessionListener delegate = createMock(HttpSessionListener.class);

    ServletContext context = createServletContext(delegate);
    HttpSessionEvent event = createMock(HttpSessionEvent.class);
    replay(event);

    delegate.sessionCreated(event);
    delegate.sessionDestroyed(event);
    replay(delegate);

    SpringBootstrapListener listener = new SpringBootstrapListener();
    initBootstrapListener(listener, context);

    ((HttpSessionListener) listener).sessionCreated(event);
    listener.sessionDestroyed(event);

    // Checks if the bootstrap called contextInitialized on the delegate.
    verify(delegate);
  }

  /* Tests if the listener correctly forwards the request to
   * HttpSessionBindingListener implementations.
   */
  public void testHttpSessionBindingListener() {

    HttpSessionBindingListener delegate;
    delegate = createMock(HttpSessionBindingListener.class);

    ServletContext context = createServletContext(delegate);
    HttpSessionBindingEvent event;
    event = createMock(HttpSessionBindingEvent.class);
    replay(event);

    delegate.valueBound(event);
    delegate.valueUnbound(event);
    replay(delegate);

    SpringBootstrapListener listener = new SpringBootstrapListener();
    initBootstrapListener(listener, context);

    ((HttpSessionBindingListener) listener).valueBound(event);
    listener.valueUnbound(event);

    // Checks if the bootstrap called contextInitialized on the delegate.
    verify(delegate);
  }
}

