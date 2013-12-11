/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.application;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.globant.katari.report.ReportsTestSupport;
import com.globant.katari.report.domain.JasperReportGenerator;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ParameterDefinition;
import com.globant.katari.report.domain.ReportDefinition;
import com.globant.katari.report.domain.ReportType;

/** Test the GenerateReportCommand controller from the report module.
 */
public class GenerateReportCommandTest {

  /** The reports repository. */
  private JasperReportRepository repository;

  private JasperReportGenerator generator;

  private DataSource dataSource;

  /** The saved report used for testing. */
  private ReportDefinition savedReport;

  /**
   * Injects the repository and sets up the database for testing.
   */
  @Before
  public final void setUp() throws Exception {
    ReportsTestSupport.initTestReportSecurityContext("REPORT_ADMIN");

    ReportsTestSupport.get().clearDatabase();

    ReportsTestSupport.get().beginTransaction();

    repository = ReportsTestSupport.getRepository();
    generator = ReportsTestSupport.getGenerator();
    dataSource = ReportsTestSupport.get().getDataSource();
    savedReport = ReportsTestSupport.createSampleReport();

    ReportsTestSupport.get().endTransaction();
  }

  /** Tests for a bug that did not free the connection after generating the
   * report.
   */
  @Test
  public final void testExecute_multipleTimes() throws Exception {

    ReportsTestSupport.get().beginTransaction();

    GenerateReportCommand command;
    command = new GenerateReportCommand(repository, generator, dataSource);

    command.setReportId(savedReport.getId());
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "5");

    command.setValues(values);
    command.setReportType(ReportType.PDF);

    command.setOutputStream(EasyMock.createMock(ServletOutputStream.class));
    command.init();

    for (int i = 0; i < 10; ++ i) {
      command.execute();
    }

    ReportsTestSupport.get().endTransaction();

  }

  @Test
  public final void testExecute_reloadDropdown() throws Exception {

    ReportsTestSupport.get().beginTransaction();
    GenerateReportCommand command;
    command = new GenerateReportCommand(repository, generator, dataSource);

    command.setReportId(savedReport.getId());
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "12");

    command.setValues(values);
    command.setReportType(ReportType.PDF);

    command.setOutputStream(EasyMock.createMock(ServletOutputStream.class));
    command.init();
    command.execute();
    Map<String, String> possibleValues = null;
    for (ParameterDefinition parameterDefinition :
      savedReport.getParameterDefinitions()) {
      if (parameterDefinition.getName().equals("TEST_PARAM_4")) {
        possibleValues = command.getDropdownOptions(parameterDefinition);
      }
    }
    assertNotNull(possibleValues);

    ReportsTestSupport.get().endTransaction();
  }

  private Errors runValidateWithValues(final Map<String, String> values) {
    GenerateReportCommand command;
    command = new GenerateReportCommand(repository, generator, dataSource);

    command.setReportId(savedReport.getId());
    command.setValues(values);
    command.setReportType(ReportType.PDF);

    command.setOutputStream(EasyMock.createMock(ServletOutputStream.class));
    Errors errors = new BindException(command, "command");
    command.init();
    command.validate(errors);
    return errors;
  }

  @Test
  public final void testValidate_ok() throws Exception {
    ReportsTestSupport.get().beginTransaction();

    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(0, errors.getAllErrors().size());
    ReportsTestSupport.get().endTransaction();
  }

  public final void testValidate_invalidFormat() throws Exception {

    ReportsTestSupport.get().beginTransaction();

    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_3", "xcvvxc");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
    ReportsTestSupport.get().endTransaction();
  }

  @Test
  public final void testValidate_requiredValuesEmpty() throws Exception {

    ReportsTestSupport.get().beginTransaction();

    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
    ReportsTestSupport.get().endTransaction();
  }

  @Test
  public final void testValidate_requiredValuesBlank() throws Exception {

    ReportsTestSupport.get().beginTransaction();

    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "    ");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
    ReportsTestSupport.get().endTransaction();
  }

  @Test
  public final void testValidate_requiredValuesNull() throws Exception {

    ReportsTestSupport.get().beginTransaction();

    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", null);
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
    ReportsTestSupport.get().endTransaction();
  }

  @Test
  public final void testValidate_optionalValue() throws Exception {

    ReportsTestSupport.get().beginTransaction();

    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_2", "24/07/2008");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    values.put("TEST_PARAM_5", "");
    Errors errors = runValidateWithValues(values);
    assertEquals(0, errors.getAllErrors().size());
    ReportsTestSupport.get().endTransaction();
  }

}
