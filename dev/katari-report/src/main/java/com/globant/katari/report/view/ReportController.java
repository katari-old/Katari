package com.globant.katari.report.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.globant.katari.core.application.Command;
import com.globant.katari.report.application.SaveReportCommand;

/**
 * Controller to handle operations on a single Report Definition.
 *
 * This controller handles view, create, edit and delete of reports.
 *
 * @author sergio.sobek
 */
public abstract class ReportController extends SimpleFormController {

  /**
   * The class logger.
   */
  private static Log log = LogFactory.getLog(ReportController.class);

  /**
   * Creates a default ReportController.
   */
  public ReportController() {
    setSuccessView("redirect:reports.do");
    setBindOnNewForm(true);
  }

  /**
   * Create a reference data map for the given request.
   *
   * @param request
   *                The HTTP request we are processing. It cannot be null.
   *
   * @param command
   *                The command object with the bound parameters. It cannot be
   *                null.
   *
   * @param errors
   *                The errors holder. It cannot be null.
   *
   * @exception Exception
   *                    if the application logic throws an exception.
   *
   * @return the Map for the form view, the model of the MdelAndView. This
   *         implementation always returns null.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Map referenceData(final HttpServletRequest request,
      final Object command, final Errors errors) throws Exception {
    log.trace("Entering referenceData");

    Validate.notNull(request, "The request cannot be null");
    Validate.notNull(command, "The command cannot be null");
    Validate.notNull(errors, "The errors cannot be null");

    SaveReportCommand saveReportCommand;
    saveReportCommand = (SaveReportCommand) command;
    saveReportCommand.init();

    log.trace("Leaving referenceData");
    return null;
  }

  /**
   * Saves the report.
   *
   * @param command
   *                Form object with request parameters bound onto it.
   *
   * @exception Exception
   *                    if the application logic throws an exception.
   */
  protected void doSubmitAction(final Object command) throws Exception {
    log.trace("Entering doSubmitAction");

    ((Command<?>) command).execute();

    log.trace("Leaving doSubmitAction");
  }

  /**
   * Registers a binder to handle file uploads.
   *
   * The registered binder stores the content of the file in a command attribute
   * of type byte[].
   *
   * @param request the servlet request. It cannot be null.
   *
   * @param binder the binder. It cannot be null.
   *
   * @throws Exception when there's an error.
   */
  protected void initBinder(final HttpServletRequest request,
      final ServletRequestDataBinder binder) throws Exception {

    Validate.notNull(request, "The request cannot be null.");
    Validate.notNull(binder, "The binder cannot be null.");

    super.initBinder(request, binder);
    binder.registerCustomEditor(byte[].class,
        new EmptyAwareMultipartFileEditor());
  }

  /**
   * Creates the object associated with the form.
   *
   * The form uses this object for doing the bindings.
   *
   * @param request the servlet request. This parameter is no used.
   *
   * @return the command object used in the form. It never returns null.
   *
   * @throws Exception when there's an error
   */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception {
    log.trace("Entering formBackingObject");
    log.trace("Leving formBackingObject");
    return createCommandBean();
  }

  /**
   * Creates the command associated with this controller.
   *
   * In the configuration file, this method has to be defined as a
   * lookup-method.
   *
   * @return the injected command. It never returns null.
   */
  protected abstract Object createCommandBean();
}
