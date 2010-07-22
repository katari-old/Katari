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

  /** {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    ModelAndView mav = super.handleRequestInternal(request, response);
    response.addHeader("Content-type", "application/x-javascript");
    return mav;
  }
}

