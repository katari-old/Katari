/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;

import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import com.globant.katari.core.application.Command;

/** A simple command controller that executes the command when handling the
 * request.
 *
 * This controller should be configured in spring to inject a prototype
 * instance of a Katari's command.
 *
 * When the request is handled, this controller executes the command without
 * any validation and forwards to the provided view.
 *
 * The view has a model object with 'result' as the value returned by
 * Command.execute() and 'command' as the value of the command object.
 */
public abstract class CommandController extends AbstractCommandController {

  /** The name of the key of the result object when bound to the view. */
  private static final String RESULT_NAME = "result";

  /** The name of the key of the command object when bound to the view. */
  private static final String COMMAND_NAME = "command";

  /** The name of the view to be rendered, never null nor empty. */
  private String viewName;

  /** Default initialization for the controller.
   *
   * @param theViewName Specifies the view that this controller will render.
   * It cannot be null or empty.
   */
  public CommandController(final String theViewName) {
    Validate.notEmpty(theViewName, "The view name cannot be null or empty.");
    viewName = theViewName;
  }

  /** Process the request and return a <code>ModelAndView</code> with keys
   * 'result' and 'command', pointing to the view passed as parameter in the
   * constructor.
   *
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) {
    Object result = ((Command) command).execute();
    ModelAndView mav = new ModelAndView(viewName);
    mav.addObject(RESULT_NAME, result);
    mav.addObject(COMMAND_NAME, command);
    return mav;
  }

  /** Abstract method used to inject the command bean, overriden in spring.
   *
   * @return Returns the command bean injected, never null.
   */
  protected abstract Object createCommandBean();

  /** Triggers the creation of the command object through the abstract method,
   * overriding the default behavior of creating it using binutils.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object getCommand(final HttpServletRequest request)
      throws Exception {
    return createCommandBean();
  }
}

