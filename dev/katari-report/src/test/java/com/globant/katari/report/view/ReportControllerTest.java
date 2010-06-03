package com.globant.katari.report.view;

import java.beans.PropertyEditor;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.globant.katari.report.ReportsTestSupport;
import com.globant.katari.report.application.DeleteReportCommand;
import com.globant.katari.report.application.SaveReportCommand;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;

/**
 * Test the ReportController controller from the reports module.
 *
 * @author jorge.atucha@globant.com
 *
 */
public class ReportControllerTest extends TestCase {

  /** The name of the report used for testing. */
  private static final String REPORT_NAME = "Test Project Report";
  /** The name of the report used for testing. */
  private static final String REPORT_DESCRIPTION = "Report description";
  /** The fake content of the report used for testing. */
  private static final byte[] REPORT_CONTENT = "<some report content"
      .getBytes();

  /** The name of the spring bean that handles the report edition. */
  private static final String EDIT_REPORT_BEAN_NAME = "/editReport.do";
  /** The name of the spring bean that handles the report deletion. */
  private static final String DELETE_REPORT_BEAN_NAME = "/deleteReport.do";

  /** The name of the spring form view in the ReportController. */
  private static final String EDIT_REPORT_FORM_VIEW = "editReport";
  /** The name of the spring success view in the ReportController. */
  private static final String SUCCESS_VIEW = "redirect:reports.do";

  /** The reports repository bean name. */
  private static final String REPOSITORY_BEAN_NAME = "jasperReportRepository";

  /** The controller. */
  private ReportController editReportController;

  /** The repository. */
  private JasperReportRepository repository;

  /** The report definition that is saved and used for testing. */
  private ReportDefinition savedReport;

  /**
   * Injects the repository and sets up the database for testing.
   */
  protected final void setUp() {
    ReportsTestSupport.initTestReportSecurityContext("REPORT_ADMIN");
    repository = (JasperReportRepository) ReportsTestSupport
        .getApplicationContext().getBean(REPOSITORY_BEAN_NAME);

    // Removes the unneeded reports.
    for (ReportDefinition report : repository.getReportList()) {
      repository.remove(report);
    }
    // add one report
    ReportDefinition testReport = new ReportDefinition(REPORT_NAME,
        REPORT_DESCRIPTION, REPORT_CONTENT);
    repository.save(testReport);

    savedReport = repository.findReportDefinition(REPORT_NAME);
  }

  /**
   * Tests the submit action for the editReport.do bean.
   *
   * Tests that modifications on a report definition are persisted after calling
   * the ReportController.doSubmitAction method.
   */
  public final void testEditReportDoSubmitAction() throws Exception {

    editReportController = (ReportController) ReportsTestSupport
        .getApplicationContext().getBean(EDIT_REPORT_BEAN_NAME);

    SaveReportCommand command = (SaveReportCommand) editReportController
        .createCommandBean();
    command.setReportId(savedReport.getId());
    command.setName(savedReport.getName() + "_MODIFIED");
    command.setReportContent(savedReport.getReportContent());

    editReportController.doSubmitAction(command);

    ReportDefinition retrievedReport = repository
        .findReportDefinitionById(command.getReportId());

    assertEquals(command.getName(), retrievedReport.getName());

    assertEquals(SUCCESS_VIEW, editReportController.getSuccessView());
    assertEquals(EDIT_REPORT_FORM_VIEW, editReportController.getFormView());
  }

  /**
   * Tests the submit action for the deleteReport.do bean.
   *
   * Tests that deletion of a report definition are persisted after calling the
   * ReportController.doSubmitAction method.
   */
  public final void testDeleteReportDoSubmitAction() throws Exception {
    editReportController = (ReportController) ReportsTestSupport
        .getApplicationContext().getBean(DELETE_REPORT_BEAN_NAME);

    DeleteReportCommand command;
    command = (DeleteReportCommand) editReportController.createCommandBean();
    command.setReportId(savedReport.getId());

    editReportController.doSubmitAction(command);

    ReportDefinition retrievedReport = repository
        .findReportDefinitionById(savedReport.getId());
    assertNull(retrievedReport);
    assertEquals(SUCCESS_VIEW, editReportController.getSuccessView());
  }

  /**
   * Tests the initBinder method of the ReportController controller.
   *
   * It tests that there is a custom binder defined to handle MultipartFile
   * properties.
   */
  public final void testInitBinder() throws Exception {
    editReportController = (ReportController) ReportsTestSupport
        .getApplicationContext().getBean(EDIT_REPORT_BEAN_NAME);

    SaveReportCommand command;
    command = (SaveReportCommand) editReportController.createCommandBean();

    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
    editReportController.initBinder(request, binder);

    PropertyEditor customEditor = binder.findCustomEditor(byte[].class, null);
    assertNotNull(customEditor);
    assertTrue(ByteArrayMultipartFileEditor.class.isInstance(customEditor));
  }

  /**
   * Tests the formBackingObject method of the ReportController controller.
   *
   * It tests that a valid formBacking Object is returned when editing reports.
   */
  public final void testEditReportFormBackingObject() throws Exception {
    editReportController = (ReportController) ReportsTestSupport
        .getApplicationContext().getBean(EDIT_REPORT_BEAN_NAME);

    MockHttpServletRequest request = new MockHttpServletRequest();
    Object formBackingObject = editReportController.formBackingObject(request);
    assertNotNull(formBackingObject);
    assertTrue(SaveReportCommand.class.isInstance(formBackingObject));
  }
}
