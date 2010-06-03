/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Map;

import javax.servlet.Filter;

import org.apache.commons.lang.Validate;

/** Specifies on which url a specific filter is called.
 *
 * When a request is about to be processed, the request url is matched against
 * the url pattern. If it matches, the filter is called. Filters are called in
 * ascending priority order.
 */
public final class FilterMapping {

  /** The regular expression that must match the url to invoke the filter.
   *
   * It is never null.
   */
  private String pattern;

  /** The filter that is called on the matching url.
   *
   * It is never null.
   */
  private FilterAndParameters filterAndParameters;

  /** The filter priority.
   *
   * The filters are called in order according to the priority, lowest first.
   */
  private int priority = 0;

  /** Builds a filter mapping instance.
   *
   * @param thePattern The regular expression against which the url is checked.
   *
   * @param theFilter The filter being configured. It cannot be null.
   */
  public FilterMapping(final String thePattern, final FilterAndParameters
      theFilter) {
    Validate.notNull(theFilter, "The filter cannot not be null");
    Validate.notNull(thePattern, "The pattern regular expression cannot not be"
        + " null");
    filterAndParameters = theFilter;
    pattern = thePattern;
  }

  /** Builds a filter mapping instance.
   *
   * @param thePattern The regular expression against which the url is checked.
   *
   * @param theFilter The filter being configured. It cannot be null.
   *
   * @param thePriority The priority of the filter. Filters are invoked, for a
   * matching url, in asending priority order.
   */
  public FilterMapping(final String thePattern, final FilterAndParameters
      theFilter, final int thePriority) {
    this(thePattern, theFilter);
    priority = thePriority;
  }

  /** Obtains the servlet filter of this filter mapping.
   *
   * @return The filter, never returns null.
   */
  Filter getFilter() {
    return filterAndParameters.getFilter();
  }

  /** Obtains the initialization parameters of the filter.
   *
   * @return a map of string to string with the initialization parameters.
   */
  Map<String, String> getParameters() {
    return filterAndParameters.getParameters();
  }

  /** Obtains the regular expression pattern that must match the request url to
   * invoke the filter.
   *
   * @return a string with the regular expression.
   */
  String getPattern() {
    return pattern;
  }

  /** Obtains the priority of this filter mapping.
   *
   * Filters are invoked on ascending priority order.
   *
   * @return the priority.
   */
  int getPriority() {
    return priority;
  }
}

