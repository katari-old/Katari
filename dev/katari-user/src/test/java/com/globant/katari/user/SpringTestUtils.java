/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.support.XmlWebApplicationContext;

/** Utility class to give support to test cases.
 */
public final class SpringTestUtils {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(SpringTestUtils.class);

  /** a data source.
   */
  private static DataSource dataSource = null;

  /** Bean factory.
   */
  private static ApplicationContext beanFactory;

  /** User module Bean factory.
   */
  private static ApplicationContext servletBeanFactory;

  /** The katari transaction manager for the application context.
   */
  private static PlatformTransactionManager transactionManager;

  /** The current transaction status.
   */
  private static TransactionStatus transactionStatus;

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
  private static synchronized ApplicationContext getBeanFactory() {
    if (beanFactory == null) {
      log.info("Creating a beanFactory");
      ServletContext sc = new MockServletContext("./src/main/webapp",
          new FileSystemResourceLoader());
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setServletContext(sc);
      appContext.setConfigLocations(new String[] {
        "classpath:/com/globant/katari/user/applicationContext.xml"});
      appContext.refresh();
      beanFactory = appContext;
    }
    return beanFactory;
  }

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory
   */
  private static synchronized ApplicationContext getServletBeanFactory() {
    if (servletBeanFactory == null) {
      log.info("Creating a beanFactory");
      servletBeanFactory = new FileSystemXmlApplicationContext(
          new String[]
          {"classpath:/com/globant/katari/user/view/spring-servlet.xml"},
          getBeanFactory());
    }
    return servletBeanFactory;
  }

  /** Obtains a bean from the application bean factory.
   *
   * @param beanName the name of the bean to search for in the bean factory.
   * It cannot be null.
   *
   * @return the bean named beanName, or null if not found.
   */
  public static Object getBean(final String beanName) {
    return getBeanFactory().getBean(beanName);
  }

  /** Obtains a bean from the spring dispatcher servlet bean factory or its
   *  parent.
   *
   * @param beanName the name of the bean to search for in the bean factory.
   * It cannot be null.
   *
   * @return the bean named beanName, or null if not found.
   */
  public static Object getServletBean(final String beanName) {
    return getServletBeanFactory().getBean(beanName);
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
   * This is used to guarantee that each unit tests use a single hibernate
   * session. Only one transaction can be active at any given time. Calling
   * this operation implicitely commits the pending transaction, if any.
   *
   * This is intended to be called in a @Before operation.
   */
  public static synchronized void beginTransaction() {
    endTransaction();
    transactionStatus = getTransactionManager().getTransaction(
        new DefaultTransactionDefinition());
  }

  /** Commits the pending transaction, if any.
   */
  public static synchronized void endTransaction() {
    if (transactionStatus != null && !transactionStatus.isCompleted()) {
      if (transactionStatus.isRollbackOnly()) {
        transactionManager.rollback(transactionStatus);
      } else {
        transactionManager.commit(transactionStatus);
      }
    }
    transactionStatus = null;
  }
}

