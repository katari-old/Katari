/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A simple bean that performs memory clean up of the application when the
 * spring context shuts down.
 *
 * This class is intended to be used as a spring bean. Without this, java
 * leaves some references to classes in the web context class loader when the
 * web application context shuts down. This makes it impossible to hot-redeploy
 * the web application multiple times, eventualy leading to an out of perm gen
 * memory.
 *
 * There are various causes for this.
 *
 * The current implementation:
 *
 * Frees the jdbc drivers registered in the driver manager.
 *
 * @author pablo.saavedra
 */
public class ReferenceCleaner implements ServletContextListener {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(ReferenceCleaner.class);

  /** This method does nothing.
   *
   * {@inheritDoc}
   */
  public void contextInitialized(final ServletContextEvent event) {
    // noting to do.
  }

  /** Performs the memory clean up when the context destroyed event is
   * received.
   *
   * Basic clean up done by this method is to de-register the JDBC drivers from
   * the {@link DriverManager}.
   *
   * @param event This is the event class for notifications about changes to
   * the servlet context of a web application. This parameter is ignored.
   *
   * {@inheritDoc}
   */
  public void contextDestroyed(final ServletContextEvent event) {

    log.trace("Entering contextDestroyed");

    deregisterJdbcDrivers();
    // shutDownEvictorThreads();

    log.trace("Leaving contextDestroyed");
  }

  /** Deregisters the jdbc drivers from the driver manager.
   *
   * This is necessary so that the garbage collector can free the war class
   * loader when the war is redeployed. The driver manager in the system class
   * loader holds a reference to the jdbc driver in the war class loader.
   */
  private void deregisterJdbcDrivers() {
    log.trace("Entering deregisterJdbcDrivers");
    ClassLoader myClassLoader = getClass().getClassLoader();
    if (DriverManager.class.getClassLoader() == myClassLoader) {
      // The driver manager and this class share the same classloader. There is
      // no need to deregister the driver from the driver manager.
      log.trace("Leaving deregisterJdbcDrivers");
      return;
    }
    try {
      Enumeration<Driver> e = DriverManager.getDrivers();
      while (e.hasMoreElements()) {
        Driver driver = e.nextElement();
        if (driver.getClass().getClassLoader() == myClassLoader) {
          if (log.isDebugEnabled()) {
            log.debug("Deregistering jdbc driver " + driver.getClass());
          }
          DriverManager.deregisterDriver(driver);
        }
      }
    } catch (SQLException e) {
      log.error("Error deregistering jdbc driver", e);
      throw new RuntimeException("Unable to deregister drivers", e);
    }
    log.trace("Leaving deregisterJdbcDrivers");
  }
}

