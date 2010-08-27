/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import org.apache.commons.lang.Validate;

import com.globant.katari.core.application.Command;

/** Executes a command and forwards the output to the configured view.
 *
 * This controller creates a model object with the command under the 'command'
 * key and the result of execute under the 'result' key.
 */
public abstract class ViewCommandController extends AbstractCommandController {

  /** The view that will render the request.
   *
   * It must be set to something valid before calling handle.
   */
  private String viewName;

  /** Executes the command and forwards to the configured view to show the
   * result.
   *
   * You must set the view name before calling this operation.
   * 
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  protected ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object theCommand,
      final BindException errors) throws Exception {

    Validate.notNull(viewName, "Call setViewName before handle");

    ModelAndView mav = new ModelAndView(viewName);

    Command command = (Command) theCommand;
    mav.addObject("command", command);
    mav.addObject("result", command.execute());

    return mav;
  }

  /** Sets the name of the view to generate the response of this command.
   *
   * @param targetView the name of the target view. It cannot be null.
   */
  public void setViewName(final String targetView) {
    Validate.notNull(targetView, "the target view name cannot be null");
    viewName = targetView;
  }

  /** Obtains a command object.
   *
   * This operation is implemented by method injection in spring.
   *
   * @return a new instance of a Command object, never null.
   */
  protected abstract Command<?> createCommandBean();

  /** {@inheritDoc}
   */
  @Override
  protected Object getCommand(final HttpServletRequest request)
    throws Exception {
    return createCommandBean();
  }
}

