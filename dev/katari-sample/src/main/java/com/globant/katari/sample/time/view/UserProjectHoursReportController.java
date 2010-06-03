/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.time.view;

import com.globant.katari.sample.time.application.UserProjectHoursReportCommand;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/** Spring MVC Controller to create a report of user hours by project.
 *
 * Subclasses need to override <code>createCommandBean</code> to retrieve a
 * backing object for the current form. Use method injection to override
 * <code>createCommandBean</code>.
 *
 * @author roman.cunci
 */
public abstract class UserProjectHoursReportController
  extends SimpleFormController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(
      UserProjectHoursReportController.class);

  /** Receives the request to show the report.
   *
   * Generate a view for a specific view name, returning the ModelAndView
   * provided there. The views ending in "JR" refer to a Jasper Reports view.
   *
   * @param request The HTTP request we are processing.
   * @param response The HTTP response we are creating.
   * @param command Form object with request parameters bound onto it.
   * @param errors Errors instance without errors.
   * @exception Exception if the application logic throws an exception.
   * @return the ModelAndView for the next view.
   */
  @Override
  protected final ModelAndView onSubmit(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {
    log.trace("Entering onSubmit");
    Map<Object, Object> model = new HashMap<Object, Object>();

    UserProjectHoursReportCommand userProjectHoursReportCommand =
      (UserProjectHoursReportCommand) command;
    model.put("datasource", userProjectHoursReportCommand.execute());
    model.put("format", userProjectHoursReportCommand.getFormat());

    log.trace("Leaving onSubmit");
    return new ModelAndView("userProjectHoursReportJR", model);
  }

  /**
   * Create a reference data map for the given request. Sets the values used
   * to render the view beside the command.
   *
   * @param request The HTTP request we are processing.
   * @exception Exception if the application logic throws an exception.
   * @return the Map for the form view.
   */
  @Override
  protected Map<?, ?> referenceData(final HttpServletRequest request)
      throws Exception {
    log.trace("Entering referenceData");

    Map<String, Object> reference = new  HashMap<String, Object>();
    reference.put("baseweb", request.getAttribute("baseweb"));
    reference.put("request", request);

    log.trace("Leaving referenceData");
    return reference;
  }

  /** Retrieve a backing object for the current form from the given request.
   *
   * @param request The HTTP request we are processing.
   * @exception Exception if the application logic throws an exception.
   * @return The command bean object.
   */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception {

    return createCommandBean();
  }

  /** This method is injected by AOP.
   * Creates a {@link UserProjectHoursReportCommand}.
   *
   * @return Returns the command bean injected.
   */
  protected abstract Object createCommandBean();
}

