package com.globant.katari.report.application;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.core.application.Command;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;

/**
 * Command for Deleting a Report definition.
 *
 * @author sergio.sobek
 */
public class DeleteReportCommand implements Command<Void> {

  /** The class logger. */
  private static Log log = LogFactory.getLog(DeleteReportCommand.class);

  /** The report repository. It is never null. */
  private JasperReportRepository reportRepository;

  /** The report id to be deleted. It is never used without initializing. */
  private long reportId;

  /**
   * Creates a new Command with a JasperRepository.
   *
   * @param theReportRepository the reportDefinition repository. It cannot be
   * null.
   */
  public DeleteReportCommand(final JasperReportRepository theReportRepository) {
    Validate.notNull(theReportRepository,
        "the report repository cannot be null");

    reportRepository = theReportRepository;
  }

  /**
   * Gets the ReportId.
   *
   * @return the reportId
   */
  public long getReportId() {
    return reportId;
  }

  /**
   * Sets the ReportId.
   *
   * @param theReportId the report id. It cannot be equal or less than 0.
   */
  public void setReportId(final long theReportId) {
    Validate.isTrue(theReportId > 0, "The report id must be greater than 0.");
    reportId = theReportId;
  }

  /**
   * Executes the command.
   *
   * This command deletes a report.
   *
   * @return a null value.
   */
  public Void execute() {
    log.trace("Entering execute");

    ReportDefinition reportDefinition;
    reportDefinition = reportRepository.findReportDefinitionById(reportId);
    reportRepository.remove(reportDefinition);

    log.trace("Leaving execute");
    return null;
  }
}
