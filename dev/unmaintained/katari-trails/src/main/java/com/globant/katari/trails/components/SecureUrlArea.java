package com.globant.katari.trails.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebSession;

import com.globant.katari.core.security.SecureUrlAccessHelper;
import com.javaforge.tapestry.spring.annotations.InjectSpring;

/**
 * Hides non accessible url related contents.<br>
 *
 * This SecureUrlArea component hides the component body if the access of given
 * url will throw an AccessDeniedException based on security url-to-role
 * mappings configuration in the module.xml and the authenticated user roles.
 * <br>
 * If the authenticated user has not the required roles to access the url,
 * the application is running on development mode (see
 * {@link com.globant.katari.core.web.DevelopmentDataBaseChecker})
 * and the user adds in the get query string the parameter securityDebug=true
 * then this component will render in red color with a debug label<br>
 * above the body.
 * <pre>{@code
 * Example:
 * <span jwcid="reports@SecureUrlArea" url="/module/reports/editReport.do">
 *   You can edit reports using the following link.
 *   <span jwcid="@Insert" value="ognl:#this.components.reports.url"/>
 * </span>
 * }</pre>
 */
@ComponentClass(allowBody = true, allowInformalParameters = true)
public abstract class SecureUrlArea extends BaseComponent {

  /**
   * Class logger.
   */
  private static Log log = LogFactory.getLog(SecureUrlArea.class);

  /**
   * Secure access helper used to determine if an action is accessible for the
   * current user.
   * @return the SecureUrlAccessHelper. it never returns null.
   */
  @InjectSpring("katari.secureUrlAccessHelper")
  public abstract SecureUrlAccessHelper getHelper();

  /**
   * Current web request.
   * @return the WebRequest. it never returns null.
   */
  @InjectObject("infrastructure:request")
  public abstract WebRequest getRequest();

  /**
   * The katari url parameter, the path can be relative to the current module
   * or absolute to an external target module.
   * @return a String. Never returns null.
   */
  @Parameter(required = true)
  public abstract String getUrl();

  /**
   * Verify if the given url is accessible by the current user based on the
   * given url.
   * @return true if the current user has access to the given url.
   */
  public boolean canAccessUrl() {
    boolean canAccess = getHelper().canAccessUrl(getRequest().getRequestURI(),
        getUrl());
    if (log.isDebugEnabled()) {
      if (canAccess) {
        log.debug("the current user can access to " + getUrl());
      } else {
        log.debug("the current user can not access to " + getUrl());
      }
    }
    return canAccess;
  }

  /**
   * Indicates if the application is in development security debug mode.
   *
   * If the application is in security debug mode, and the current user cannot
   * access the given url the template will render a red 'debug' title over the
   * SecureUrlArea body.
   * @return true if the application is in security debug mode.
   */
  public boolean isInSecurityDebugMode() {
    WebSession session = getRequest().getSession(false);
    boolean testMode = false;
    if (session != null) {
      testMode = session.getAttribute("securityDebug") != null;
    }
    return testMode;
  }
}
