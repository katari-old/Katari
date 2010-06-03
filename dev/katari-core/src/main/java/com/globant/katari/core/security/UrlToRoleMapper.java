package com.globant.katari.core.security;

/**
 * This interface is used to apply security to the modules, implementing
 * <code>getRolesForUrl</code> to look for the mandatory roles for the given
 * url.
 * @author maximiliano.roman
 */
public interface UrlToRoleMapper {

  /** Finds the roles that are allowed to access an url.
   *
   * @param url the url to get the roles for. It cannot be null.
   *
   * @return A String[] with the roles. It never returns null.
   */
  String[] getRolesForUrl(final String url);
}
