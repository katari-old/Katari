/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/** Utility class to give support to test cases.
 */
public final class SpringTestUtils {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(SpringTestUtils.class);

  /** The bean factory for the test application context.
   */
  private static ApplicationContext beanFactory;

  /** The view bean factory for the test application context.
   */
  private static ApplicationContext viewBeanFactory;

  /** A private constructor so no instances are created.
   */
  private SpringTestUtils() {
  }

  /** Obtains a bean from the bean factory.
   *
   * @param name Bean name, never null.
   *
   * @return The bean requested, never returns null.
   */
  public static Object getBean(final String name) {
    return getBeanFactory().getBean(name);
  }

  /** Obtains a bean from the view bean factory (the spring-servlet.xml).
   *
   * @param name Bean name, never null.
   *
   * @return The bean requested, never returns null.
   */
  public static Object getViewBean(final String name) {
    return getViewBeanFactory().getBean(name);
  }

  /** Returns the singleton bean factory for the test application context.
   *
   * @return a BeanFactory, never null.
   */
  private static synchronized ApplicationContext getBeanFactory() {
    log.trace("Entering getBeanFactory");
    if (beanFactory == null) {
      log.debug("Creating the bean factory");
      ServletContext sc = new MockServletContext("./src/main/webapp",
          new FileSystemResourceLoader());
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setServletContext(sc);
      appContext.setConfigLocations(new String[] {
        "classpath:/applicationContext.xml",
      });
      appContext.refresh();
      beanFactory = appContext;
    }
    log.trace("Leaving getBeanFactory");
    return beanFactory;
  }

  /** Returns the singleton bean factory for the view application context.
   *
   * @return a BeanFactory, never null.
   */
  private static synchronized ApplicationContext getViewBeanFactory() {
    log.trace("Entering getViewBeanFactory");
    if (viewBeanFactory == null) {
      log.debug("Creating the bean factory");
      ServletContext sc = new MockServletContext("./src/main/webapp",
          new FileSystemResourceLoader());
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setServletContext(sc);
      appContext.setConfigLocations(new String[] {
        "classpath:/com/globant/katari/login/cas/view/spring-servlet.xml",
      });
      appContext.setParent(getBeanFactory());
      appContext.refresh();
      viewBeanFactory = appContext;
    }
    log.trace("Leaving getViewBeanFactory");
    return viewBeanFactory;
  }
}

