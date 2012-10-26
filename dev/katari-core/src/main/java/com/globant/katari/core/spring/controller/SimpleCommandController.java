package com.globant.katari.core.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.apache.commons.lang.Validate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

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
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public abstract class SimpleCommandController extends AbstractController {

  /** The class logger.*/
  private static Logger log = LoggerFactory
      .getLogger(SimpleCommandController.class);

  /** The name of the key of the result for this view.*/
  private static final String RESULT_NAME = "result";

  /** The name of the key of the errors for this view.*/
  private static final String RESULT_ERRORS = "errors";

  /** The name of the key of the command for this view.*/
  private static final String COMMAND_NAME = "command";

  /** The name of the view to render, ot's never null.*/
  private final String viewName;

  /** The property mapper, it's never null. */
  private final ServletRequestPropertyMapper propertyMapper;

  /** The list of property editor factory, can be null.*/
  private List<PropertyEditorBinder> propertyEditorBinder;

  /** Default constructor, for commands that do not needs a view to render.*/
  public SimpleCommandController() {
    viewName = "";
    propertyMapper = new ServletRequestPropertyMapper();
  }

  /** Creates a new instance of the Command Controller.
   *
   * @param view the view name to render. Cannot be null.
   */
  public SimpleCommandController(final String view) {
    Validate.notNull(view, "The view name cannot be null");
    viewName = view;
    propertyMapper = new ServletRequestPropertyMapper();
  }

  /** Creates a new instance of the Command Controller.
  *
  * @param view the view name to render. Cannot be null.
  * @param mapper the property editor mapper. Cannot be null.
  */
  public SimpleCommandController(final String view,
      final ServletRequestPropertyMapper mapper) {
    Validate.notNull(view, "The view name cannot be null");
    Validate.notNull(mapper, "The property mapper cannot be null");
    viewName = view;
    propertyMapper = mapper;
  }

  /** Creates a new instance of the Command Controller. This constructor _MUST_
   * be used for operations that does not need a view, the ones that writes
   * directly to the response.
   *
   * @param factory the factory. Cannot be null.
   * @param mapper the property editor mapper. Cannot be null.
   */
  public SimpleCommandController(final List<PropertyEditorBinder> factory,
     final ServletRequestPropertyMapper mapper) {
    Validate.notNull(mapper, "The property mapper cannot be null");
    Validate.notNull(factory, "The property editor binder cannot be null");
    propertyEditorBinder = factory;
    propertyMapper = mapper;
    viewName = "";
  }

  /** Creates a new instance of the Command Controller.
   *
   * @param view the view name to render. Cannot be null.
   * @param factory the factory. Cannot be null.
   */
  public SimpleCommandController(final String view,
      final List<PropertyEditorBinder> factory) {
    Validate.notNull(factory, "The property editor binder cannot be null");
    Validate.notNull(view, "The view name cannot be null");
    propertyEditorBinder = factory;
    propertyMapper = new ServletRequestPropertyMapper();
    viewName = view;
  }

  /** Creates a new instance of the Command Controller. This constructor _MUST_
   * be used for operations that does not need a view, the ones that writes
   * directly to the response.
   *
   * @param view the view name to render. Cannot be null.
   * @param factory the factory. Cannot be null.
   * @param mapper the property editor mapper. Cannot be null.
   */
  public SimpleCommandController(final String view,
      final List<PropertyEditorBinder> factory,
     final ServletRequestPropertyMapper mapper) {
    Validate.notNull(view, "The view name cannot be null");
    Validate.notNull(mapper, "The property mapper cannot be null");
    Validate.notNull(factory, "The property editor binder cannot be null");
    propertyEditorBinder = factory;
    propertyMapper = mapper;
    viewName = view;
  }

  /** {@inheritDoc}.*/
  @Override
  protected ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    log.trace("Entering handleRequestInternal");

    Command<?> command = createCommandBean();
    ServletRequestDataBinder binder = createAndBind(request, response, command);
    BindException errors = new BindException(binder.getBindingResult());

    if (command instanceof Validatable) {
      ((Validatable) command).validate(errors);
    }

    /*
     * TODO [waabox] support initializable?
     *
     * TODO [waabox] checks if we need to verify the headers to
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
  private ServletRequestDataBinder createAndBind(
      final HttpServletRequest request, final HttpServletResponse response,
      final Command<?> command) {
    ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(
        command, request, response, propertyMapper, propertyEditorBinder);
    dataBinder.bind(request);
    return dataBinder;
  }

  /** Abstract method used to inject the command bean, overriden in spring.
   *
   * @return returns the command bean injected, never null.
   */
  protected abstract Command<?> createCommandBean();

}
