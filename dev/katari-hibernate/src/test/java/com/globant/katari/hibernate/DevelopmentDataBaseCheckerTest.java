/* vim: set ts=2 et sw=2 cindent fo=qroca: */
package com.globant.katari.hibernate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public class DevelopmentDataBaseCheckerTest extends
    AbstractTransactionalDataSourceSpringContextTests {

  /* Injected by spring.
  */
  private DataSource dataSource = null;

  Connection connection = null;

  /* Disables rollback.
   */
  public DevelopmentDataBaseCheckerTest() {
    setDefaultRollback(false);
  }

  /** Configures application context xml file.
   */
  @Override
  protected String[] getConfigLocations() {
    return new String[] {
        "classpath:com/globant/katari/hibernate/role/applicationContext.xml" };
  }

  /** Creates administrator role.
   */
  @Override
  protected void onSetUp() throws Exception {
    connection = dataSource.getConnection();
    Statement statement = connection.createStatement();
    try {
      statement.execute("drop table sample_marker");
    } catch (SQLException e) {
      // Ignored.
    }
  }

  /** Deletes all the roles.
   */
  @Override
  protected void onTearDown() throws Exception {
  //  this.deleteFromTables(new String[]{"roles"});
    if (connection != null) {
      connection.close();
    }
  }

  public void testCheckForDevelopmentDatabase_noMarker() {
    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource, "sample_marker");
    assertTrue(!checker.checkForDevelopmentDatabase());
  }

  public void testCheckForDevelopmentDatabase_emptyMarker() throws Exception {
    Statement statement = connection.createStatement();
    statement.execute("create table sample_marker(drop_database varchar(50))");
    statement.close();

    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource, "sample_marker");
    assertTrue(!checker.checkForDevelopmentDatabase());
  }

  public void testCheckForDevelopmentDatabase_column() throws Exception {
    Statement statement = connection.createStatement();
    statement.execute("create table sample_marker(something varchar(50))");
    statement.close();

    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource, "sample_marker");
    assertTrue(!checker.checkForDevelopmentDatabase());
  }

  public void testCheckForDevelopmentDatabase_true() throws Exception {
    Statement statement = connection.createStatement();
    statement.execute("create table sample_marker(drop_database varchar(50))");
    statement.execute("insert into sample_marker values ('YES, DROP ME')");
    statement.close();

    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource, "sample_marker");
    assertTrue(checker.checkForDevelopmentDatabase());
  }

  public DataSource getDataSaurce() {
    return dataSource;
  }

  public void setDataSaurce(final DataSource theDataSource) {
    dataSource = theDataSource;
  }
}

