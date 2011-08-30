/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/** A DispatcherServlet that removes the, in my opinion, broken exception
 * handling in spring.
 */
public class DispatcherServlet extends
    org.springframework.web.servlet.DispatcherServlet {

  /** The serial version id.
   */
  private static final long serialVersionUID = 1L;

  /** {@inheritDoc}
   *
   * Rethrows the exception so that a filter (or the container) can handle it.
   */
  protected ModelAndView processHandlerException(
      final HttpServletRequest request, final HttpServletResponse response,
      final Object handler, final Exception ex) throws Exception {
    throw ex;
  }
}

