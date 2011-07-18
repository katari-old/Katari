/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.testsupport.database;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import com.globant.katari.sample.testsupport.SpringTestUtils;

import com.globant.katari.tools.DatabaseTestSupport;

/** This class contains methods to initialize a cleaned database.
 */
public class TestDbSupport extends TestCase {

  /** A logger.
   */
  private static Logger log = LoggerFactory.getLogger(TestDbSupport.class);

  /** This test marker, is the marker of the db of permision.
   */
  private static String testMarker = "katari_marker_666";

  /** Initialize this test case.
   */
  public final void setUp() {
    org.apache.log4j.PropertyConfigurator.configure(
        "src/test/resources/log4j.properties");
  }

  /** Recreates the database, dropping all objects and running the
   * corresponding ddl and sql scripts.
   */
  public final void testDoSetup() throws Exception {
    log.trace("Entering testDoSetup");

    log.info("Cleaning database");
    LocalSessionFactoryBean sessionFactory = (LocalSessionFactoryBean)
        SpringTestUtils.getBeanFactory().getBean("&katari.sessionFactory");
    DatabaseTestSupport databaseTestSupport;
    databaseTestSupport = DatabaseTestSupport.create(sessionFactory);
    databaseTestSupport.dropAll(SpringTestUtils.getDataSource(), testMarker);

    databaseTestSupport.runSqlSentences(SpringTestUtils.getDataSource(),
        "target/katari-sample.ddl");
    // Initialize the autoincrement of all created tables to 1000. This is to
    // make it very obvious when the client is formatting ids where it
    // shouldn't (1,000 instead of 1000).
    databaseTestSupport.initializeAutoincrement(
        SpringTestUtils.getDataSource(), 1000);
    databaseTestSupport.runSqlSentences(SpringTestUtils.getDataSource(),
        "src/main/sql/db-setup.sql");
    databaseTestSupport.runSqlSentences(SpringTestUtils.getDataSource(),
        "src/test/sql/db-setup.sql");

    log.trace("Leaving testDoSetup");
  }
}

