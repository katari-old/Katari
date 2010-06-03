/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/** Utility class to give support to test cases.
 */
public final class SpringTestUtils {

  /** A logger.
   */
  private static Log log = LogFactory.getLog(SpringTestUtils.class);

  /** Bean factory, a singleton.
   */
  private static ApplicationContext beanFactory = null;

  /** Module Bean fectory, a singleton.
   */
  private static ApplicationContext moduleBeanFactory = null;

  /** A private constructor so no instances are created.
   */
  private SpringTestUtils() {
  }

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory initialized from applicationContext.xml. Never
   * returns null.
   */
  public static synchronized ApplicationContext getBeanFactory() {
    if (beanFactory == null) {
      log.info("Creating a beanFactory");
      ServletContext sc = new MockServletContext("./src/main/webapp",
          new FileSystemResourceLoader());
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setServletContext(sc);
      appContext.setConfigLocations(new String[]
          {"classpath:/applicationContext.xml"});
      appContext.refresh();
      beanFactory = appContext;
    }
    return beanFactory;
  }

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory initialized from the spring servlet. Never returns
   * null.
   */
  public static synchronized ApplicationContext getModuleBeanFactory() {
    if (moduleBeanFactory == null) {
      log.info("Creating a beanFactory");
      moduleBeanFactory = new FileSystemXmlApplicationContext(
          new String[]
          {"classpath:/com/globant/katari/search/view/spring-servlet.xml"},
          getBeanFactory());
    }
    return moduleBeanFactory;
  }
}

