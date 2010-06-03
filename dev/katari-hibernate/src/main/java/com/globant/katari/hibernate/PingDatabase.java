/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.core.ping.PingService;
import com.globant.katari.core.ping.PingResult;

/**
 * This class checks if the database responds to some predefined query.
 *
 * @author demian.calcaprina
 */
public class PingDatabase implements PingService {

  /**
   * Text indicating that the database is woking.
   */
  private static final String STATUS_OK = "Database query: SUCCESS\n";

  /**
   * Text indicating that the database failed.
   */
  private static final String STATUS_FAIL = "Database query: FAIL\n";

  /**
   * The class logger.
   */
  private static Log log = LogFactory.getLog(PingDatabase.class);

  /**
   * The data source.
   *
   * It is never null.
   */
  private final DataSource dataSource;

  /** The query to be done in the database.
   *
   * It is never null.
   */
  private final String databaseQuery;

  /** The database checker.
   *
   * It is never null.
   */
  private final DevelopmentDataBaseChecker devDataBaseChecker;

  /** Constuctor method.
   *
   * @param theDataSource the DataSource. It cannot be null.
   *
   * @param theDatabaseQuery the query to be done in the database. It cannot be
   * null.
   *
   * @param checker the development database checker. It cannot be null.
   */
  public PingDatabase(final DataSource theDataSource,
      final String theDatabaseQuery, final DevelopmentDataBaseChecker checker) {
    Validate.notNull(checker, "the DevelopmentDataBaseChecker cannot be"
        + " null");
    Validate.notNull(theDataSource, "the dataSource cannot be null");
    Validate.notNull(theDatabaseQuery, "the database query cannot be null");
    devDataBaseChecker = checker;
    dataSource = theDataSource;
    databaseQuery = theDatabaseQuery;
  }

  /**
   * This method checks if the database responds to some predefined query.
   *
   * @return the status of the database.
   */
  public PingResult ping() {
    log.trace("Entering ping");

    Connection connection = null;
    String message = "";
    boolean status = false;
    try {
      connection = dataSource.getConnection();
      status = testDatabase(connection);
      if (status) {
        message += STATUS_OK;
      } else {
        message += STATUS_FAIL;
      }
      if (devDataBaseChecker.checkForDevelopmentDatabase()) {
        message += "WARNING: this is a development database\n";
      }
    } catch (SQLException e) {
      message += STATUS_FAIL;
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          log.error("Error closing connection", e);
        }
      }
    }
    return new PingResult(status, message);
  }

  /** Checks if the database is accessible running a query.
   *
   * @param connection The database connection. It cannot be null.
   *
   * @return Returns a string with the result.
   */
  private boolean testDatabase(final Connection connection) {
    Validate.notNull(connection, "The connection cannot be null");
    Statement query = null;
    boolean status = true;
    try {
      query = connection.createStatement();
      query.executeQuery(databaseQuery);
      log.trace("Leaving ping with status OK");
      status = true;
    } catch (SQLException e) {
      log.error("Leaving ping with fail status", e);
      status = false;
    } finally {
      if (query != null) {
        try {
          query.close();
        } catch (SQLException e) {
          log.error("Error closing statement", e);
        }
      }
    }
    return status;
  }

}

