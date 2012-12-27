package com.globant.katari.core.spring.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/** Main class that holds the list of parameter processors.
 * This class brings back to spring the result of the execution of each
 * parameter processor.
 *
 * If no parameter given, by default, this class executes the
 * DefaultRequestParameterProcessor.
 *
 * @see com.globant.katari.core.spring.controller
 *  .DefaultRequestParameterProcessor
 *
 * @see com.globant.katari.core.spring.controller.ParameterProcessor
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class ServletRequestPropertyMapper {

  /** Maps properties with the result of the execution of the mapper.*/
  private final List<ParameterProcessor> parameterProcessor;

  /** Creates a new instance of the mapper.*/
  public ServletRequestPropertyMapper() {
    parameterProcessor = new LinkedList<ParameterProcessor>();
    parameterProcessor.add(new DefaultRequestParameterProcessor());
    parameterProcessor.add(new ReferenceDataParameterProcessor());
  }

  /** Creates a new instance of the mapper.
   *
   * @param definitions the parameter definition list.
   */
  public ServletRequestPropertyMapper(
      final List<ParameterProcessor> definitions) {
    Validate.notNull(definitions, "The parameter definition cannot be null");
    Validate.isTrue(!definitions.isEmpty(),
        "The parameter definition cannot be empty");
    parameterProcessor = definitions;
  }

  /** Creates the key value property for the request parameters and attributes.
   * @param request the Servlet request.
   * @param response the Servlet response.
   * @return the map with the parameters/attributes with its values.
   */
  public Map<String, Object> createPropertyMap(final HttpServletRequest request,
      final HttpServletResponse response) {
    Map<String, Object> parameters = new TreeMap<String, Object>();
    for (ParameterProcessor definition : parameterProcessor) {
      definition.process(request, response, parameters);
    }
    return parameters;
  }
}
