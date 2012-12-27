package com.globant.katari.core.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.apache.commons.lang.Validate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.WebContentGenerator;

import com.globant.katari.core.application.Validatable;
import com.globant.katari.core.application.Command;

/** A simple command controller that executes the command when handling the
 * request.
 *
 * When the request is handled, this controller executes the command with
 * validations then forwards to the provided view.
 *
 * The view has a model object with 'result' as the value returned by
 * Command.execute() the 'command' as the value of the command object and
 * the 'errors' resulted from the bind and validate phase.
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public abstract class SimpleCommandController extends WebContentGenerator
  implements Controller {

  /** The class logger.*/
  private static Logger log = LoggerFactory
      .getLogger(SimpleCommandController.class);

  /** The name of the key of the result for this view.*/
  protected static final String RESULT_NAME = "result";

  /** The name of the key of the errors for this view.*/
  protected static final String RESULT_ERRORS = "errors";

  /** The name of the key of the command for this view.*/
  protected static final String COMMAND_NAME = "command";

  /** The name of the view to render, can be null.*/
  private String viewName;

  /** The property mapper, it's never null. */
  private ServletRequestPropertyMapper propertyMapper =
       new ServletRequestPropertyMapper();

  /** The list of property editor factory, can be null.*/
  private List<PropertyEditorBinder> propertyEditorBinder;

  /** {@inheritDoc}.*/
  public ModelAndView handleRequest(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    log.trace("Entering handleRequestInternal");

    Command<?> command = getCommand(request);
    ServletRequestDataBinder binder = bindCommandToCurrentRequest(
        request, response, command);
    BindException errors = new BindException(binder.getBindingResult());

    if (command instanceof Validatable) {
      ((Validatable) command).validate(errors);
    }

    /* TODO [waabox] checks if we need to verify the headers to
     * dispatch the response (JSON or HTML or whatever).
     */

    ModelAndView mav;
    mav = handleRequestInternal(request, response, command, errors);

    log.trace("Leaving handleRequestInternal");
    return mav;
  }

  /** Creates the model and view.
   * This is the extension point for particular controllers.
   *
   * @param request the servlet request.
   * @param response the servlet response.
   * @param command the command.
   * @param errors the spring bind errors.
   * @return the model and view, or null.
   * @throws Exception if something wrong happen.
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response, final Command<?> command,
      final BindException errors) throws Exception {

    Validate.notEmpty(viewName, "The view name cannot be empty, "
        + "if you need a command without a view, please extend this one,"
        + "overriding the method: #handleRequestInternal(HttpServletRequest,"
        + "HttpServletResponse, Command, BindException)");

    ModelAndView mav = new ModelAndView(viewName);

    if (!errors.hasErrors()) {
      mav.addObject(RESULT_NAME, command.execute());
    }

    mav.addObject(COMMAND_NAME, command);
    mav.addObject(RESULT_ERRORS, errors);

    return mav;
  }

  /** Binds the current command within the current request.
   * @param request the Servlet request.
   * @param response the Servlet response.
   * @param command the command instance.
   * @return the Servlet data binder.
   */
  protected ServletRequestDataBinder bindCommandToCurrentRequest(
      final HttpServletRequest request, final HttpServletResponse response,
      final Command<?> command) {
    ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(
        command, request, response, propertyMapper, propertyEditorBinder);
    dataBinder.bind(request);
    return dataBinder;
  }

  /** Retrieves the command from the current request.
   * @param request the http servlet request.
   * @return the command instance.
   */
  protected Command<?> getCommand(final HttpServletRequest request) {
    return createCommandBean();
  }

  /** Abstract method used to inject the command bean, overridden in spring.
   *
   * @return returns the command bean injected, never null.
   */
  abstract Command<?> createCommandBean();

  /** Sets the viewName to this instance.
   * @param view the viewName to set
   */
  public void setViewName(final String view) {
    viewName = view;
  }

  /** Sets the propertyMapper to this instance.
   * @param mapper the propertyMapper to set
   */
  public void setPropertyMapper(final ServletRequestPropertyMapper mapper) {
    propertyMapper = mapper;
  }

  /** Sets the propertyEditorBinder to this instance.
   * @param editors the propertyEditorBinder to set
   */
  public void setPropertyEditorBinder(
      final List<PropertyEditorBinder> editors) {
    propertyEditorBinder = editors;
  }

  /** Retrieves the view name.
   * @return the view name, default an empty string.
   */
  protected String getViewName() {
    return viewName;
  }

}
