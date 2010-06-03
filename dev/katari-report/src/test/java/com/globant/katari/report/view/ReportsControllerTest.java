package com.globant.katari.report.view;

import java.util.List;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;
import com.globant.katari.report.ReportsTestSupport;
import com.globant.katari.report.domain.ReportDefinition;

/**
 * Test the ReportController controller from the reports module.
 *
 * @author jorge.atucha@globant.com
 *
 */
public class ReportsControllerTest extends TestCase {

  /** The Reports Controller that's being tested. */
  private ReportsController controller;

  /** The command that the controller uses. */
  private Command<List<ReportDefinition>> command;

  /**
   * Injects the controller and the command being tested.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected final void setUp() throws Exception {
    controller = (ReportsController) ReportsTestSupport.getApplicationContext()
        .getBean("/reports.do");
    command = (Command<List<ReportDefinition>>) controller.createCommandBean();
    ReportsTestSupport.initTestReportSecurityContext("REPORT_ADMIN");
  }

  /**
   * Tests the showForm method of the controller.
   */
  @SuppressWarnings("unchecked")
  public final void testShowForm() throws Exception {
    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse resp = new MockHttpServletResponse();
    ModelAndView mav = controller.handle(req, resp, command, null);

    assertEquals("reportsDefinitions", mav.getViewName());

    List<ReportDefinition> reportDefinitions = (List<ReportDefinition>) mav
        .getModel().get("reportsDefinitions");
    assertNotNull(reportDefinitions);
    assertEquals(1, reportDefinitions.size());
  }
}

