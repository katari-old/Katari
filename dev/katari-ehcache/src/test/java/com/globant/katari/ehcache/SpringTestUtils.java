/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.ehcache;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.springframework.web.context.support.XmlWebApplicationContext;

/** Utility class to give support to test cases.
 *
 * @author nicolas.frontini
 */
public class SpringTestUtils {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(SpringTestUtils.class);

  /** The data source obtained from the application context.
   *
   * This is a singleton.
   */
  private static DataSource dataSource = null;

  /** Bean factory.
   */
  private static ApplicationContext beanFactory;

  /** The katari transaction manager for the application context.
   */
  private static PlatformTransactionManager transactionManager = null;

  /** The current transaction status.
   */
  private static TransactionStatus transactionStatus = null;

  /** A private constructor so no instances are created.
   */
  private SpringTestUtils() {
  }

  /** Gets the configured data source.
   *
   * @return a DataSource.
   */
  public static synchronized DataSource getDataSource() {
    if (dataSource == null) {
      beanFactory = getBeanFactory();
      dataSource = (DataSource) beanFactory.getBean("dataSource");
    }
    return dataSource;
  }

  /** Gets the connection to the database.
   *
   * @return a Connection.
   *
   * @exception SQLException if a database access error occurs
   */
  public static synchronized Connection getConnection() throws SQLException {
    if (dataSource == null) {
      beanFactory = getBeanFactory();
      dataSource = (DataSource) beanFactory.getBean("dataSource");
    }
    Connection connection = dataSource.getConnection();
    return connection;
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
        "classpath:/com/globant/katari/ehcache/hibernate/applicationContext.xml",
        "classpath:/com/globant/katari/ehcache/view/spring-servlet.xml"
      });
      appContext.refresh();
      beanFactory = appContext;
    }
    return beanFactory;
  }

  /** Obtains the transactionManager from the application context.
   *
   * @return a transaction manager.
   */
  private static synchronized PlatformTransactionManager
      getTransactionManager() {
    if (transactionManager == null) {
      log.info("Creating a transactionManager");
      transactionManager = (PlatformTransactionManager)
          getBeanFactory().getBean("katari.transactionManager");
    }
    return transactionManager;
  }

  /** Begins a global transaction.
   *
   * This is used to guarantee that each unit test use a single hibernate
   * session. Only one transaction can be active at any given time. Calling
   * this operation implicitely commits the pending transaction, if any.
   *
   * This is usually called in a @Before method.
   */
  public static synchronized void beginTransaction() {
    log.trace("Entering beginTransaction");
    endTransaction();
    transactionStatus = getTransactionManager().getTransaction(
        new DefaultTransactionDefinition());
    log.trace("Leaving beginTransaction");
  }

  /** Commits the pending transaction (opened with beginTransaction), if any.
   *
   * This is usually be called in a @After method.
   */
  public static synchronized void endTransaction() {
    log.trace("Entering endTransaction");
    if (transactionStatus != null && !transactionStatus.isCompleted()) {
      if (transactionStatus.isRollbackOnly()) {
        log.debug("Rollbacking transaction");
        transactionManager.rollback(transactionStatus);
      } else {
        log.debug("Committing transaction");
        transactionManager.commit(transactionStatus);
      }
    }
    transactionStatus = null;
    log.trace("Leaving endTransaction");
  }

  /** Destroys the current bean factory.*/
  public static void destroy() {
    CacheManager.getInstance().shutdown();
    beanFactory = null;
    transactionManager = null;
    transactionStatus = null;
    dataSource = null;
  }

  /** Retrieves the given bean by name.
   *
   * @param beanName the name of the bean to retrieve.
   * @return the bean.
   */
  public static Object getBean(final String beanName) {
    return getBeanFactory().getBean(beanName);
  }
}

