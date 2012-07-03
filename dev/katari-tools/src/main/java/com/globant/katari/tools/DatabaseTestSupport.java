/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;

import com.globant.katari.tools.database.MySqlDropAllObjects;
import com.globant.katari.tools.database.PostgreSqlDropAllObjects;
import com.globant.katari.tools.database.OracleDropAllObjects;

/** DatabaseTestSupport abstract class for classes that can drop objects and
 * run sql sentences.
 */
public abstract class DatabaseTestSupport {

  /** A logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(DatabaseTestSupport.class);

  /** The data base drop objects Map.
   */
  private static Map<String, DatabaseTestSupport> dbDropObjectsMap
    = new HashMap<String, DatabaseTestSupport>();

  /** Registers a handler for an additional dialect.
   *
   * @param dialect The hibernate dialect. It cannot be null.
   *
   * @param support The DatabaseTestSupport that will handle the dialect. It
   * cannot be null.
   */
  public static void registerDialect(final String dialect, final
      DatabaseTestSupport support) {
    dbDropObjectsMap.put(dialect, support);
  }

  static {
    initialize();
  }

  /** Registers the classes responsible for effectively dropping the database
   * objects.
   */
  private static void initialize() {
    registerDialect("org.hibernate.dialect.MySQLDialect",
        new MySqlDropAllObjects());
    registerDialect("org.hibernate.dialect.MySQL5InnoDBDialect",
        new MySqlDropAllObjects());
    registerDialect("org.hibernate.dialect.PostgreSQLDialect",
        new PostgreSqlDropAllObjects());
    registerDialect("org.hibernate.dialect.Oracle8iDialect",
        new OracleDropAllObjects());
    registerDialect("org.hibernate.dialect.Oracle9Dialect",
        new OracleDropAllObjects());
    registerDialect("org.hibernate.dialect.Oracle9iDialect",
        new OracleDropAllObjects());
    registerDialect("org.hibernate.dialect.Oracle10gDialect",
        new OracleDropAllObjects());
  }

  /** Returns the corresponding DatabaseTestSupport instance.
   *
   * The returned instance depends on the database used.
   *
   * @param session The session factory. It cannot be null.
   *
   * @return The correspinding DatabaseTestSupport.
   */
  public static DatabaseTestSupport create(
      final LocalSessionFactoryBean session) {
    Validate.notNull(session, "The session factory cannot be null.");
    String dialect = (String) session.getConfiguration()
        .getProperties().get("hibernate.dialect");
    DatabaseTestSupport databaseTestSupport = dbDropObjectsMap.get(dialect);
    Validate.notNull(databaseTestSupport, "Dialect " + dialect
        + " not supported.");
    return databaseTestSupport;
  }

