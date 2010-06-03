/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Initializable;
import com.globant.katari.editablepages.application.ShowPageCommand;

/** Spring MVC Controller to handle operations on a page.
 *
 * This controller handles view, preview, create, edit and delete of pages. All
 * these operations have a common workflow: select a page from and operate on
 * that page. The only difference between these operations is the view to use
 * to complete the operation, and the command that effectively performs the
 * operation.<br>
 *
 * The steps are:<br>
 *
 * 1- Show a form (or html) with the page.<br>
 *
 * 2- Submit the form to the controller.<br>
 *
 * 3- Return to the original page.<br>
 *
 * In step 1, create, edit and view operation only differ in the ftl view they
 * use. The delete could use the step one to show a confirmation page (not done
 * here yet).<br>
 *
 * In step 1 and 2, each operation use its own command (preview, create and
 * edit reuse the same command). The view operation does not have a step 2.<br>
 *
 * The command must be of type <code>Command&lt;String&gt;</code>, that implies
 * that execute must return a string. This controller uses the returned string
 * as the name of the page to go after saving or cancelling the operation.
 *
 * The page view supported by this controller is used only to show an editable
 * page from a weblet. Normal page view is done with a different controller
 * (PageController).
 */
public abstract class PageEditController extends SimpleFormController {

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(PageEditController.class);

  /** The FCKEditor configuration.
   *
   * Defines the configuration js location, toolbar, width and height.
   */
  private FckEditorConfiguration fckEditorConfiguration;

  /** Defines what http request method is considered a form submission.
   *
   * If null, then leave spring default of considering a POST a form
   * submission.
   */
  private String submitMethod = null;

  /** Default initialization for the controller.
   *
   * @param fckConfiguration The fck configuration object. This is passed to
   * the edit page to configure FCKEditor. It cannot be null.
   */
  public PageEditController(final FckEditorConfiguration fckConfiguration) {
    Validate.notNull(fckConfiguration,
        "The FCKEditor configuration cannot be null");
    setSuccessView("redirect:pages.do");
    setBindOnNewForm(true);
    fckEditorConfiguration = fckConfiguration;
  }

  /** Create a reference data map for the given request.
   *
   * Initializes the command loading the corresponding page from the
   * repository.
   *
   * This controller is also responisible for rendering weblets. The editable
   * pages weblets need an id for some html tags. This controller also provides
   * these ids to the weblets. It uses the request object to hold an elementId
   * attribute, an integer that is incremented on every use. This element id is
   * only meaningful during the rendering of the views. It is exposed to the
   * view as elementId.
   *
   * This controller also puts the FCKEditor configuration in the model map
   * under the key fckEditorConfiguration.
   *
   * @param request The HTTP request we are processing.
   *
   * @param command The command object with the bound parameters. It cannot be
   * null.
   *
   * @param errors The errors holder. It cannot be null.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the Map for the form view.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Map referenceData(final HttpServletRequest request, final Object
      command, final Errors errors) throws Exception {
    log.trace("Entering referenceData");

    Validate.notNull(request, "The request cannot be null");
    Validate.notNull(command, "The command cannot be null");
    Validate.notNull(errors, "The errors cannot be null");

    if (!(command instanceof Initializable)) {
      throw new IllegalArgumentException("The provided command does not"
          + " implement Initializable");
    }
    Initializable initializable = (Initializable) command;

    /* If the controller is rendering a weblet, this initializes the
     * ShowPageCommand from the weblet instance.
     *
     * This instance is the name of the page to show.
     *
     * This is a nasty hack: spring does not bind request attributes.
     *
     * TODO See if it is possible to use a binder that binds request attributes.
     */
    Object instance = request.getAttribute("instance");
    if (instance != null && command instanceof ShowPageCommand) {
      ((ShowPageCommand) command).setInstance((String) instance);
    }

    initializable.init();

    Map<String, Object> data = new  LinkedHashMap<String, Object>();
    data.put("fckEditorConfiguration", fckEditorConfiguration);
    data.put("request", request);
    data.put("baseweb", request.getAttribute("baseweb"));

    // Increments the elementId from the request.
    Long elementId = (Long) request.getAttribute("elementId");
    if (elementId == null) {
      elementId = Long.valueOf(1);
    } else {
      elementId++;
    }
    request.setAttribute("elementId", elementId);

    data.put("elementId", elementId);

    log.trace("Leaving referenceData");
    return data;
  }

  /** Performs the operation on the page through the command.
   *
   * @param command Form object with request parameters bound onto it.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return Returns the model and view to render the following page. The view
   * name is obtained from the command. If null, then the view will be the
   * success view of this controller.
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView onSubmit(final Object command) throws Exception {
    log.trace("Entering onSubmit");
    String targetPage = ((Command<String>) command).execute();
    ModelAndView mav = null;
    if (targetPage != null) {
      log.debug("Redirecting to {}{}", "../page/", targetPage);
      mav = new ModelAndView("redirect:../page/" + targetPage);
    }
    log.trace("Leaving onSubmit");
    return mav;
  }

  /** Sets the method to perform a form submission.
   *
   * @param method the http request method (ej: GET, POST). It cannot be null.
   */
  public void setSubmitMethod(final String method) {
    submitMethod = method;
  }

  /** Decides if the form is submited based on the request method.
   *
   * Set the method to consider a submission with setSubmitMethod.
   *
   * {@inheritDoc}
   *
   * @return true if the request method matches what was specified with
   * setSubmitMethod.
   */
  protected boolean isFormSubmission(final HttpServletRequest request) {
    if (submitMethod == null) {
      return super.isFormSubmission(request);
    } else  if (submitMethod.equals(request.getMethod())) {
      return true;
    }
    return false;
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

