#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.web.testsupport.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to clean tables from the data base.
 *
 * This class contains methods to drop tables from the database. The
 * implementation is tied to PostgreSQL.
 *
 * Warning: this class is provided with no automated testing because katari
 * uses mysql. We need to add a new sub-project with support for postgres to
 * test this.
 *
 * @author nicolas.frontini
 */
public class PostgreSqlDropAllObjects extends DatabaseTestSupport {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(PostgreSqlDropAllObjects.class);

  /**
   * string to see all the tables.
   */
  private static String listTables = "select table_name"
      + " from information_schema.tables where table_schema='public'"
      + " and table_type = 'BASE TABLE'";

  /**
   * string to see all the sequences.
   */
  private static String listSequences = "select relname from pg_class"
      + " where relkind = 'S'";

  /**
   * Drop all tables from the database.
   *
   * @param connection
   *          The connection to execute sql sentences. It cannot be null.
   *
   * @param markerTable
   *          The marker table name. Is the marker of the db of permision.
   *
   * @throws SQLException
   *           if a database access error occurs.
   */
  protected void dropTables(final Connection connection,
      final String markerTable) throws SQLException {
    Validate.notNull(connection, "The connection cannot be null.");

    // Drops all tables.
    log.debug("Dropping all tables");
    Statement st = connection.createStatement();
    ResultSet rs = null;
    rs = st.executeQuery(listTables);
    List<String> tableNames = new ArrayList<String>();
    while (rs.next()) {
      tableNames.add(rs.getString(1));
    }
    rs.close();
    for (String table : tableNames) {
      if (!table.equalsIgnoreCase(markerTable)) {
        log.debug("Dropping table " + table);
        st.executeUpdate("DROP TABLE " + table + " cascade");
      }
    }
  }

  /**
   * Drop all sequences from the database.
   *
   * @param connection
   *          The connection to execute sql sentences. It cannot be null.
   *
   * @throws SQLException
   *           if a database access error occurs.
   */
  protected void dropSequences(final Connection connection)
      throws SQLException {
    Validate.notNull(connection, "The connection cannot be null.");

    // Drops all sequences.
    log.debug("Dropping all sequences");
    Statement st = connection.createStatement();
    ResultSet rs = null;
    rs = st.executeQuery(listSequences);
    List<String> sequenceNames = new ArrayList<String>();
    while (rs.next()) {
      sequenceNames.add(rs.getString(1));
    }
    rs.close();
    for (String seq : sequenceNames) {
      log.debug("Dropping sequence " + seq);
      st.executeUpdate("DROP SEQUENCE " + seq);
    }
  }

  /** {@inheritDoc}
   */
  @Override
  protected void doDropAll(final Connection connection, final String
      markerTable) throws Exception {
    dropTables(connection, markerTable);
    dropSequences(connection);
  }
}