  /** Drops all tables from the data base.
   *
   * Checks if the marker table exists and contains 'YES DROP ME'. If it does,
   * it drops all objects in the database, excluding the marker table.
   *
   * @param connection The database connection to use. It cannot be null.
   *
   * @param markerTable The marker table name. This table must exist and have
   * one column named drop_database with 'YES DROP ME'.
   */
  public void dropAll(final Connection connection, final String markerTable) {
    try {
      assertDevelopmentDatabase(connection, markerTable);
      doDropAll(connection, markerTable);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Deletes all rows from all tables in the data base.
   *
   * Checks if the marker table exists and contains 'YES DROP ME'. If it does,
   * it deletes all rows in the database, excluding the marker table.
   *
   * @param connection The database connection to use. It cannot be null.
   *
   * @param markerTable The marker table name. This table must exist and have
   * one column named drop_database with 'YES DROP ME'.
   */
  public void deleteAll(final Connection connection,
      final String markerTable) {
    try {
      assertDevelopmentDatabase(connection, markerTable);
      doDeleteAll(connection, markerTable);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Initializes the auto increment columns to a predefined value.
   *
   * @param connection The database connection to use. It cannot be null.
   * 
   * @param initialValue the initial value to use for autoincrement.
   *
   * @throws Exception in case of error.
   */
  public void initializeAutoincrement(final Connection connection,
      final int initialValue) throws Exception {
    try {
      doInitializeAutoincrement(connection, initialValue);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Template method to drop all tables from the data base.
   *
   * @param connection The database connectino to use to drop all the objects.
   * It cannot be null.
   *
   * @param markerTable The name of the marker table. It cannot be null.
   *
   * @throws Exception in case of error.
   */
  protected abstract void doDropAll(final Connection connection, final String
      markerTable) throws Exception;

  /** Template method to delete all rows from all tables in the data base.
   *
   * @param connection The database connectino to use to drop all the objects.
   * It cannot be null.
   *
   * @param markerTable The name of the marker table. It cannot be null.
   *
   * @throws Exception in case of error.
   */
  protected abstract void doDeleteAll(final Connection connection, final String
      markerTable) throws Exception;

  /** Template method to initialize the auto increment columns to a predefined
   * value.
   *
   * @param connection The database connectino to use to drop all the objects.
   * It cannot be null.
   *
   * @param initialValue the initial value to use for autoincrement.
   *
   * @throws Exception in case of error.
   */
  protected abstract void doInitializeAutoincrement(
      final Connection connection, final int initialValue) throws Exception;

  /** Runs a set of sql sentences stored in a file.
   *
   * Sentences in the file are delimited by a line ending in ;.
   *
   * @param connection the database connection to use to run the sql sentences.
   * It cannot be null.
   *
   * @param fileName the String with the file name.
   */
  public void runSqlSentences(final Connection connection, final String
      fileName) {
    log.trace("Entering runSqlSentences('" + fileName + "')");
    Validate.notNull(fileName, "The file name cannot be null.");

    String sentence = null;
    try {
      Statement statement = connection.createStatement();
      SqlSentencesParser parser = new SqlSentencesParser(fileName);

      while (null != (sentence = parser.readSentence())) {
        statement.execute(StringEscapeUtils.unescapeJava(sentence));
      }

    } catch (Exception e) {
      System.out.println("Error executing " + sentence);
      e.printStackTrace();
      System.exit(1);
    }
    log.trace("Leaving runSqlSentences");
  }

  /** Verifies if it is a development database, that is, if it has the mark
   * table.
   *
   * It throws an exception if it is not a development database.
   *
   * @param connection The connection to execute sql sentences. It cannot be
   * null.
   *
   * @param markerTable The marker table name. Is the marker of the db of
   * permision.
   *
   * @throws SQLException if a database access error occurs.
   */
  private void assertDevelopmentDatabase(final Connection connection,
      final String markerTable) throws SQLException {
    Validate.notNull(connection, "The connection cannot be null.");
    Statement st = connection.createStatement();

    /* Check if we are running against a scratch database.
     */
    log.debug("Verifying if it is a test database");
    ResultSet rs = null;
    try {
      rs = st.executeQuery("select drop_database from " + markerTable);
    } catch (SQLException e) {
      // An exeption. Give some explanation just in case.
      log.info("An exception was caught selecting from {}"
          + ". It is probable because the table does not exist. "
          + "Please create it with:", markerTable);
      log.info("create table {} (drop_database varchar (50));",  markerTable);
      log.info("insert into {} values ('YES, DROP ME');", markerTable);

      System.out.println("An exception was caught selecting from "
          + markerTable + ". It is probable because the table does"
          + " not exist. Please create it with:");
      System.out.println("create table " + markerTable
          + "(drop_database varchar (50));");
      System.out.println("insert into " + markerTable
          + " values ('YES, DROP ME');");
      throw e;
    }
    String message = null;
    if (rs.next()) {
      message = rs.getString("drop_database");
    }
    if (message == null) {
      throw new RuntimeException("Marker table does not contain a row");
    }
    if (!message.equals("YES, DROP ME")) {
      throw new RuntimeException("Marker table does not contain the"
          + " correct row");
    }
  }

  /** Checks if the database schema matches the hibernate session factory.
   *
   * @param sessionFactory the hibernate session factory. It cannot be null.
   *
   * @param connection the database connection. It cannot be null.
   *
   * @return returns true if the database schema matches the session factory,
   * false otherwise.
   */
  public boolean isUpToDate(final LocalSessionFactoryBean sessionFactory,
      final Connection connection) {

    Configuration configuration = sessionFactory.getConfiguration();

    Dialect dialect = Dialect.getDialect(configuration.getProperties());

    DatabaseMetadata databaseMetadata = null;
    try {
      databaseMetadata = new DatabaseMetadata(connection, dialect, false);
    } catch (Exception e) {
      System.out.println("Error obtaining database metadata");
      e.printStackTrace();
      System.exit(1);
    }

    try {
      configuration.validateSchema(dialect, databaseMetadata);
      return true;
    } catch (HibernateException e) {
      return false;
    }
  }
}

