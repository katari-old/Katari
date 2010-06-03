/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.security;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * A predicate used to decide which filter chain to use to manage security.
 * This class represents a predicate that will return true or false
 * based on whether the URL matches one of certain regular expression patterns
 * or not.
 *
 * @author rcunci
 */
public class RequestPredicate {

  /** regexpCollection. A collection of Patterns already compiled. The
   * patterns will be matched against the URL of the request (context
   * path + path info).
   */
  private Collection<Pattern> regexpCollection = new LinkedList<Pattern>();

  /**
   * Constructor.
   * @param aRegexpCollection A collection of regular expressions written
   * the java way (as used by Pattern) to be matched against the
   * URL of the request.
   */
  public RequestPredicate(final Collection<String> aRegexpCollection) {
    Validate.notNull(regexpCollection, "The regexp collection can't be null.");
    Validate.noNullElements(regexpCollection, "The regexp collection cannot "
        + "have empty nor null elements.");

    for (String regexp : aRegexpCollection) {
      regexpCollection.add(Pattern.compile(regexp));
    }
  }

  /**
   * Evaluates the predicate. Returns true or false whether the URL
   * matches one of the regular expressions from the set or not.
   *
   * @param request a ServletRequest. Cannot be null.
   * @return true or false
   */
  public boolean evaluate(final ServletRequest request) {
    Validate.isTrue(request instanceof HttpServletRequest, "The request"
        + " should be a HttpServletRequest");

    if (!(request instanceof HttpServletRequest)) {
      throw new RuntimeException("Not an http request.");
    }

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;

    String url = httpServletRequest.getContextPath()
      + StringUtils.defaultString(httpServletRequest.getPathInfo());

    for (Pattern regexp : regexpCollection) {
      if (regexp.matcher(url).matches()) {
        return true;
      }
    }

    return false;
  }
}
