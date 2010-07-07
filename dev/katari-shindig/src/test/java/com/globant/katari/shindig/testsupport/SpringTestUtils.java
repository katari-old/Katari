/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.testsupport;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/** Utility class to give support to test cases.
 */
public final class SpringTestUtils {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(SpringTestUtils.class);

  /** a data source.
   */
  // private static DataSource dataSource = null;

  /** The bean factory for the test application context.
   */
  private static ApplicationContext beanFactory;

  /** A private constructor so no instances are created.
   */
  private SpringTestUtils() {
  }

  /** Gets the configured data source.
   *
   * @return a DataSource.
   */
  /*
  public static synchronized DataSource getDataSource() {
    if (dataSource == null) {
      beanFactory = getBeanFactory();
      dataSource = (DataSource) beanFactory.getBean("dataSource");
    }
    return dataSource;
  }
  */

  /** Gets the connection to the database.
   *
   * @return a Connection.
   *
   * @exception SQLException if a database access error occurs
   */
  /*
  public static synchronized Connection getConnection() throws SQLException {
    if (dataSource == null) {
      beanFactory = getBeanFactory();
      dataSource = (DataSource) beanFactory.getBean("dataSource");
    }
    Connection connection = dataSource.getConnection();
    return connection;
  }
  */

  /** Returns the singleton bean factory for the test application context.
   *
   * @return a BeanFactory, never null.
   */
  public static synchronized ApplicationContext getBeanFactory() {
    log.trace("Entering getBeanFactory");
    if (beanFactory == null) {
      log.debug("Creating the bean factory");
      ServletContext sc = new MockServletContext("./src/main/webapp",
          new FileSystemResourceLoader());
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setServletContext(sc);
      appContext.setConfigLocations(new String[] {
        "classpath:/com/globant/katari/shindig/applicationContext.xml",
      });
      appContext.refresh();
      beanFactory = appContext;
    }
    return beanFactory;
  }
}

