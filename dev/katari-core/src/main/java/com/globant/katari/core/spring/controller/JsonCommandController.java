/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.globant.katari.core.application.JsonRepresentation;
import com.globant.katari.core.application.Command;

/** Executes a Command&lt;JsonRepresentation&gt; and writes the resulting json
 * to the client.
 *
 * This sets the content type to application/json.
 *
 * The result of executing the command must not be null.
 */
public abstract class JsonCommandController extends AbstractCommandController {

  /** Writes the json object created by the command directly to the servlet
   * output.
   *
   * {@inheritDoc}
   *
   * Note: This controller always returns null, because it writes directly to
   * the response.
   */
  @Override
  @SuppressWarnings("unchecked")
  protected ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {

    Command<JsonRepresentation> jsonCommand;
    jsonCommand = (Command<JsonRepresentation>) command;
    response.addHeader("Content-type", "application/json; charset=UTF-8");
    JsonRepresentation result = jsonCommand.execute();
    if (result == null) {
      throw new RuntimeException("The result of executing the command"
          + " cannnot be null. If your command may return nullt, then you"
          + " should use another controller");
    }
    result.write(response.getWriter());

    return null;
  }

  /** Creates a command bean.
   *
   * This is intended to be implemented by spring, through getter injection.
   *
   * @return {@link Command&lt;JsonRepresentation&gt;}
   */
  protected abstract Command<JsonRepresentation> createCommandBean();

  /** {@inheritDoc}.
   */
  @Override
  protected Object getCommand(final HttpServletRequest request)
    throws Exception {
    return createCommandBean();
  }
}

