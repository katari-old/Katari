package com.globant.katari.report.application;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.core.application.Command;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;
import com.globant.katari.report.domain.ReportSecurityUtils;

/** Command that retrieves a List of ReportDefinition.
 *
 * @author sergio.sobek
 */
public class ReportsCommand implements Command<List<ReportDefinition>> {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(ReportsCommand.class);

  /** The report repository.
   *
   * It is never null.
   */
  private JasperReportRepository reportRepository;

  /** Creates a new instance of JasperReportRepository.
   *
   * @param theRepository the report repository. It cannot be null.
   */
  public ReportsCommand(final JasperReportRepository theRepository) {
    Validate.notNull(theRepository, "The report repository cannot be null.");
    reportRepository = theRepository;
  }

  /**
   * Executes the command and returns a list with all the report definitions
   * that are already stored.
   *
   * @return a list with all the report definitions.
   */
  public List<ReportDefinition> execute() {
    log.trace("Entering execute");

    List<ReportDefinition> definitions;
    definitions = ReportSecurityUtils.getAccesibleReports(reportRepository);
    log.trace("Leaving execute");
    return definitions;
  }
}

