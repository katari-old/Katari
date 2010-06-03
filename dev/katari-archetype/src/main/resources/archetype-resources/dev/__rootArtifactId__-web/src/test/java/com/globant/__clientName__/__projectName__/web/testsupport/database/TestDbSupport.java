#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.testsupport.database;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import com.globant.${clientName}.${projectName}.web.testsupport.SpringTestUtils;

/** This class contains methods to initialize a cleaned database.
 *
 * @author nicolas.frontini
 */
public class TestDbSupport extends TestCase {

  /** A logger.
   */
  private static Log log = LogFactory.getLog(TestDbSupport.class);

  /** This test marker, is the marker of the db of permision.
   */
  private static String testMarker = "${clientName}_${projectName}_marker_666";

  /** Initialize this test case.
   */
  public final void setUp() {
    org.apache.log4j.PropertyConfigurator.configure(
        "src/test/resources/log4j.properties");
  }

  /** This is a setup method of this TestCase.
   */
  public final void testDoSetup() throws Exception {
    log.trace("Entering testDoSetup");

    log.info("Cleaning database");
    LocalSessionFactoryBean sessionFactory = (LocalSessionFactoryBean)
        SpringTestUtils.getBeanFactory().getBean("&katari.sessionFactory");
    DatabaseTestSupport databaseTestSupport = DatabaseTestSupport.create(
        sessionFactory);
    databaseTestSupport.dropAll(testMarker);

    databaseTestSupport.runSqlSentences("target/${clientName}-${projectName}.ddl");
    databaseTestSupport.runSqlSentences("src/main/sql/db-setup.sql");
    // If you have additional test data, add it here:
    // databaseTestSupport.runSqlSentences("src/test/sql/db-setup.sql");

    log.trace("Leaving testDoSetup");
  }
}

