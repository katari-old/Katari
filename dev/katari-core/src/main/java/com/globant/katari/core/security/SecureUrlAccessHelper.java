/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.security;

import org.acegisecurity.AccessDecisionManager;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.intercept.web.AbstractFilterInvocationDefinitionSource;

import org.apache.commons.lang.Validate;

import com.globant.katari.core.web.ModuleUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Secure URL Access Helper.
 *
 * This helper is used by the katari secureUrlArea macro and the {@link
 * MenuAccessFilterer} to determine if an action is accessible for the current
 * user based on the role configuration of the target url.
 *
 * @author gerardo.bercovich
 */
public class SecureUrlAccessHelper {

  /** The class logger.
  */
  private static Logger log =
    LoggerFactory.getLogger(SecureUrlAccessHelper.class);

  /** The acegi definition source implementation.
   *
   * It is used to obtain the ConfigAttributeDefinition with the
   * roles for a given url. It is never null.
   */
  private final AbstractFilterInvocationDefinitionSource definitionSource;

  /** The acegi decider.
   *
   * It is never null.
   */
  private final AccessDecisionManager accessDecider;

  /**
   * Constructor.
   *
   * It constructs a SecureUrlAccessHelper with the AccessDesicionManager
   * and the {@link AbstractFilterInvocationDefinitionSource} given.
   *
   * @param thedefinitionSource the definition source used to obtain the roles
   * that can access a url. It cannot be null.
   *
   * @param theAccessDecisionManager the access decision manager used to check
   * if the user has the necessary roles. It cannot be null.
   */
  public SecureUrlAccessHelper(
      final AbstractFilterInvocationDefinitionSource thedefinitionSource,
      final AccessDecisionManager theAccessDecisionManager) {
    Validate.notNull(thedefinitionSource,
        "Filter invocation access definition " + "source cannot be null");
    Validate.notNull(theAccessDecisionManager, "Access Decision Manager "
        + "cannot be null");
    definitionSource = thedefinitionSource;
    accessDecider = theAccessDecisionManager;
  }

  /**
   * Verify if the given url is accessible by the current user based on the
   * given url and the current uri.
   *
   * The current uri is used if the given url is relative.
   *
   * @param currentUri the current uri. If the target url is relative it cannot
   * be null.
   *
   * @param targetUrl the target url. It cannot be null.
   *
   * @return True if the current user has access to the given url (theUrl).
   */
  public boolean canAccessUrl(final String currentUri,
      final String targetUrl) {

    log.trace("Entering canAccessUrl({}, {})", currentUri, targetUrl);

    Validate.notNull(targetUrl, "The url cannot be null");
    Validate.isTrue(!targetUrl.matches("^[^:]+://.+"), "The url has protocol");

    Authentication authentication;
    authentication = SecurityContextHolder.getContext().getAuthentication();
    Validate.notNull(authentication, "The authentication cannot be null");

    String url = null;

    if (!targetUrl.startsWith("/")) {
      // if the url is relative compose url.
      Validate.notNull(currentUri,
          "The current uri cannot be null for a relative url");
      Validate.isTrue(currentUri.matches(".*/module/.*"),
          "The corrent uri does not contain any module");
      String globalContextPath = ModuleUtils.getGlobalContextPath(currentUri);
      url = currentUri.substring(globalContextPath.length());
      // Strip the last path component.
      int lastSlash = url.lastIndexOf('/');
      url = url.substring(0, lastSlash + 1);
      url = url + targetUrl;
    } else {
      // if is absolute validate context path.
      String globalContextPath = ModuleUtils.getGlobalContextPath(targetUrl);
      Validate.isTrue(targetUrl.matches(".*/module/.*"),
        "The target url does not contain any module");
      url = targetUrl.substring(globalContextPath.length());
    }

    log.debug("Checking {}", url);
    ConfigAttributeDefinition attributes;
    try {
      attributes = definitionSource.lookupAttributes(url);
    } catch (AccessDeniedException e) {
      log.trace("Leaving canAccessUrl with false. No attribute matched the"
          + " link.");
      return false;
    }
    Validate.notNull(attributes, "No attribtues found for url");
    try {
      accessDecider.decide(authentication, url, attributes);
    } catch (AccessDeniedException e) {
      log.trace("Leaving canAccessUrl with false");
      return false;
    }
    log.trace("Leaving canAccessUrl with true");
    return true;
  }
}

