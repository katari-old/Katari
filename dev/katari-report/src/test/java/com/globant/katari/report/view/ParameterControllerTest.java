/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.view;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.easymock.classextension.EasyMock;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

import com.globant.katari.report.ReportsTestSupport;
import com.globant.katari.report.application.GenerateReportCommand;
import com.globant.katari.report.domain.ReportDefinition;
import com.globant.katari.report.domain.ReportType;

/**
 * Test the ReportController controller from the reports module.
 *
 * @author jorge.atucha@globant.com
 */
public class ParameterControllerTest extends
    AbstractTransactionalSpringContextTests {

  /** The name of the spring form view in the ReportController. */
  private static final String EDIT_PARAMETER_FORM_VIEW = "editParameters";

  /** The controller. */
  private ParameterController editParameterController;

  /** The saved report used for testing. */
  private ReportDefinition savedReport;

  public ParameterControllerTest() {
    setDefaultRollback(false);
  }

  /** Sets up the database for testing.
   */
  @Override
  protected final void onSetUpBeforeTransaction() throws Exception {
    ReportsTestSupport.initTestReportSecurityContext("REPORT_ADMIN");
    editParameterController = (ParameterController) ReportsTestSupport
        .getApplicationContext().getBean("/editParameters.do");

    savedReport = ReportsTestSupport.createSampleReport();
  }

  /**
   * Tests the submit action for the editReport.do bean.
   *
   * Tests that modifications on a report definition are persisted after calling
   * the ReportController.doSubmitAction method.
   */
  public final void testEditParameterDoSubmitAction() throws Exception {

    MockHttpServletRequest req = new MockHttpServletRequest();

    ServletOutputStream output = EasyMock.createMock(ServletOutputStream.class);

    HttpServletResponse resp = createStrictMock(HttpServletResponse.class);
    resp.setContentType("application/pdf");
    expectLastCall().times(1);
    expect(resp.getOutputStream()).andReturn(output).times(1);
    replay(resp);

    BindingResult bindingResult = createStrictMock(BindingResult.class);
    BindException errors = new BindException(bindingResult);

    GenerateReportCommand command = (GenerateReportCommand)
      editParameterController.createCommandBean();

    command.setReportId(savedReport.getId());
    Map<String, String> values = new HashMap<String, String>();
    values.put("TEST_PARAM_1", "Custom Example Text 1");
    values.put("TEST_PARAM_3", "1");
    values.put("TEST_PARAM_4", "22");

    command.setValues(values);
    command.setReportType(ReportType.PDF);
    command.init();

    editParameterController.onSubmit(req, resp, command, errors);

    assertEquals(EDIT_PARAMETER_FORM_VIEW, editParameterController
        .getFormView());
  }

  /**
   * Tests the referenceData() method of the ParameterController.
   *
   * Tests that a valid map with the reference data is constructed.
   */
  public final void testReferenceData() throws Exception {
    MockHttpServletRequest req = new MockHttpServletRequest();
    GenerateReportCommand command = (GenerateReportCommand)
      editParameterController.createCommandBean();
    command.setReportId(savedReport.getId());

    BindingResult bindingResult = createStrictMock(BindingResult.class);
    BindException errors = new BindException(bindingResult);

    Map<String, Object> map = editParameterController.referenceData(req,
        command, errors);
    assertNotNull(map);
    assertTrue(map.size() == 2);
    assertNotNull(map.get("command"));
    assertNotNull(map.get("reportTypes"));
  }

  /**
   * Tests the initBinder method of the ParameterController controller.
   *
   * It tests that there is a custom binder defined to handle Date properties.
   */
  public final void testInitBinder() throws Exception {
    GenerateReportCommand command = (GenerateReportCommand)
      editParameterController.createCommandBean();

    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
    editParameterController.initBinder(request, binder);

    PropertyEditor customEditor = binder.findCustomEditor(Date.class, null);
    assertNotNull(customEditor);
    assertTrue(CustomDateEditor.class.isInstance(customEditor));
  }

  /**
   * Tests the formBackingObject method of the ParameterController controller.
   *
   * It tests that a valid formBacking Object is returned when editing
   * parameters.
   */
  public final void testEditParameterFormBackingObject() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    GenerateReportCommand fbo = (GenerateReportCommand) editParameterController
        .formBackingObject(request);
    assertNotNull(fbo);
    assertTrue(GenerateReportCommand.class.isInstance(fbo));
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

