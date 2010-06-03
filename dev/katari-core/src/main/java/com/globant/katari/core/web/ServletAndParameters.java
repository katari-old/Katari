/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.Validate;

/** Class that holds a servlet and its initialization parameters.
 *
 * The parameters consists of name/value pairs that are used to initialize the
 * servlet.
 */
public final class ServletAndParameters {

  /** The servlet that is being configured.
   *
   * It is never null.
   */
  private HttpServlet servlet;

  /** The parameters to configure the servlet.
   *
   * It is never null.
   */
  private Map<String, String> parameters;

  /** Builds a configured servlet instance with no parameters.
   *
   * @param theServlet The servlet being configured. It cannot be null.
   */
  public ServletAndParameters(final HttpServlet theServlet) {
    this (theServlet, new HashMap<String, String>());
  }

  /** Builds a configured servlet instance.
   *
   * @param theServlet The servlet being configured. It cannot be null.
   *
   * @param theParameters The parameters to initialize the servlet. It cannot
   * be null.
   */
  public ServletAndParameters(final HttpServlet theServlet,
      final Map<String, String> theParameters) {
    Validate.notNull(theServlet, "The servlet cannot not be null");
    Validate.notNull(theParameters, "The parameters cannot not be null");
    servlet = theServlet;
    parameters = theParameters;
  }

  /** The servlet being configured.
   *
   * @return Returns the contained servlet. Never returns null.
   */
  public HttpServlet getServlet() {
    return servlet;
  }

  /** The parameters to configure the servlet.
   *
   * @return Returns the parameters. Never returns null.
   */
  public Map<String, String> getParameters() {
    return parameters;
  }
}

