/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/** Shows the login page with an optional captcha image.
 */
public class LoginWithCaptchaController extends AbstractController {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      LoginWithCaptchaController.class);

  /** Indicates if the login page will show the captcha image.
   */
  private boolean showCaptcha = false;

  /** Additional buttons to show in the login page, at the right of the current
   * buttons.
   *
   * Each element of the list is a string of the form [button label] | [context
   * relative url].
   *
   * This is never null.
   */
  private List<String> additionalButtons = new LinkedList<String>();

  /** Creates a controller.
   *
   * @param mustShowCaptcha true if the view must show the captcha image.
   *
   * @param buttons the list of additional buttons to show. Each entry is of
   * the form [button label] | [context relative url]. If null, no additional
   * buttons are added.
   */
  public LoginWithCaptchaController(final boolean mustShowCaptcha,
      final List<String> buttons) {
    showCaptcha = mustShowCaptcha;
    if (buttons != null) {
      additionalButtons = buttons;
    }
  }

  /** Shows the login page, with an optional captcha image and additional
   * buttons.
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
   * @return the ModelAndView for the next view. The model object includes
   * showCaptcha (true or false) to show or hide the captcha image, and
   * additionalButtons, a list of strings with name and link of additional
   * buttons to show on the page. It never returns null.
   */
  @Override
  public ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    log.trace("Entering handleRequestInternal");

    ModelAndView mav = new ModelAndView("localLoginView");
    mav.addObject("showCaptcha", showCaptcha);
    mav.addObject("additionalButtons", additionalButtons);

    log.trace("Leaving handleRequestInternal");
    return mav;
  }
}

