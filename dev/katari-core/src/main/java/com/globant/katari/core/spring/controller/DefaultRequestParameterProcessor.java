package com.globant.katari.core.spring.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/** This parameter processor maps the values from the client (Request) into
 * the current parameters.
 *
 * Also, allows Servlet: request & parameter overriding.
 * Example:
 *
 * The original request comes with 2 parameters:
 *
 * 1) the_name => 'the name'
 * 2) the_last_name => 'the last name'
 *
 * if this processor is initialized with the constructor with the argument:
 * <code>map(String, String)</code> (the key represents the main 'Key, and
 * the value, the replacement for that key).
 *
 * Continuing with the example, let's override those properties.
 *
 *  <code>
 *    Map<String, String> overrider = new HashMap();
 *    overrider.put("the_name", "name");
 *    overrider.put("the_last_name", "lastName");
 *    new DefaultRequestParameterProcessor(overrider);
 *  </code>
 *
 * The result of this code, will propagate a map with the values:
 *
 * 1) name => 'the name'
 * 2) lastName => 'the last name'
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class DefaultRequestParameterProcessor implements ParameterProcessor {

  /** The properties (Origin as key, destiny as value), It's never null.*/
  private final Map<String, String> properties;

  /** Creates a new instance of the request parameter definition.*/
  public DefaultRequestParameterProcessor() {
    properties = new HashMap<String, String>();
  }

  /** Creates a new instance of the request parameter definition.
   *
   * @param theProperties the properties to override.
   */
  public DefaultRequestParameterProcessor(
      final Map<String, String> theProperties) {
    Validate.notNull(theProperties, "The properties cannot be null");
    properties = theProperties;
  }

  /** {@inheritDoc}.
   *
   * Process the request and attributes parameters and add it back to the
   * given map.
   * Also, allows attribute & parameters overriding.
   *
   */
  @SuppressWarnings("unchecked")
  public void process(final HttpServletRequest request,
      final HttpServletResponse response,
      final Map<String, Object> theParameters) {

    Map<String, Object> params = new TreeMap<String, Object>();
    Enumeration<String> parameters = request.getParameterNames();
    Enumeration<String> attributes = request.getAttributeNames();

    while (parameters.hasMoreElements()) {
      String name = parameters.nextElement();
      String[] values = request.getParameterValues(name);
      String realName = resolveName(name);
      if (!ArrayUtils.isEmpty(values)) {
        if (values.length > 1) {
          params.put(realName, values);
        } else {
          params.put(realName, values[0]);
        }
      }
    }

    while(attributes.hasMoreElements()) {
      String name = attributes.nextElement();
      Object value = request.getAttribute(name);
      String realName = resolveName(name);
      params.put(realName, value);
    }

    theParameters.putAll(params);

  }

  /** Resolves the name if there are an override defined.
   * @param name the name of the request parameter to override.
   * @return the overridden name or the same value.
   */
  private String resolveName(final String name) {
    String newName = properties.get(name);
    if (newName != null) {
      return newName;
    }
    return name;
  }
}
