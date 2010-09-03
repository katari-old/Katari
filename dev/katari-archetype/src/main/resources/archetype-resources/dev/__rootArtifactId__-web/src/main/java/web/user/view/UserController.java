#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.web.user.view;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Initializable;

/** Spring MVC Controller to handle operations on a single user.
 *
 * This controller handles view, create, edit and delete of users. All these
 * operations have a common workflow: select a user from a list and operate on
 * that user. The only difference between these operations is the view to use
 * to complete the operation, and the command that effectively performs the
 * operation.<br>
 *
 * The steps are:<br>
 *
 * 1- Show a form with the user.<br>
 *
 * 2- Submit the form to the controller.<br>
 *
 * 3- Return to the user list.<br>
 *
 * In step 1, create, edit and view operation only differ in the ftl view they
 * use. The delete could use the step one to show a confirmation page (not done
 * here yet).<br>
 *
 * In step 1 and 2, each operation uses its own command (view, create and edit
 * reuse the same command). The view operation does not have a step 2.<br>
 */
public abstract class UserController extends SimpleFormController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(UserController.class);

  /** Default initialization for the controller.
   *
   * The list of roles provide all the existing roles in the system.
   */
  public UserController() {
    setSuccessView("redirect:users.do");
    setBindOnNewForm(true);
  }

  /** Create a reference data map for the given request.
   *
   * Prepare the user form Map. Generate a form view for a specific
   * view name, returning the Map provided there.
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
   *
   * TODO This method puts the request object in the model map. We need to
   * verify if this can be problematic, considering that some filters can add
   * the request object as a request attribute.
   */
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
    initializable.init();

    Map<String, Object> data = new  LinkedHashMap<String, Object>();
    data.put("request", request);

    log.trace("Leaving referenceData");
    return data;
  }

  /** Performs the action (delete, save, etc depending on command instance) on
   * the user.
   *
   * @param command Form object with request parameters bound onto it.
   *
   * @exception Exception if the application logic throws an exception.
   */
  protected void doSubmitAction(final Object command) throws Exception {
    log.trace("Entering doSubmitAction");

    ((Command) command).execute();

    log.trace("Leaving doSubmitAction");
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

