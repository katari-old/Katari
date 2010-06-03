package com.globant.katari.report.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.globant.katari.core.application.Initializable;
import com.globant.katari.report.application.GenerateReportCommand;
import com.globant.katari.report.domain.ReportType;

/**
 * This controller asks the user to fill the report parameters and output format
 * and afterwards, it shows the desired report.
 *
 * @author sergio.sobek.
 */
public abstract class ParameterController extends SimpleFormController {

  /** The class logger. */
  private static Log log = LogFactory.getLog(ParameterController.class);

  /**
   * Constructor.
   */
  public ParameterController() {
    setBindOnNewForm(true);
  }

  /**
   * Create a reference data map for the given request.
   *
   * @param request The HTTP request we are processing. It cannot be null.
   *
   * @param command The command object with the bound parameters. It cannot be
   * null.
   *
   * @param errors The errors holder. It cannot be null.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return The map for the form view, the model of the MdelAndView.
   */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request,
      final Object command, final Errors errors) throws Exception {
    log.trace("Entering referenceData");

    Validate.notNull(request, "The request cannot be null");
    Validate.notNull(command, "The command cannot be null");
    Validate.notNull(errors, "The errors cannot be null");

    // Initialize the command.
    if (!(command instanceof Initializable)) {
      throw new RuntimeException("Command does not implement Initializable.");
    }
    ((Initializable) command).init();

    Map<String, Object> result = new LinkedHashMap<String, Object>();
    ReportType[] reportTypesArray = ReportType.values();
    result.put("reportTypes", reportTypesArray);
    result.put("command", command);

    log.trace("Leaving referenceData");

    return result;
  }

  /**
   * Submits the form.
   *
   * @param request the servlet request.
   * @param response the servlet response.
   * @param command the command object associated with the form.
   * @param errors the form errors.
   * @return the model and view.
   * @throws Exception when there's an error.
   */
  @Override
  protected ModelAndView onSubmit(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {
    log.trace("Entering onSubmit");

    GenerateReportCommand parameterCommand;
    parameterCommand = (GenerateReportCommand) command;

    ReportType reportType = parameterCommand.getReportType();

    if (ReportType.PDF.equals(reportType)) {
      response.setContentType("application/pdf");
    } else if (ReportType.XML.equals(reportType)) {
      response.setContentType("text/xml");
    } else if (ReportType.EXCEL.equals(reportType)) {
      response.setContentType("application/vnd.ms-excel");
    } else if (ReportType.HTML.equals(reportType)) {
      response.setContentType("text/html");
    }

    parameterCommand.setOutputStream(response.getOutputStream());
    parameterCommand.execute();

    log.trace("Leaving onSubmit");

    return null;
  }

  /**
   * Creates the object associated with the form.
   *
   * The form uses this object for doing the bindings.
   *
   * @param request the servlet request
   * @return the command object used in the form
   * @throws Exception when there's an error
   */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception {
    log.trace("Entering formBackingObject");
    log.trace("Leaving formBackingObject");
    return createCommandBean();
  }

  /**
   * Creates the command associated with this controller. In the configuration
   * file, this method has to be defined as a lookup-method.
   *
   * @return the command
   */
  protected abstract Object createCommandBean();

  /**
   * Provides a custom binder for dates.
   *
   * @param aRequest - the request.
   * @param aBinder - the binder.
   * @throws Exception - the exception.
   */
  @Override
  protected void initBinder(final HttpServletRequest aRequest,
      final ServletRequestDataBinder aBinder) throws Exception {
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    CustomDateEditor editor = new CustomDateEditor(df, true);
    aBinder.registerCustomEditor(Date.class, editor);
  }

  /** Detects form reloading.
   * {@inheritDoc}
   */
  @Override
  protected boolean isFormSubmission(final HttpServletRequest request) {
    boolean formSubmission = false;
    if (request.getMethod().equals("POST")) {
      formSubmission = !request.getParameter("reloading").equals("true");
    }
    return formSubmission;
  }
}
