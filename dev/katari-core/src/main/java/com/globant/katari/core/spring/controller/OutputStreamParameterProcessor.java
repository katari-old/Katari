package com.globant.katari.core.spring.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Retrieves from the response the output stream and store it into the
 * request parameter called: outputStream.
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class OutputStreamParameterProcessor implements ParameterProcessor {

  /** The parameter name.*/
  private static final String PARAMETER_NAME = "outputStream";

  /** {@inheritDoc}.*/
  public void process(final HttpServletRequest request,
      final HttpServletResponse response,
      final Map<String, Object> parameters) {
    try {
      parameters.put(PARAMETER_NAME, response.getOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
