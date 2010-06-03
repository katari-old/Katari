/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.application;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import org.easymock.classextension.EasyMock;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractTransactionalSpringContextTests;
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
public class GenerateReportCommandTest extends
    AbstractTransactionalSpringContextTests {

  /** The reports repository. */
  private JasperReportRepository repository;

  private JasperReportGenerator generator;

  private DataSource dataSource;

  /** The saved report used for testing. */
  private ReportDefinition savedReport;

  public GenerateReportCommandTest() {
    setDefaultRollback(false);
  }

  /**
   * Injects the repository and sets up the database for testing.
   */
  @Override
  protected final void onSetUpBeforeTransaction() throws Exception {
    ReportsTestSupport.initTestReportSecurityContext("REPORT_ADMIN");

    repository = ReportsTestSupport.getRepository();
    generator = ReportsTestSupport.getGenerator();
    dataSource = ReportsTestSupport.getDataSource();
    savedReport = ReportsTestSupport.createSampleReport();
  }

  /** Tests for a bug that did not free the connection after generating the
   * report.
   */
  public final void testExecute_multipleTimes() throws Exception {
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
  }

  public final void testExecute_reloadDropdown() throws Exception {
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

  public final void testValidate_ok() throws Exception {
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(0, errors.getAllErrors().size());
  }

  public final void testValidate_invalidFormat() throws Exception {
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_3", "xcvvxc");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
  }

  public final void testValidate_requiredValuesEmpty() throws Exception {
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
  }

  public final void testValidate_requiredValuesBlank() throws Exception {
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "    ");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
  }

  public final void testValidate_requiredValuesNull() throws Exception {
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", null);
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    Errors errors = runValidateWithValues(values);
    assertEquals(1, errors.getAllErrors().size());
  }

  public final void testValidate_optionalValue() throws Exception {
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_2", "24/07/2008");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "1");
    values.put("TEST_PARAM_5", "");
    Errors errors = runValidateWithValues(values);
    assertEquals(0, errors.getAllErrors().size());
  }

  @Override
  protected ConfigurableApplicationContext loadContext(final Object key) throws
      Exception {
    return ReportsTestSupport.getApplicationContext();
  }

  @Override
  protected Object contextKey() {
    return "notnull";
  }
}

