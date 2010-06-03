/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.tools.DatabaseTestSupport;

/** Utility class to clean tables from the data base.
 *
 * This class contains methods to drop tables from the database. The
 * implementation is tied to Oracle.
 *
 * @author rcunci
 */
public class OracleDropAllObjects extends DatabaseTestSupport {

  /** The class logger. */
  private final Log log = LogFactory.getLog(OracleDropAllObjects.class);

  /** ALL_TABLES_QUERY. */
  private static final String ALL_TABLES_QUERY =
    "select TABLE_NAME from user_tables";

  /** ALL_SEQ_QUERY. */
  private static final String ALL_SEQ_QUERY =
    "select SEQUENCE_NAME from USER_SEQUENCES";

  /** ALL_VIEWS_QUERY. */
  private static final String ALL_VIEWS_QUERY =
    "select VIEW_NAME from USER_VIEWS";

  /** How to cascade the drops.
   */
  private static final String CASCADE = "cascade constraints";

  /** {@inheritDoc}
   */
  @Override
  protected void doDropAll(final Connection connection,
      final String markerTable) throws SQLException {
    dropAllTables(markerTable, connection);
    dropAllSequences(markerTable, connection);
    dropAllViews(markerTable, connection);
  }

  /** Drops all tables.
   *
   * @param markerName Table marker name
   *
   * @param conn Connection
   *
   * @throws SQLException exception
   */
  protected void dropAllTables(final String markerName, final Connection conn)
    throws SQLException {
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery(ALL_TABLES_QUERY);

    List<String> tableNames = new ArrayList<String>();
    while (rs.next()) {
      tableNames.add(rs.getString(1));
    }
    rs.close();
    for (String table : tableNames) {
      if (!table.equalsIgnoreCase(markerName)) {
        log.debug("Dropping table " + table);
        st.executeUpdate("DROP TABLE " + table + " " + CASCADE);
      }
    }
  }

  /** Drops all sequences.
   *
   * @param markerName Table marker name
   * @param conn Connection
   * @throws SQLException exception
   */
  protected void dropAllSequences(final String markerName, final Connection
      conn) throws SQLException {
    /* Drops all sequences. */
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery(ALL_SEQ_QUERY);
    rs = st.executeQuery(ALL_SEQ_QUERY);
    List<String> sequenceNames = new ArrayList<String>();
    while (rs.next()) {
      sequenceNames.add(rs.getString(1));
    }
    rs.close();
    for (String sequence : sequenceNames) {
      log.debug("Dropping sequence " + sequence);
      st.executeUpdate("DROP SEQUENCE " + sequence);
    }
  }

  /** Drops all views.
   *
   * @param markerName Table marker name
   * @param conn Connection
   * @throws SQLException exception
   */
  protected void dropAllViews(final String markerName, final Connection conn)
    throws SQLException {
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery(ALL_VIEWS_QUERY);
    List<String> viewNames = new ArrayList<String>();
    while (rs.next()) {
      viewNames.add(rs.getString(1));
    }
    rs.close();
    for (String view : viewNames) {
      log.debug("Dropping view " + view);
      st.executeUpdate("DROP VIEW " + view);
    }
  }
}

