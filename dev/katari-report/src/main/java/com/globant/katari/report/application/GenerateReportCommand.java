/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.application;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;

import com.globant.katari.core.application.Initializable;
import com.globant.katari.core.application.ValidatableCommand;
import com.globant.katari.report.domain.DropdownOptions;
import com.globant.katari.report.domain.JasperReportGenerator;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ParameterDefinition;
import com.globant.katari.report.domain.ReportDefinition;
import com.globant.katari.report.domain.ReportSecurityUtils;
import com.globant.katari.report.domain.ReportType;

/**
 * Holds a list of parameter descriptor command. Used for binding the parameter
 * list with the UI.
 *
 * @author sergio.sobek
 */
public class GenerateReportCommand implements ValidatableCommand<Void>,
    Initializable {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(GenerateReportCommand.class);

  /** Map of the posted values, it is never null. */
  private Map<String, String> values = new HashMap<String, String>();

  /** The id of the report definition owning the parameter. */
  private long reportId;

  /** The repository. It is never null. */
  private JasperReportRepository repository;

  /** The jasper report generator service. It is never null. */
  private JasperReportGenerator reportGenerator;

  /**
   * This command obtains a database connection from this dataSource and passes
   * it to the report generator to query the database.
   *
   * It is never null.
   */
  private DataSource dataSource;

  /**
   * The report generator writes the report output to this stream.
   *
   * This stream can be obtained, for example, form the servlet response. It
   * must be set before generating the report to a non null value.
   */
  private OutputStream outputStream;

  /** The report output type, by default it is set to PDF, it is never null. */
  private ReportType reportType = ReportType.PDF;

  /** The list of parameters from a report, it is null before the init phase. */
  private List<ParameterDefinition> parameters = null;

  /** The report name, it is null before the init phase. */
  private String reportName = null;

  /**
   * Constructor.
   *
   * @param theRepository the repository for retrieving the report definition.
   * It cannot be null.
   * @param theGenerator the repository generator. It cannot be null.
   * @param theDataSource the data source a data source for connecting to a
   * database. It cannot be null.
   */
  public GenerateReportCommand(final JasperReportRepository theRepository,
      final JasperReportGenerator theGenerator,
      final DataSource theDataSource) {
    Validate.notNull(theRepository, "The report repository cannot be null");
    Validate.notNull(theGenerator, "The report repository cannot be null");
    Validate.notNull(theDataSource, "The report repository cannot be null");

    repository = theRepository;
    reportGenerator = theGenerator;
    dataSource = theDataSource;
  }

  /**
   * Gets the reportId.
   *
   * @return the id of the report.
   */
  public long getReportId() {
    return reportId;
  }

  /**
   * Gets the reportName.
   *
   * @return the name of the report.
   */
  public String getReportName() {
    return reportName;
  }

  /**
   * Sets the reportId.
   *
   * @param theReportId
   *            the parameterReportId to set.
   */
  public void setReportId(final long theReportId) {
    reportId = theReportId;
  }

  /**
   * Gets the list of parameters of the report with id = reportId.
   *
   * @return the parameters
   */
  public List<ParameterDefinition> getParameters() {
    return parameters;
  }

  /**
   * Gets the map with the parameter values.
   *
   * @return a map with the parameter name as key and the parameter value as
   *         value.
   */
  public Map<String, String> getValues() {
    return values;
  }

  /**
   * Sets the map with parameter values.
   *
   * @param theValues
   *            contains a map with each parameter name as key and each
   *            parameter value as value for that key. It cannot be null.
   */
  public void setValues(final Map<String, String> theValues) {
    Validate.notNull(theValues, "the parameter values map cannot be null.");
    values = theValues;
  }

  /** Sets the output stream.
   *
   * @param theOutStream the stream to write the generated report. It cannot be
   * null.
   */
  public void setOutputStream(final OutputStream theOutStream) {
    Validate.notNull(theOutStream, "The output stream cannot be null");
    this.outputStream = theOutStream;
  }

  /**
   * Getter of the reportType.
   *
   * @return the reportType.
   */
  public ReportType getReportType() {
    return reportType;
  }

  /**
   * Setter of the reportType.
   *
   * @param theReportType
   *            wth the report output type.
   */
  public void setReportType(final ReportType theReportType) {
    Validate.notNull(theReportType, "theReportType cannnot be null.");
    reportType = theReportType;
  }

  /**
   * Executes the command.
   *
   * This Command delegates to the <code>JasperReportGenerator</code> the
   * generation of the report. The report is written to the output stream
   * specified that this command has as property.
   *
   * You must call setOutputStream and setValues before calling execute.
   *
   * @return null.
   */
  public Void execute() {
    Validate.notNull(values, "the parameter values map cannot be null.");
    Validate.notNull(outputStream, "The output stream cannot be null");
    Map<String, Object> map = new HashMap<String, Object>();
    ReportDefinition report = repository.findReportDefinitionById(reportId);
    Validate.isTrue(ReportSecurityUtils.isAccesible(report),
        "User has not rigths to generate this report");

    List<ParameterDefinition> definitions = report.getParameterDefinitions();
    for (ParameterDefinition parameter : definitions) {
      String parameterName = parameter.getName();
      String parameterValue = values.get(parameterName);
      if (parameter.isOptional() && StringUtils.isBlank(parameterValue)) {
        break;
      }
      try {
        map.put(parameter.getName(), parameter.convertValue(parameterValue));
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException("Error converting parameter "
            + parameterName, e);
      }
    }
    // Generates the report and writes it in the output stream.
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      reportGenerator.generate(report.getId(), map, reportType, outputStream,
          connection);
    } catch (SQLException e) {
      log.error("Error executing report", e);
      throw new RuntimeException("Error executing report", e);
    } finally {
      // DbUtils has rollbackAndCloseQuietly(connection), not used here because
      // we would add another dependency.
      if (connection != null) {
        try {
          connection.rollback();
        } catch (SQLException e) {
          log.warn("Unexpected error rolling back a connection - ignored.", e);
        }
        try {
          connection.close();
        } catch (SQLException e) {
          log.warn("Unexpected error closing connection - ignored.", e);
        }
      }
    }
    return null;
  }

  /**
   * Validation method.
   *
   * @param errors Holder for all the validation errors. It cannot be null.
   */
  public void validate(final Errors errors) {
    Validate.notNull(errors, "The errors holder cannot be null.");
    ReportDefinition report = repository.findReportDefinitionById(reportId);
    List<ParameterDefinition> definitions;
    definitions = report.getParameterDefinitions();
    for (ParameterDefinition parameter : definitions) {
      String parameterName = parameter.getName();
      String parameterValue = values.get(parameterName);
      String[] commonArgs = {parameterName};

      if (StringUtils.isBlank(parameterValue)) {
        if (!parameter.isOptional()) {
          errors.rejectValue("values", "error.required", commonArgs, "");
        }
      } else {
        try {
          parameter.convertValue(parameterValue);
        } catch (NumberFormatException e) {
          errors.rejectValue("values", "error.incorrectType", commonArgs, "");
        } catch (ParseException e) {
          errors.rejectValue("values", "error.incorrectFormat", commonArgs, "");
        }
      }
    }
  }

  /**
   * Initializes the command.
   *
   * During initialization, a report with the corresponding reportId is
   * retrieved from the repository. This report must exist in the repository.
   */
  public void init() {
    Validate.isTrue(reportId > 0, "The report id should be greater than 0.");
    ReportDefinition report = repository.findReportDefinitionById(reportId);
    Validate.notNull(report, "The report id must exist in the repository.");

    reportName = report.getName();
    parameters = report.getParameterDefinitions();
  }

  /**
   * Returns dropdown options for a given parameter.
   * @param theParameter the report parameter definition, it cannot be null.
   * @return a map of possible values.
   * TODO that must be on the init method.
   */
  public Map<String, String> getDropdownOptions(
      final ParameterDefinition theParameter) {
    Map<String, String> dropdownOptions = new LinkedHashMap<String, String>();

    List<DropdownOptions> options = repository.getDropdownOptions(theParameter,
        values);
    // parse results and insert them into dropDownOptions
    String firstValue = null;
    if (options.size() != 0) {
      firstValue = options.get(0).getValue();
    }

    for (DropdownOptions option : options) {
      dropdownOptions.put(option.getValue(), option.getLabel());
    }
    // we must set a default value for linked combos
    if (values.get(theParameter.getName()) == null) {
      values.put(theParameter.getName(), firstValue);
    }
    return dropdownOptions;
  }
}
