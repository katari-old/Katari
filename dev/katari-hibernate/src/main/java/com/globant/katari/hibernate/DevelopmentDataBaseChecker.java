/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Checks if the application is using a development database.
 *
 * A development database has a marker table. The presence of this marker table
 * is an indication that the data can be lost at any time.
 *
 * @author gerardo.bercovich
 */
public class DevelopmentDataBaseChecker {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(DevelopmentDataBaseChecker.class);

  /**
   * The data source.
   *
   * It is never null.
   */
  private final DataSource dataSource;

  /** The marker table name.
   *
   * It is never null.
   */
  private final String markerTableName;

/**
 * The DevelopmentDataBaseChecker Constructor.
 *
 * @param theDataSource it cannot be null.
 * @param theMarkerTableName it cannot be null.
 */
  public DevelopmentDataBaseChecker(final DataSource theDataSource,
      final String theMarkerTableName) {
    Validate.notNull(theMarkerTableName,
        "The marker table name cannnot be null.");
    Validate.notNull(theDataSource, "The DataSource cannnot be null.");
    dataSource = theDataSource;
    markerTableName = theMarkerTableName;
  }

  /** Checks if this is a development database.
  *
  * This method hits the database and does not cache the result, so beware of
  * how often this method is called.
  *
  * @return Returns true if it is a development database;
  */
 public boolean checkForDevelopmentDatabase() {
   log.trace("Entering checkForDevelopmentDatabase");
   Connection connection = null;
   ResultSet rs = null;
   Statement query = null;
   boolean isDevelopmentDatabase = true;
   try {
     connection = dataSource.getConnection();
     query = connection.createStatement();
     rs = query.executeQuery("select * from " + markerTableName);
     String message = null;
     if (rs.next()) {
       message = rs.getString("drop_database");
     }
     if (message == null) {
       isDevelopmentDatabase = false;
     } else if (!message.equals("YES, DROP ME")) {
       isDevelopmentDatabase = false;
     }
   } catch (SQLException e) {
     // Every exception is considered that this is not a development database.
     isDevelopmentDatabase = false;
   } finally {
     if (rs != null) {
       try {
         rs.close();
       } catch (SQLException e) {
         log.error("Error closing connection", e);
       }
     }
     if (query != null) {
       try {
         query.close();
       } catch (SQLException e) {
         log.error("Error closing connection", e);
       }
     }
     if (connection != null) {
       try {
         connection.close();
       } catch (SQLException e) {
         log.error("Error closing connection", e);
       }
     }
   }
   log.trace("Leaving checkForDevelopmentDatabase");
   return isDevelopmentDatabase;
 }

}
