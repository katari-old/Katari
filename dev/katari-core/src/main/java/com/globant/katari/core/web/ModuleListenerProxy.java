/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.List;
import java.util.LinkedList;
import java.util.EventListener;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextAttributeEvent;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionBindingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;

/** An event listener that delegates the event handling to a bean obtained from
 * the spring application context.
 *
 * It forwards all events to a spring configured listener defind under the bean
 * named, by default 'listenerDelegate' in the spring application context.
 *
 * The specific bean name is configured using the 'listenerBeanName' context
 * parameter.
 */
public final class ModuleListenerProxy implements ServletContextListener,
       ServletContextAttributeListener, HttpSessionActivationListener,
       HttpSessionAttributeListener, HttpSessionListener,
       HttpSessionBindingListener {

  /** The serialization version number.
   *
   * This number must change every time a new serialization incompatible change
   * is introduced in the class.
   */
  private static final long serialVersionUID = 1;

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      ModuleListenerProxy.class);

  /** The target filter that this filter delegates all requests.
   *
   * This is null until initialization.
   */
  private List<EventListener> delegates = new LinkedList<EventListener>();

  /** Builds a ModuleListenerProxy with no registered listeners.
   */
  public ModuleListenerProxy() {
  }

  /** Builds a ModuleListenerProxy with an initial list of registered
   * listeners.
   *
   * @param initialListeners The list of initial listeners. It cannot be null.
   */
  public ModuleListenerProxy(final List<EventListener> initialListeners) {
    Validate.notNull(initialListeners, "The list of initial listeners cannot be"
        + " null");
    delegates.addAll(initialListeners);
  }

  /** Adds a list of listeners to the chain.
   *
   * Each module can have a list of listeners that must receive web application
   * level events. This operation is inteded for modules to add a list of
   * listeners to the chain of module listeners.
   *
   * @param additionalListeners The list of listeners usually provided by a
   * module. It cannot be null.
   */
  public void addListeners(final List<EventListener> additionalListeners) {
    Validate.notNull(additionalListeners, "The listeners cannot be null");
    delegates.addAll(additionalListeners);
  }

  /*
   * ServletContextListener methods.
   */

  /** Notification that the web application is ready to process requests.
   *
   * It forwards the operation to the delegate if the delegate implements
   * ServletContextListener.
   *
   * @param event The servlet context event. It cannot be null.
   */
  public void contextInitialized(final ServletContextEvent event) {
    log.trace("Entering contextInitialized");

    for (EventListener delegate : delegates) {
      if (delegate instanceof ServletContextListener) {
        ((ServletContextListener) delegate).contextInitialized(event);
      }
    }
    log.trace("Leaving contextInitialized");
  }

  /** Notification that the servlet context is about to be shut down.
   *
   * It forwards the operation to the delegate if the delegate implements
   * ServletContextListener.
   *
   * @param event The servlet context event. It cannot be null.
   */
  public void contextDestroyed(final ServletContextEvent event) {
    log.trace("Entering contextDestroyed");
    for (EventListener delegate : delegates) {
      if (delegate instanceof ServletContextListener) {
        ((ServletContextListener) delegate).contextDestroyed(event);
      }
    }
    log.trace("Leaving contextDestroyed");
  }

  /*
   * ServletContextAttributeListener methods.
   */

  /** Notification that a new attribute was added to the servlet context.
   *
   * It forwards the operation to the delegate if the delegate implements
   * ServletContextAttributeListener..
   *
   * @param event The servlet context attribute event. It cannot be null.
   */
  public void attributeAdded(final ServletContextAttributeEvent event) {
    log.trace("Entering attributeAdded");
    for (EventListener delegate : delegates) {
      if (delegate instanceof ServletContextAttributeListener) {
        ((ServletContextAttributeListener) delegate).attributeAdded(event);
      }
    }
    log.trace("Leaving attributeAdded");
  }

  /** Notification that an existing attribute has been remved from the servlet
   * context.
   *
   * It forwards the operation to the delegate if the delegate implements
   * ServletContextAttributeListener..
   *
   * @param event The servlet context attribute event. It cannot be null.
   */
  public void attributeRemoved(final ServletContextAttributeEvent event) {
    log.trace("Entering attributeRemoved");
    for (EventListener delegate : delegates) {
      if (delegate instanceof ServletContextAttributeListener) {
        ((ServletContextAttributeListener) delegate).attributeRemoved(event);
      }
    }
    log.trace("Leaving attributeRemoved");
  }

  /** Notification that an attribute on the servlet context has been replaced.
   *
   * It forwards the operation to the delegate if the delegate implements
   * ServletContextAttributeListener..
   *
   * @param event The servlet context attribute event. It cannot be null.
   */
  public void attributeReplaced(final ServletContextAttributeEvent event) {
    log.trace("Entering attributeReplaced");
    for (EventListener delegate : delegates) {
      if (delegate instanceof ServletContextAttributeListener) {
        ((ServletContextAttributeListener) delegate).attributeReplaced(event);
      }
    }
    log.trace("Leaving attributeReplaced");
  }

  /*
   * HttpSessionActivationListener methods.
   */

  /** Notification that the session has just been activated.
   *
   * It forwards the operation to the delegate if the delegate implements
   * HttpSessionActivationListener.
   *
   * @param event The session event. It cannot be null.
   */
  public void sessionDidActivate(final HttpSessionEvent event) {
    log.trace("Entering sessionDidActivate");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionActivationListener) {
        ((HttpSessionActivationListener) delegate).sessionDidActivate(event);
      }
    }
    log.trace("Leaving sessionDidActivate");
  }

  /** Notification that the session is about to be passivated.
   *
   * It forwards the operation to the delegate if the delegate implements
   * ServletContextAttributeListener.
   *
   * @param event The session event. It cannot be null.
   */
  public void sessionWillPassivate(final HttpSessionEvent event) {
    log.trace("Entering sessionWillPassivate");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionActivationListener) {
        ((HttpSessionActivationListener) delegate).sessionWillPassivate(event);
      }
    }
    log.trace("Leaving sessionWillPassivate");
  }

  /*
   * HttpSessionAttributeListener methods.
   */

  /** Notification that a new attribute was added to a session.
   *
   * It forwards the operation to the delegate if the delegate implements
   * HttpSessionAttributeListener..
   *
   * @param event The session binding event. It cannot be null.
   */
  public void attributeAdded(final HttpSessionBindingEvent event) {
    log.trace("Entering attributeAdded");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionAttributeListener) {
        ((HttpSessionAttributeListener) delegate).attributeAdded(event);
      }
    }
    log.trace("Leaving attributeAdded");
  }

  /** Notification that an existing attribute has been remved from the servlet
   * context.
   *
   * It forwards the operation to the delegate if the delegate implements
   * HttpSessionAttributeListener..
   *
   * @param event The servlet context attribute event. It cannot be null.
   */
  public void attributeRemoved(final HttpSessionBindingEvent event) {
    log.trace("Entering attributeRemoved");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionAttributeListener) {
        ((HttpSessionAttributeListener) delegate).attributeRemoved(event);
      }
    }
    log.trace("Leaving attributeRemoved");
  }

  /** Notification that an attribute on the servlet context has been replaced.
   *
   * It forwards the operation to the delegate if the delegate implements
   * HttpSessionAttributeListener..
   *
   * @param event The servlet context attribute event. It cannot be null.
   */
  public void attributeReplaced(final HttpSessionBindingEvent event) {
    log.trace("Entering attributeReplaced");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionAttributeListener) {
        ((HttpSessionAttributeListener) delegate).attributeReplaced(event);
      }
    }
    log.trace("Leaving attributeReplaced");
  }

  /*
   * HttpSessionListener methods.
   */

  /** Notification that a session was created.
   *
   * It forwards the operation to the delegate if the delegate implements
   * HttpSessionListener..
   *
   * @param event The session event. It cannot be null.
   */
  public void sessionCreated(final HttpSessionEvent event) {
    log.trace("Entering sessionCreated");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionListener) {
        ((HttpSessionListener) delegate).sessionCreated(event);
      }
    }
    log.trace("Leaving sessionCreated");
  }

  /** Notification that a session was invalidated.
   *
   * It forwards the operation to the delegate if the delegate implements
   * HttpSessionListener..
   *
   * @param event The session event. It cannot be null.
   */
  public void sessionDestroyed(final HttpSessionEvent event) {
    log.trace("Entering sessionDestroyed");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionListener) {
        ((HttpSessionListener) delegate).sessionDestroyed(event);
      }
    }
    log.trace("Leaving sessionDestroyed");
  }

  /*
   * HttpSessionBindingListener methods.
   */

  /** Notifies the object that it is being bound to a session and identifies
   * the session.
   *
   * @param event the session binding event. It cannot be null.
   */
  public void valueBound(final HttpSessionBindingEvent event) {
    log.trace("Entering valueBound");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionBindingListener) {
        ((HttpSessionBindingListener) delegate).valueBound(event);
      }
    }
    log.trace("Leaving valueBound");
  }

  /** Notifies the object that it is being unbound from a session and
   * identifies the session.
   *
   * @param event the session binding event. It cannot be null.
   */
  public void valueUnbound(final HttpSessionBindingEvent event) {
    log.trace("Entering valueUnbound");
    for (EventListener delegate : delegates) {
      if (delegate instanceof HttpSessionBindingListener) {
        ((HttpSessionBindingListener) delegate).valueUnbound(event);
      }
    }
    log.trace("Leaving valueUnbound");
  }
}

