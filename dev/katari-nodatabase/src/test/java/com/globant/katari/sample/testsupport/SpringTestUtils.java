/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.testsupport;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/** Utility class to give support to test cases.
 *
 * @author nicolas.frontini
 */
public final class SpringTestUtils {

  /** A logger.
   */
  private static Log log = LogFactory.getLog(SpringTestUtils.class);

  /** Bean factory.
   */
  private static ApplicationContext beanFactory;

  /** A private constructor so no instances are created.
   */
  private SpringTestUtils() {
  }

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory
   */
  public static synchronized ApplicationContext getBeanFactory() {
    if (beanFactory == null) {
      log.info("Creating a beanFactory");
      ServletContext sc = new MockServletContext("./src/main/webapp",
          new FileSystemResourceLoader());
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setServletContext(sc);
      appContext.setConfigLocations(new String[] {
        "/WEB-INF/applicationContext.xml",
        "/WEB-INF/applicationContextRuntime.xml" });
      appContext.refresh();
      beanFactory = appContext;
    }
    return beanFactory;
  }
}

