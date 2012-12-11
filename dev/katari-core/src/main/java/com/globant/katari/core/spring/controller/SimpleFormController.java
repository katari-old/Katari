package com.globant.katari.core.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Initializable;
import com.globant.katari.core.application.Validatable;

/** Implementation that provides configurable form and success views,
 * and an onCommandExecute chain for convenient overriding.
 * Automatically resubmits to the form view in case of validation errors,
 * and renders the success view in case of a valid submission.
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public abstract class SimpleFormController extends
    SimpleCommandController implements InitializingBean {

  /** The form view, it's never null if it's initialized within spring. */
  private String formView;

  /** The success view rendered once the form is submitted, it's never null if
   * it's initialized within spring. */
  private String successView;

  /** Flag that double checks if this command should perform the binding
   * phase when renders its form. */
  private boolean bindOnNewForm = false;

  /** {@inheritDoc}. */
  @Override
  public ModelAndView handleRequest(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    Command<?> command = getCommand(request);

    ModelAndView mav = new ModelAndView();
    if (isFormSubmission(request)) {

      ServletRequestDataBinder binder;
      binder = bindCommandToCurrentRequest(request, response, command);
      BindException errors = new BindException(binder.getBindingResult());

      if (command instanceof Validatable) {
        ((Validatable) command).validate(errors);
      }

      if (errors.hasErrors()) {
        mav.setViewName(formView);
        mav.addObject(RESULT_ERRORS, errors);
        mav.addObject(COMMAND_NAME, command);
        return mav;
      } else {
        return onSubmit(request, response, command, errors);
      }

    } else {
      return showForm(request, response, command);
    }

  }

  /** Raise the command execution.
   * @param request the current request.
   * @param response the current response.
   * @param command the command.
   * @param errors the errors object.
   * @return the model and view.
   */
  protected ModelAndView onSubmit(final HttpServletRequest request,
      final HttpServletResponse response, final Command<?> command,
      final BindException errors) {
    Object result = onCommandExecute(command, request, response);
    ModelAndView mav = new ModelAndView(getSuccessView(request));
    mav.addObject(RESULT_ERRORS, errors);
    mav.addObject(COMMAND_NAME, command);
    mav.addObject(RESULT_NAME, result);
    return mav;
  }

  /** Performs the execution of the command.
   * @param command the command to execute.
   * @param request the current request.
   * @param response the current response.
   * @return the object result from the command execution.
   */
  protected Object onCommandExecute(final Command<?> command,
      final HttpServletRequest request, final HttpServletResponse response) {
    return command.execute();
  }

  /** Renders the form.
   *
   * If the command implements the interface Initializable will call the method
   * init, also if this controller is bindeable on new form, will bind
   * the current request to the command instance.
   *
   * @param request the current request.
   * @param response the current response.
   * @param command the command bean.
   * @return the model and view.
   */
  protected ModelAndView showForm(final HttpServletRequest request,
      final HttpServletResponse response, final Command<?> command) {
    ModelAndView mav = new ModelAndView(getFormView(request));

    if (command instanceof Initializable) {
      ((Initializable) command).init();
    }

    if (bindOnNewForm) {
      ServletRequestDataBinder binder;
      binder = bindCommandToCurrentRequest(request, response, command);
      BindException errors = new BindException(binder.getBindingResult());
      mav.addObject(RESULT_ERRORS, errors);
    }

    mav.addObject(COMMAND_NAME, command);
    return mav;
  }

  /** Determine if the given request represents a form submission.
   * <p>The default implementation treats a POST request as form submission.
   * Note: If the form session attribute doesn't exist when using session form
   * mode, the request is always treated as new form by handleRequestInternal.
   * <p>Subclasses can override this to use a custom strategy, e.g. a specific
   * request parameter (assumably a hidden field or submit button name).
   * @param request current HTTP request
   * @return if the request represents a form submission
   */
  protected boolean isFormSubmission(final HttpServletRequest request) {
    return METHOD_POST.equals(request.getMethod());
  }

  /** Sets the bind on new form to this instance.
   * @param bind checks if this command should bind on new form or not.
   */
  public void setBindOnNewForm(final boolean bind) {
    bindOnNewForm = bind;
  }

  /** Sets the form view to this instance.
   * @param formViewName the name of the view that represents the form.
   */
  public void setFormView(final String formViewName) {
    Validate.isTrue(formView == null,
        "Cannot change the reference to the form view");
    formView = formViewName;
  }

  /** Sets the success view to this instance.
   * @param successViewName the success view to render once the form
   *  is submitted.
   */
  public void setSuccessView(final String successViewName) {
    Validate.isTrue(successView == null,
        "Cannot change the reference to the success view");
    successView = successViewName;
  }

  /** {@inheritDoc}. */
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(successView, "The success view cannot be null");
    Validate.notNull(formView, "The form view cannot be null");
  }

  /** Retrieves the form view.
   * @return the form view.
   */
  public String getFormView(final HttpServletRequest request) {
    return formView;
  }

  /** Retrieves the success view.
   * @return the success view.
   */
  public String getSuccessView(final HttpServletRequest request) {
    return successView;
  }

}
