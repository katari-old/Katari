/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/** A ParameterizableViewController that also exposes the request object and
 * the baseweb attributes for the page.
 */
public class RequestModelViewController extends ParameterizableViewController {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      RequestModelViewController.class);

  /** Creates a model with the request object and forwards to the parameterized
   * view.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are creating.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView that contains the specified view and the request
   * in the model, never null.
   */
  protected final ModelAndView handleRequestInternal(final HttpServletRequest
      request, final HttpServletResponse response) throws Exception {
    log.trace("Entering handleRequestInternal");

    ModelAndView mav = new ModelAndView(getViewName());
    mav.addObject("request", request);
    mav.addObject("baseweb", request.getAttribute("baseweb"));

    log.trace("Leaving handleRequestInternal");
    return mav;
  }
}

