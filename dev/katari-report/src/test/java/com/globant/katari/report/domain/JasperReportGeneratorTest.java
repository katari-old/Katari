/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.domain;

import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

import com.globant.katari.report.ReportsTestSupport;

/**
 * UnitTest for JasperReportGenerator class.
 *
 * @author jorge.atucha@globant.com
 */
public class JasperReportGeneratorTest extends TestCase {

  /** The JasperReports generator. */
  private JasperReportGenerator generator;

  /** The id of the report saved for testing. */
  private long savedReportId;

  /** The datasource. */
  private Connection connection;

  /** the map with the parameter names and values. */
  private HashMap<String, Object> params;

  /** Sets up the database for tests.
   *
   * @throws ParseException
   */
  @Override
  protected final void setUp() throws Exception {
    generator = ReportsTestSupport.getGenerator();

    DataSource dataSource = ReportsTestSupport.getDataSource();
    connection = dataSource.getConnection();

    // add one report
    savedReportId = ReportsTestSupport.createSampleReport().getId();

    params = new HashMap<String, Object>();
    params.put("TEST_PARAM_1", "Custom Example Text 1");
    params.put("TEST_PARAM_2", Calendar.getInstance().getTime());
    params.put("TEST_PARAM_3", 1);
  }

  @Override
  public final void tearDown() throws Exception {
    connection.close();
    connection = null;
  }

  /**
   * Tests the generation of a report without giving a connection.
   */
  public final void testGenerationWithoutConnection() throws Exception {
    ServletOutputStream output;
    output = EasyMock.createMock(ServletOutputStream.class);
    ReportType type = ReportType.PDF;
    generator.generate(savedReportId, params, type, output, null);
  }

  /**
   * Tests the generation of a PDF report.
   */
  public final void testPdfGeneration() throws Exception {
    ServletOutputStream output;
    output = EasyMock.createMock(ServletOutputStream.class);
    ReportType type = ReportType.PDF;
    generator.generate(savedReportId, params, type, output, connection);
  }

  /**
   * Tests the generation of a Excel report.
   */
  public final void testExcelGeneration() throws Exception {
    ServletOutputStream output;
    output = EasyMock.createMock(ServletOutputStream.class);
    ReportType type = ReportType.EXCEL;
    generator.generate(savedReportId, params, type, output, connection);
  }

  /**
   * Tests the generation of a Html report.
   */
  public final void testHtmlGeneration() throws Exception {
    ServletOutputStream output;
    output = EasyMock.createMock(ServletOutputStream.class);
    ReportType type = ReportType.HTML;
    generator.generate(savedReportId, params, type, output, connection);
  }

  /**
   * Tests the generation of a XML report.
   */
  public final void testXmlGeneration() throws Exception {
    ServletOutputStream output;
    output = EasyMock.createMock(ServletOutputStream.class);
    ReportType type = ReportType.XML;
    generator.generate(savedReportId, params, type, output, connection);
  }
}

