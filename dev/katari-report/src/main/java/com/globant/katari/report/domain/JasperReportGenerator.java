package com.globant.katari.report.domain;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Service class that generates jasper reports.
 *
 * @author sergio.sobek
 *
 */
public final class JasperReportGenerator {

  /** The class logger. */
  private static Log log = LogFactory.getLog(JasperReportGenerator.class);

  /** The report repository. It cannot be null. */
  private JasperReportRepository repository;

  /**
   * Constructor.
   *
   * @param theRepository the jasper report repository. It cannot be null.
   */
  protected JasperReportGenerator(final JasperReportRepository theRepository) {
    Validate.notNull(theRepository, "The the repository cannot be null");
    repository = theRepository;
  }

  /**
   * Generate the output report.
   *
   * @param reportId the id of the report definition. It must be greater than 0.
   * @param parameters map of the values of the report's parameters. It cannot
   * be null.
   * @param theReportType with the type of output for the report. It cannot be
   * null.
   * @param theOutputStream (output parameter) The output stream where the
   * generated report is written to. It cannot be null.
   * @param connection to the database.
   *
   * @return OutputStream - An output stream with the generated report.
   */
  public OutputStream generate(final long reportId,
      final Map<String, Object> parameters, final ReportType theReportType,
      final OutputStream theOutputStream, final Connection connection) {
    Validate.isTrue(reportId > 0, "The reportId must be greater than 0");
    Validate.notNull(parameters, "The parameters map cannot be null.");
    Validate.notNull(theReportType, "The reportType cannot be null.");
    Validate.notNull(theOutputStream, "The outputStream cannot be null.");

    ReportDefinition rt = repository.findReportDefinitionById(reportId);
    try {
      log.trace("Loading report...");

      JasperDesign jasperDesign = JRXmlLoader.load(new ByteArrayInputStream(rt
          .getReportContent()));

      log.trace("Compiling report...");

      JasperReport jasperReport = JasperCompileManager
          .compileReport(jasperDesign);
      jasperReport.setWhenNoDataType(
          JasperReport.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL);

      log.trace("Filling report...");

      JasperPrint filledReport;

      if (connection == null) {
        filledReport = JasperFillManager.fillReport(jasperReport, parameters,
            new JREmptyDataSource());
      } else {
        filledReport = JasperFillManager.fillReport(jasperReport, parameters,
            connection);
      }

      if (ReportType.PDF.equals(theReportType)) {
        JasperExportManager.exportReportToPdfStream(filledReport,
            theOutputStream);
      } else if (ReportType.XML.equals(theReportType)) {
        JasperExportManager.exportReportToXmlStream(filledReport,
            theOutputStream);
      } else if (ReportType.EXCEL.equals(theReportType)) {
        JRXlsExporter xlsExporter = new JRXlsExporter();
        xlsExporter
            .setParameter(JRExporterParameter.JASPER_PRINT, filledReport);
        xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,
            theOutputStream);
        xlsExporter.exportReport();
      } else if (ReportType.HTML.equals(theReportType)) {
        JRHtmlExporter htmlExporter = new JRHtmlExporter();
        htmlExporter.setParameter(
          JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        htmlExporter.setParameter(JRExporterParameter.JASPER_PRINT,
            filledReport);
        htmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM,
            theOutputStream);
        htmlExporter.exportReport();
      }

    } catch (JRException e) {
      throw new RuntimeException("Oops: reports engine exception", e);
    } catch (Exception e) {
      throw new RuntimeException("Oops: General Reports Engine exception", e);
    }
    return theOutputStream;
  }
}
