package com.globant.katari.core.spring.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/** The parameter definition interfaces allows to modify parameters within
 * a Servlet context.
 *
 * The typical scenario is: add new parameters into the current request.
 * For example: infrastructure support, such as output streams, cookies, etc.
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public interface ParameterProcessor {

  /** This method allows to redefine, change, remove or any kind of operation
   * related to the parameters received in the parameters map.
   *
   * @param request the HTTP Servlet request, cannot be null.
   * @param response the HTTP Servlet response, cannot be null.
   * @param parameters the map of parameters, cannot be null.
   */
  void process(final HttpServletRequest request,
      final HttpServletResponse response, final Map<String, Object> parameters);

}
