/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * This controller serves the javascript needed for the container js that
 * interacts with the shindig module.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class CanvasModelController extends ParameterizableViewController {

  /** The gadget debug mode, defaults to false.
   */
  private boolean debug;

  /** Constructor.
   *
   * @param newDebugMode The debug mode for the canvas. In debug mode, the
   * canvas asks the gadget container to not cache the gadget xml spec
   * processing and not compress the social javascript files.
   */
  public CanvasModelController(final boolean newDebugMode) {
    debug = newDebugMode;
  }

  /** {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    ModelAndView mav = super.handleRequestInternal(request, response);
    mav.addObject("debug", debug);
    response.addHeader("Content-type", "application/x-javascript");
    return mav;
  }
}

