/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

/** Handles user logout, including redirection to a page specified in the query
 * string.
 *
 * This controller handles the url configured in acegi logout filter. It is
 * invoked after acegi has logged out. It supports an optional query parameter
 * 'service' that this controller redirects to if specified. If service is not
 * specified, it redirects to the root of the webapp.
 */
public class LogoutAndRedirectController extends AbstractController {

  /** The class logger.
   */
  private static Log log =
    LogFactory.getLog(LogoutAndRedirectController.class);

  /** Process the request and return a <code>ModelAndView</code> instance
   * describing where and how control should be forwarded after logout.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are creating.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView for the next view.
   */
  public ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    log.trace("Entering handleRequestInternal");

    ModelAndView mav = null;
    String service = request.getParameter("service");
    if (service == null) {
      service = request.getAttribute("baseweb") + "/";
    }

    mav = new ModelAndView(new RedirectView(service));

    log.trace("Leaving handleRequestInternal");
    return mav;
  }
}

