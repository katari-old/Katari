/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;

import org.acegisecurity.ui.WebAuthenticationDetails;

/** Provides the full request object to the client of this authentication
 * details.
 *
 * This is used to obtain request attributes and urls.
 */
public class FullWebAuthenticationDetails extends WebAuthenticationDetails {

  /** The serial version for this serializable class.
   */
  private static final long serialVersionUID = 1L;

  /** The request.
   *
   * This is never null.
   */
  private HttpServletRequest request;

  /** Creates a new FullWebAuthenticationDetails.
   *
   * @param theRequest The request. It cannot be null.
   */
  public FullWebAuthenticationDetails(final HttpServletRequest theRequest) {
    super(theRequest);
    Validate.notNull(theRequest, "The request cannot be null");
    request = theRequest;
  }

  /** Returns the request.
   *
   * @return the request object. It never returns null.
   */
  public HttpServletRequest getRequest() {
    return request;
  }
}

