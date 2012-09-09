/* vim: set ts=2 et sw=2 cindent fo=qroca: */
package com.globant.katari.hibernate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class DevelopmentDataBaseCheckerTest {

  private DataSource dataSource;
  private Connection connection = null;

  /** Creates administrator role.
   */
  @Before
  public void setUp() throws Exception {
    dataSource = (DataSource) SpringTestUtils.get().getBean("dataSource");
    connection = SpringTestUtils.get().getConnection();
    Statement statement = connection.createStatement();
    try {
      statement.execute("drop table sample_marker");
    } catch (SQLException e) {
      // Ignored.
    }
  }

  @After
  public void tearDown() throws Exception {
    if (connection != null) {
      connection.close();
    }
  }

  @Test
  public void testCheckForDevelopmentDatabase_noMarker() {
    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource , "sample_marker");
    assertThat(checker.checkForDevelopmentDatabase(), is(false));
  }

  @Test
  public void testCheckForDevelopmentDatabase_emptyMarker() throws Exception {
    Statement statement = connection.createStatement();
    statement.execute("create table sample_marker(drop_database varchar(50))");
    statement.close();

    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource, "sample_marker");
    assertThat(checker.checkForDevelopmentDatabase(), is(false));
  }

  @Test
  public void testCheckForDevelopmentDatabase_column() throws Exception {
    Statement statement = connection.createStatement();
    statement.execute("create table sample_marker(something varchar(50))");
    statement.close();

    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource, "sample_marker");
    assertThat(checker.checkForDevelopmentDatabase(), is(false));
  }

  @Test
  public void testCheckForDevelopmentDatabase_true() throws Exception {
    Statement statement = connection.createStatement();
    statement.execute("create table sample_marker(drop_database varchar(50))");
    statement.execute("insert into sample_marker values ('YES, DROP ME')");
    statement.close();

    DevelopmentDataBaseChecker checker;
    checker = new DevelopmentDataBaseChecker(dataSource, "sample_marker");
    assertThat(checker.checkForDevelopmentDatabase(), is(true));
  }
}

