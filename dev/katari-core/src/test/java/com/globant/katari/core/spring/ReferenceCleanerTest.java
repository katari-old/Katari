/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

public class ReferenceCleanerTest extends TestCase {

  /** True if the driver was registered in the driver manager before running
   * the tests.
   */
  private boolean driverWasRegistered = false;

  /** A sample in memory jdbc url.
   */
  String url = "jdbc:hsqldb:mem:aname";

  /** Tests if the cleaner removes the references to the jdbc driver from the
   * driver manager.
   */
  public void testCleanUp() throws Exception {

    // This registers the driver with the driver manager.
    Class.forName("org.hsqldb.jdbcDriver" );

    // Must not fail.
    Connection connection = DriverManager.getConnection(url, "sa", "");
    connection.close();

    ReferenceCleaner cleaner = new ReferenceCleaner();
    cleaner.contextDestroyed(null);

    try {
      connection = DriverManager.getConnection(url, "sa", "");
      fail("The driver was not deregistered from the driver manager.");
    } catch (SQLException e) {
      // OK. Driver not registared, exception ignored.
    }
  }

  public void setUp() {
    // Checks if the driver was loaded prior to running the test.
    driverWasRegistered = false;
    try {
      Connection connection = DriverManager.getConnection(url, "sa", "");
      connection.close();
      driverWasRegistered = true;
    } catch (Exception e) {
      // Ignored ..
    }
  }

  public void tearDown() throws Exception {
    // This registers the driver again with the driver manager.
    if (driverWasRegistered) {
      Class.forName("org.hsqldb.jdbcDriver" );
    }
  }
}

