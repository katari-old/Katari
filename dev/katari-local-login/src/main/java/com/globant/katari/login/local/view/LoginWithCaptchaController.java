/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/** Shows the login page with the captcha.
 */
public class LoginWithCaptchaController extends AbstractController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(LoginWithCaptchaController.class);

  /** Adds a showCaptcha and redirects to the localLoginView.
   *
   * The localLoginView is intended to show the captcha image with the
   * corresponding input.
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
  public ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    log.trace("Entering handleRequestInternal");

    ModelAndView mav = new ModelAndView("localLoginView");
    mav.addObject("showCaptcha", true);

    log.trace("Leaving handleRequestInternal");
    return mav;
  }
}

