/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.File;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.servlet.ServletContext;

/**  Utility class to give support to test cases.
 *
 * This class will give access to spring beans and help you with database
 * related tests.
 */
public abstract class SpringTestUtilsBase {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(SpringTestUtilsBase.class);

  /** The spring configuration files for the global application context.
   */
  private String[] globalConfigurationFiles;

  /** The spring configuration files for the spring dispatcher servlet
   * application context.
   */
  private String[] servletConfigurationFiles;

  /** a data source.
   */
  private DataSource dataSource = null;

  /** Bean factory.
   */
  private ApplicationContext beanFactory;

  /** Company spring servlet Bean factory.
   */
  private ApplicationContext servletBeanFactory;

  /** The katari transaction manager for the application context.
   */
  private PlatformTransactionManager transactionManager = null;

  /** The current transaction status.
   */
  private TransactionStatus transactionStatus = null;

  /** Constructor.
   *
   * @param theGlobalConfigurationFiles the list of configuration files to
   * create the global application context. It cannot be null.
   *
   * @param theServletConfigurationFiles the list of configuration files to
   * create the spring dispatcher servlet application context. It cannot be
   * null.
   */
  protected SpringTestUtilsBase(final String[] theGlobalConfigurationFiles,
      final String[] theServletConfigurationFiles) {
    globalConfigurationFiles = theGlobalConfigurationFiles;
    servletConfigurationFiles = theServletConfigurationFiles;
  }

  /** Gets the configured data source.
   *
   * @return a DataSource.
   */
  private synchronized DataSource getDataSource() {
    if (dataSource == null) {
      beanFactory = getBeanFactory();
      dataSource = (DataSource) beanFactory.getBean("dataSource");
    }
    return dataSource;
  }

  /** Gets the connection to the database, enlisted to the current transaction
   * if any.
   *
   * @return a Connection.
   *
   * @exception SQLException if a database access error occurs
   */
  public synchronized Connection getConnection() throws SQLException {
    if (dataSource == null) {
      beanFactory = getBeanFactory();
      dataSource = (DataSource) beanFactory.getBean("dataSource");
    }
    Connection connection = DataSourceUtils.getConnection(dataSource);
    return connection;
  }

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory
   */
  public synchronized ApplicationContext getBeanFactory() {
    log.trace("Entering getBeanFactory");
    if (beanFactory == null) {
      log.debug("Creating the global beanFactory");
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setConfigLocations(globalConfigurationFiles);
      if (new File("./src/main/webapp").exists()) {
	// We assume that the module references a web application if there is a
	// webapp directory.
        ServletContext sc = new MockServletContext("./src/main/webapp",
            new FileSystemResourceLoader());
        appContext.setServletContext(sc);
      }
      appContext.refresh();
      beanFactory = appContext;
    }
    log.trace("Leaving getBeanFactory");
    return beanFactory;
  }

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory
   */
  public synchronized ApplicationContext getServletBeanFactory() {
    if (servletBeanFactory == null) {
      log.info("Creating the servlet beanFactory");
      servletBeanFactory = new FileSystemXmlApplicationContext(
          servletConfigurationFiles, getBeanFactory());
    }
    return servletBeanFactory;
  }

  /** Obtains a bean from the global bean factory.
   *
   * @param beanName the name of the bean to search for in the bean factory. It
   * cannot be null.
   *
   * @return the bean named beanName, or null if not found.
   */
  public Object getBean(final String beanName) {
    return getBeanFactory().getBean(beanName);
  }

  /** Obtains a bean from the spring dispatcher servlet bean factory or its
   * parent.
   *
   * @param beanName the name of the bean to search for in the bean factory. It
   * cannot be null.
   *
   * @return Retrieve the bean named beanName, or null if not found.
   */
  public Object getServletBean(final String beanName) {
    return getServletBeanFactory().getBean(beanName);
  }

  /** Obtains the transactionManager from the application context.
   *
   * @return a transaction manager.
   */
  private synchronized PlatformTransactionManager
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
  public synchronized void beginTransaction() {
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
  public synchronized void endTransaction() {
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

  /** Sets the files to use to load the global spring application context.
   *
   * @param fileNames the list of files.
   */
  protected final void setGlobalConfigurationFiles(
      final String[] fileNames) {
    globalConfigurationFiles = fileNames;
  }
}

