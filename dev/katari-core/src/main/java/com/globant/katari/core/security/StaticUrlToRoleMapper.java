package com.globant.katari.core.security;

import java.util.Map;

import org.acegisecurity.AccessDeniedException;
import org.apache.commons.lang.Validate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * This class implements <code>UrlToRoleMapper</code>, it is the default
 * implementation of this interface.
 *
 * It has a map, which can be injected by spring containing all roles which its
 * respective urls.
 * @author maximiliano.roman
 */
public class StaticUrlToRoleMapper implements UrlToRoleMapper {

  /**
   * The map containing the url as a key and the roles as a value.
   * It cannot be null.
   */
  private Map<String, String[]> urlPathRolesMap;

  /**
   * The path matcher, to match different urls.
   * It cannot be null.
   */
  private PathMatcher pathMatcher;

  /** Constructor.
   *
   * @param theUrlPathRolesMap a map of url expressions to the list of roles
   * that can access that url. It cannot be null.
   */
  public StaticUrlToRoleMapper(final Map<String, String[]> theUrlPathRolesMap) {
    Validate.notNull(theUrlPathRolesMap, "The UrlPathRolesMap"
        + " cannot be null");
    urlPathRolesMap = theUrlPathRolesMap;
    pathMatcher = new AntPathMatcher();
  }

  /** Finds the roles that are allowed to access an url.
   *
   * This method only considers the url excluding the GET parameters (whatever
   * goes after the ?). If the url does not match any expression defined for the
   * module, this method throws AccessDeniedException.
   *
   * @param theUrl the url to get the roles for. It cannot be null.
   *
   * @return A String[] with the roles. It never returns null.
   */
  public String[] getRolesForUrl(final String theUrl) {
    Validate.notNull(theUrl, "The url given cannot be null");
    String url = theUrl.toLowerCase();
    int firstQuestionMarkIndex = url.indexOf('?');
    if (firstQuestionMarkIndex != -1) {
      url = url.substring(0, firstQuestionMarkIndex);
    }
    for (String urlPattern : urlPathRolesMap.keySet()) {
      boolean matched = pathMatcher.match(urlPattern.toLowerCase(), url);
      if (matched) {
        return urlPathRolesMap.get(urlPattern);
      }
    }
    throw new AccessDeniedException("The url: '" + url
        + "' does not match any roles configuration");
  }
  /**
   * It sets the pathMatcher. It allows to define the path matcher used for
   * matching the url patterns with the given URLs. By default this class use
   * an AntPathMatcher.
   * @param thePathMatcher the pathMatcher. It cannot be null.
   */
  public void setPathMatcher(final PathMatcher thePathMatcher) {
    Validate.notNull(thePathMatcher, "The pathMatcher cannot be null");
    pathMatcher = thePathMatcher;
  }
}
