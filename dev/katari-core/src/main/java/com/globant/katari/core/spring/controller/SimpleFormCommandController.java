package com.globant.katari.core.spring.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.globant.katari.core.application.Command;

/** A simple SimpleFormController that performs the command execution.
 *
 * This controller should be configured in spring to inject a prototype
 * instance of a command.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public abstract class SimpleFormCommandController extends SimpleFormController {

  /** The name of the key of the result object when bound to the view. */
  private static final String RESULT_NAME = "result";

  /** The name of the key of the command object when bound to the view. */
  private static final String COMMAND_NAME = "command";

  /** Process the request and return a <code>ModelAndView</code> with keys
   * 'result' and 'command'.
   *
   *{@inheritDoc}
   *
   */
  @Override
  protected ModelAndView onSubmit(final Object command,
      final BindException errors) throws Exception {
    Command<?> cmd = (Command<?>) command;
    Object result = cmd.execute();
    ModelAndView mav = super.onSubmit(cmd, errors);
    mav.addObject(RESULT_NAME, result);
    mav.addObject(COMMAND_NAME, cmd);
    return mav;
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

  /** Returns the command object to post and save the user.
   *
   * This method is injected by AOP.
   *
   * @return Returns the command bean injected.
   */
  protected abstract Object createCommandBean();

}
