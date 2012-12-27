package com.globant.katari.core.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

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

  /** The servlet request parameter that holds the reference data.*/
  public static final String REFERENCE_DATA_PARAMETER = "katari-referenceData";

  /** The form view, it's never null if it's initialized within spring. */
  private String formView;

  /** The success view rendered once the form is submitted, it's never null if
   * it's initialized within spring. */
  private String successView;

  /** Flag that double checks if this command should perform the binding
   * phase when renders its form. */
  private boolean bindOnNewForm = false;

  /** Set to true if the successView must be evaluated as el expression.*/
  private boolean successViewUsesEl = false;

  /** {@inheritDoc}. */
  public ModelAndView handleRequest(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    Command<?> command = getCommand(request);
    Initializable referenceData = createReferenceDataBean(request);

    bindReferenceData(request, referenceData);

    ServletRequestDataBinder binder = null;

    if (mustBind(request)) {
      binder = bindCommandToCurrentRequest(request, response, command);
    }

    ModelAndView mav = new ModelAndView();
    if (isFormSubmission(request)) {
      BindException errors = new BindException(binder.getBindingResult());
      if (command instanceof Validatable) {
        ((Validatable) command).validate(errors);
      }
      if (errors.hasErrors()) {
        mav.setViewName(formView);
        mav.addObject(RESULT_ERRORS, errors);
        mav.addObject(COMMAND_NAME, command);
        attachReferenceData(referenceData, mav);
        return mav;
      } else {
        return onSubmit(request, response, command, errors);
      }
    } else {
      return showForm(request, response, command, referenceData);
    }
  }

  /** Binds the reference data to the current request.
   * @param request the current HTTP servlet request.
   * @param referenceData the reference data object.
   */
  private void bindReferenceData(final HttpServletRequest request,
      final Initializable referenceData) {
    if (referenceData != null) {
      // Create a binder for the reference data, and bind it.
      org.springframework.web.bind.ServletRequestDataBinder binder;
      binder = new org.springframework.web.bind.ServletRequestDataBinder(
          referenceData, "referenceData");
      binder.bind(request);
      referenceData.init();
    }
    request.setAttribute(REFERENCE_DATA_PARAMETER, referenceData);
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
    Object result = command.execute();
    String view = calculateSuccessView(command, request);
    ModelAndView mav = new ModelAndView(view);
    mav.addObject(RESULT_ERRORS, errors);
    mav.addObject(COMMAND_NAME, command);
    mav.addObject(RESULT_NAME, result);
    return mav;
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
      final HttpServletResponse response, final Command<?> command,
      final Initializable referenceData) {
    ModelAndView mav = new ModelAndView(getFormView(request));
    attachReferenceData(referenceData, mav);
    if (command instanceof Initializable) {
      ((Initializable) command).init();
    }
    mav.addObject(COMMAND_NAME, command);
    return mav;
  }

  /** Attaches the reference data to the given model and view.
   * @param referenceData the reference data.
   * @param mav the model and view.
   */
  private void attachReferenceData(final Initializable referenceData,
      final ModelAndView mav) {
    if (referenceData != null) {
      mav.addObject("referenceData", referenceData);
    }
  }

  /** Calculates the success view, based on successViewUsesEl and using the
   * provided command as EL model.
   *
   * Use the success view unchanged if successViewUsesEl is false.
   *
   * @param command the command.
   *
   * @return the succes view
   */
  private String calculateSuccessView(final Command<?> command,
      final HttpServletRequest request) {

    String mainView = getSuccessView(request);

    if (successViewUsesEl) {
      ExpressionFactory factory = new ExpressionFactoryImpl();
      SimpleContext context = new SimpleContext();
      ValueExpression commandExpression;
      commandExpression = factory.createValueExpression(command, Command.class);
      context.setVariable("command", commandExpression);
      ValueExpression result;
      result = factory.createValueExpression(context, mainView, String.class);
      return (String) result.getValue(context);
    } else {
      return mainView;
    }
  }

  /** Checks if should bind or not the current request to the command.
   * @return true if should bind.
   */
  private boolean mustBind(final HttpServletRequest request) {
    return isFormSubmission(request) || bindOnNewForm;
  }

  /** Determine if the given request represents a form submission.
   * The default implementation treats a POST request as form submission.
   * @param request current HTTP request
   * @return true if the request represents a form submission
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

  /** Defines if the successView is treated as an EL expression.
   * @param useEl true if the successView is evaluated as an EL expression,
   * false if the successView is used as is.
   */
  public void setSuccessViewUsesEl(final boolean useEl) {
    successViewUsesEl = useEl;
  }

  /** This method must be injected with the reference data, an instance of an
   * object that implements Initializable.
   * @param request the current HTTP servlet request.
   * @return an instance of the reference data.
   */
  abstract Initializable createReferenceDataBean(
      final HttpServletRequest request);

}
