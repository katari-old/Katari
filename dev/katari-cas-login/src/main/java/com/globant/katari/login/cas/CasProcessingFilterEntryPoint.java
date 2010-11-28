/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import java.io.IOException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.AuthenticationEntryPoint;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/*** Used by the <code>SecurityEnforcementFilter</code> to commence
 * authentication via the JA-SIG Central Authentication Service (CAS).
 *
 * The user's browser will be redirected to the JA-SIG CAS enterprise-wide
 * login page. This page is specified by the login url service. Once login is
 * complete, the CAS login page will redirect to the page indicated by the
 * service url. The service is a HTTP URL belonging to the current application.
 * The service URL is monitored by the CasProcessingFilter, which will validate
 * the CAS login was successful.
 */
public class CasProcessingFilterEntryPoint implements AuthenticationEntryPoint,
       InitializingBean {

  /** The initial url buffer length.
  */
  private static final int URL_BUFFER_LENGTH = 255;

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      CasProcessingFilterEntryPoint.class);

  /** A creator of all the necessary service urls.
   *
   * This object only needs the service url and the login url.
   */
  private ServicesUrlBuilder servicesUrlBuilder = null;

  /** Called by spring after all properties has been set.
   *
   * We use it to validate the required parameters.
   */
  public void afterPropertiesSet() {
    Assert.notNull(servicesUrlBuilder, "servicesUrlBuilder must be specified");
  }

  /** Starts the authentication.
   *
   * This method redirects the browser to the cas login page.
   *
   * This method can be called only with a non null servicesUrlBuilder.
   *
   * @param servletRequest The servlet request. This must be an instance of
   * HttpServletRequest. It cannot be null.
   *
   * @param servletResponse The servlet response. This must be an instance of
   * HttpServletResponse. It cann0t be null.
   *
   * @param authenticationException This parameter is not used.
   *
   * @throws IOException in case of an io error.
   *
   * @throws ServletException in case of an unexpected error.
   */
  public void commence(final ServletRequest servletRequest, final
      ServletResponse servletResponse, final AuthenticationException
      authenticationException) throws IOException, ServletException {

    log.trace("Entering commence");

    if (servicesUrlBuilder == null) {
      throw new IllegalStateException("ServicesUrlBuilder must be"
          + " specified");
    }

    if (!(servletRequest instanceof HttpServletRequest)) {
      throw new ServletException("This filter can only be applied to http"
          + " requests.");
    }
    if (!(servletResponse instanceof HttpServletResponse)) {
      throw new ServletException("This filter can only be applied to http"
          + " responses.");
    }

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String service = servicesUrlBuilder.buildServiceUrl(request);
    String loginUrl = servicesUrlBuilder.buildCasLoginUrl();

    String urlEncodedService = response.encodeURL(service);

    StringBuffer buffer = new StringBuffer(URL_BUFFER_LENGTH);

    buffer.append(loginUrl);
    buffer.append("?service=");
    buffer.append(URLEncoder.encode(urlEncodedService, "UTF-8"));
    // buffer.append(serviceProperties.isSendRenew() ? "&renew=true" : "");

    response.sendRedirect(buffer.toString());
    log.trace("Leaving commence");
  }

  /** Sets the creator of all the necessary services url.
   *
   * @param urlsBuilder The url creator. It cannot be null.
   */
  public void setServicesUrlBuilder(final ServicesUrlBuilder urlsBuilder) {
    Validate.notNull(urlsBuilder, "The services builder cannot be null");
    servicesUrlBuilder = urlsBuilder;
  }
}

