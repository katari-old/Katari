/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.Filter;

import org.apache.commons.lang.Validate;

/** Class that holds a filter and its initialization parameters.
 *
 * The parameters consists of name/value pairs that are used to initialize the
 * filter.
 */
public final class FilterAndParameters {

  /** The filter that is being configured.
   *
   * It is never null.
   */
  private Filter filter;

  /** The parameters to configure the filter.
   *
   * It is never null.
   */
  private Map<String, String> parameters;

  /** Builds a configured filter instance with no parameters.
   *
   * @param theFilter The filter being configured. It cannot be null.
   */
  public FilterAndParameters(final Filter theFilter) {
    Validate.notNull(theFilter, "The filter cannot not be null");
    filter = theFilter;
    parameters = new HashMap<String, String>();
  }

  /** Builds a configured filter instance.
   *
   * @param theFilter The filter being configured. It cannot be null.
   *
   * @param theParameters The filter being configured. If it is null, no
   * parameters will be passed to the filter.
   */
  public FilterAndParameters(final Filter theFilter,
      final Map<String, String> theParameters) {
    Validate.notNull(theFilter, "The filter cannot not be null");
    Validate.notNull(theParameters, "The parameters cannot not be null");
    filter = theFilter;
    parameters = theParameters;
  }

  /** The filter being configured.
   *
   * @return Returns the contained filter. Never returns null.
   */
  public Filter getFilter() {
    return filter;
  }

  /** The parameters to configure the filter.
   *
   * @return Returns the parameters. Never returns null.
   */
  public Map<String, String> getParameters() {
    return parameters;
  }
}

