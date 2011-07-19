/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.registration.view;

import javax.servlet.http.HttpServletRequest;

import com.globant.katari.core.spring.controller.SimpleFormCommandController;

/** Controller that accepts a reset password request with token.
 *
 * If this controller finds the token as a request parameter, it assumes the
 * request is a form submission.
 */
public abstract class ResetPasswordController
  extends SimpleFormCommandController {

  /** Returns true if the request has a token parameter.
   *
   * {@inheritDoc}
   */
  protected boolean isFormSubmission(final HttpServletRequest request) {
    if (request.getParameter("token") != null) {
      return true;
    } else {
      return false;
    }
  }
}

