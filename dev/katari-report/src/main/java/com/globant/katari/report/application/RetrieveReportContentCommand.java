package com.globant.katari.report.application;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.core.application.Command;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;

/**
 * Command for retrieve report definitions content.
 */
public class RetrieveReportContentCommand implements Command<byte[]> {

  /** The report name. */
  private String name = null;

  /** The class logger. */
  private static Log log = LogFactory
      .getLog(RetrieveReportContentCommand.class);

  /** The Report Definition repository. It is never null. */
  private JasperReportRepository jasperReportRepository;

  /** The report definition id. */
  private long reportId = 0;

  /** Creates a new RetrieveReportContentCommand object with a
   * JasperReportRepository dependency.
   * @param theReportDefinition the report repository. It cannot be null.
   */
  public RetrieveReportContentCommand(
      final JasperReportRepository theReportDefinition) {
    Validate.notNull(theReportDefinition,
        "The report repository cannot be null");
    jasperReportRepository = theReportDefinition;
  }

  /** Retrieves the report definition content.
   * @return The report content byte array, it never returns null.
   */
  public byte[] execute() {
    log.trace("entering execute");
    ReportDefinition aReportDefinition = jasperReportRepository
        .findReportDefinitionById(reportId);
    Validate.notNull(aReportDefinition, "No definition found for the current "
        + "report id");
    name = aReportDefinition.getName();
    return aReportDefinition.getReportContent();
  }

  /** Returns the report name for file naming.
   * @return reprot name, it never returns null.
   */
  public String getName() {
    if (name == null) {
      throw new IllegalStateException("You must call execute before calling "
          + "this method");
    }
    return name;
  }

  /** Report id setter.
   * @param theReportId the report id;
   */
  public void setReportId(final long theReportId) {
    reportId = theReportId;
  }
}
