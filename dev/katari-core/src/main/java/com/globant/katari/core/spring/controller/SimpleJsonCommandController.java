package com.globant.katari.core.spring.controller;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.JsonRepresentation;
import com.globant.katari.core.application.Command;

/** Executes a Command&lt;JsonRepresentation&gt; and writes the resulting JSON
 * to the client.
 *
 * This sets the content type to application/json.
 *
 * The result of executing the command must not be null.
 */
public abstract class SimpleJsonCommandController extends
    SimpleCommandController {

  /** Creates a new instance of the JSON command controller.
   *
   * @param command the command.
   */
  public SimpleJsonCommandController(final String command) {
    super(command);
  }

  /** Creates a new instance of the JSON command controller.
   *
   * @param command the command.
   * @param thePropertyEditorFactory the property editor factory.
   */
  public SimpleJsonCommandController(final String command,
      final List<PropertyEditorBinder> thePropertyEditorFactory) {
    super(command, thePropertyEditorFactory);
  }

  /** {@inheritDoc}.
   *  Writes the JSON object created by the command directly to the Servlet
   * output.
   *
   * NOTE: This controller always returns null, because it writes directly to
   * the response.
   */
  @Override
  @SuppressWarnings("unchecked")
  protected ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response, final Command<?> command,
      final BindException errors) throws Exception {
    response.addHeader("Content-type", "application/json; charset=UTF-8");
    Command<JsonRepresentation> jsonCommand;
    if (errors.hasErrors()) {
      List<ObjectError> theErrors = errors.getAllErrors();
      JSONArray jsonErrorsArray = new JSONArray();
      for (ObjectError error : theErrors) {
        JSONObject jsonError = new JSONObject();
        jsonError.put("code", error.getCode());
        jsonError.put("defaultMessage", error.getDefaultMessage());
        if (theErrors instanceof FieldError) {
          jsonError.put("fieldName", ((FieldError) error).getField());
        }
        jsonErrorsArray.put(jsonError);
      }
      jsonErrorsArray.write(response.getWriter());
      return null;
    }
    jsonCommand = (Command<JsonRepresentation>) command;
    JsonRepresentation result = jsonCommand.execute();
    if (result == null) {
      throw new RuntimeException("The result of executing the command"
          + " cannnot be null. If your command may return null, then you"
          + " should use another controller");
    }
    Writer writer = response.getWriter();
    result.write(writer);
    IOUtils.closeQuietly(writer);
    return null;
  }
}
