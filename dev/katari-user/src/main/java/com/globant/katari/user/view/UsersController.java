/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.view;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.globant.katari.hibernate.coreuser.SecurityUtils;

import com.globant.katari.user.application.UserFilterCommand;
import com.globant.katari.user.domain.User;

/** Spring MVC controller to show users.
 *
 * Subclasses need to override <code>createCommandBean</code> to retrieve
 * a backing object for the current form. Use method injection to override
 * <code>createCommandBean</code>.
 */
public abstract class UsersController extends AbstractCommandController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(UsersController.class);

  /** Default initialization for the controller.
   */
  public UsersController() {
    setCommandName("userFilter");
  }

  /** Process the request and return a <code>ModelAndView</code> instance
   * describing where and how control should be forwarded.
   *
   * Populate the ModelAndView model with the command under the specified
   * command name, as expected by the "spring:bind" tag.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are creating.
   *
   * @param command The populated command object.
   *
   * @param error Validation errors holder.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView for the next view.
   */
  protected final ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException error) throws Exception {
    log.trace("Entering handleRequestInternal");

    UserFilterCommand userFilterCommand = (UserFilterCommand) command;
    List<User> users = userFilterCommand.execute();

    ModelAndView mav = new ModelAndView("users");
    mav.addObject("users", users);
    // Only used to decide if we will show the delete button for a user.
    mav.addObject("currentUserId", SecurityUtils.getCurrentUser().getId());
    mav.addObject("totalPageNumber",
        userFilterCommand.getPaging().getTotalPageNumber());
    mav.addObject("request", request);
    mav.addObject("message", "");
    mav.addObject("userFilter", userFilterCommand);

    // Sets the urls for pagination.
    mav.addObject("nextPage", userFilterCommand.getUrlNextPage());
    mav.addObject("previousPage", userFilterCommand.getUrlPrevPage());

    // Sets the url for sorting.
    mav.addObject("order", userFilterCommand.getUrlOrder());

    log.trace("Leaving handleRequestInternal");
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
  protected Object getCommand(final HttpServletRequest request)
      throws Exception {
    return createCommandBean();
  }

  /** This method is injected by AOP.
   *
   * @return Returns the command bean injected.
   */
  protected abstract UserFilterCommand createCommandBean();
}

