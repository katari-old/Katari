/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.home.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/** Spring MVC controller to show users.
 *
 * Subclasses need to override <code>createCommandBean</code> to retrieve
 * a backing object for the current form. Use method injection to override
 * <code>createCommandBean</code>.
 */
public class HomeController extends AbstractController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(HomeController.class);

  /** Process the request and return a <code>ModelAndView</code> instance
   * describing where and how control should be forwarded.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are creating.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView for the next view.
   */
  @Override
  protected ModelAndView handleRequestInternal(final HttpServletRequest
      request, final HttpServletResponse response) throws Exception {
    log.trace("Entering handleRequestInternal");

    ModelAndView mav = new ModelAndView("home");

    log.trace("Leaving handleRequestInternal");
    return mav;
  }
}

