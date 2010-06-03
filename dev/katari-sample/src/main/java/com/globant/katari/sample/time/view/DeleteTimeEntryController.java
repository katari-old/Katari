package com.globant.katari.sample.time.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.globant.katari.sample.time.application.DeleteTimeEntryCommand;

/** Spring MVC Controller to delete a time entry.
 *
 * Subclasses need to override <code>createCommandBean</code> and
 * <code>createViewTimeEntryCommand</code> to retrieve a backing object for
 * the current form. Use method injection to override
 * <code>createCommandBean</code> and <code>createViewTimeEntryCommand</code>.
 *
 * @author nicolas.frontini
 */
public abstract class DeleteTimeEntryController extends SimpleFormController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(MyTimeController.class);

  /** Receives the request to delete a time entry.
   *
   * Generate a view for a specific view name, returning the ModelAndView
   * provided there.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are creating.
   *
   * @param command Form object with request parameters bound onto it.
   *
   * @param errors Errors instance without errors.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView for the next view.
   */
  protected final ModelAndView onSubmit(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {
    log.trace("Entering onSubmit");

    DeleteTimeEntryCommand deleteCommand = (DeleteTimeEntryCommand) command;
    deleteCommand.execute();

    ModelAndView successMav = new ModelAndView("redirect:myTime.do?date="
        + BaseTimeController.formatDate(deleteCommand.getDate()));

    log.trace("Leaving onSubmit");
    return successMav;
  }

  /** Retrieve a backing object for the current form from the given request.
   *
   * @param request The HTTP request we are processing.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return The command bean object.
   */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception {
    return createCommandBean();
  }

  /** This method is injected by AOP.
   *
   * @return Returns the command bean injected.
   */
  protected abstract Object createCommandBean();
}
