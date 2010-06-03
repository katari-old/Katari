/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;

import org.acegisecurity.providers.cas.TicketValidator;

import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.BadCredentialsException;

import org.acegisecurity.providers.cas.TicketResponse;

import org.acegisecurity.Authentication;


/** A replacement of acegisecurity CasProxyTicketValidator that allows for
 * plain http cas server and more flexible service url configuration.
 *
 * Cas 3.1 is supposed to be more flexible in this subject. Anyway,
 * acegisecurity is not yet supporting this. This is supposed to be ready for
 * version 1.1.
 *
 * @see ProxyTicketValidator.
 *
 * @see SecureUrl.
 *
 * @see HttpServiceTicketValidator.
 *
 * TODO Study this.
 */
public class CasProxyTicketValidator implements TicketValidator,
       InitializingBean {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(CasProxyTicketValidator.class);

  private CasServicesUrlBuilder servicesUrlBuilder = null;

  private String trustStore = null;

  public void afterPropertiesSet() throws Exception {
    if ((trustStore != null) && (!"".equals(trustStore))) {
      if (log.isDebugEnabled()) {
        log.debug("Setting system property 'javax.net.ssl.trustStore'"
            + " to value [" + trustStore + "]");
      }
      System.setProperty("javax.net.ssl.trustStore", trustStore);
    }
  }

  public TicketResponse confirmTicketValid(final String serviceTicket) {
    return null;
  }

  /** Attempts to validate presented ticket using CAS' ProxyTicketValidator
   * class.
   */
  public TicketResponse confirmTicketValid(final Authentication
      authentication) {
    log.trace("Entering confirmTicketValid");

    // Builds the relevant url based on the request url.
    FullWebAuthenticationDetails details;
    details = (FullWebAuthenticationDetails) authentication.getDetails();

    if (log.isDebugEnabled()) {
      log.debug("URL path: " + details.getRequest().getRequestURL());
      log.debug("Query string: " + details.getRequest().getQueryString());
      log.debug("Context path: " + details.getRequest().getContextPath());
      log.debug("Path info: " + details.getRequest().getPathInfo());
    }

    String casValidate = servicesUrlBuilder.buildCasValidatorUrl(
        details.getRequest());
    String service = servicesUrlBuilder.buildServiceUrl(details.getRequest());

    if (log.isDebugEnabled()) {
      log.debug("Cas validate url: " + casValidate);
      log.debug("Service: " + service);
    }

    ProxyTicketValidator proxyValidator = new ProxyTicketValidator();

    String serviceTicket = authentication.getCredentials().toString();

    proxyValidator.setCasValidateUrl(casValidate);
    proxyValidator.setServiceTicket(serviceTicket);
    proxyValidator.setService(service);

    /*
    if (super.getServiceProperties().isSendRenew()) {
      log.warn("The current CAS ProxyTicketValidator does not support the"
          + " 'renew' property. The ticket cannot be validated as having been"
          + " issued by a 'renew' authentication. It is expected this will be"
          + " corrected in a future version of CAS' ProxyTicketValidator.");
    }
    */

    String proxyCallbackUrl = servicesUrlBuilder.buildProxyCallbackUrl(
        details.getRequest());
    if (proxyCallbackUrl != null) {
      proxyValidator.setProxyCallbackUrl(proxyCallbackUrl);
    }

    TicketResponse response = validateNow(proxyValidator);
    log.trace("Leaving confirmTicketValid");

    return response;
  }

  /** Perform the actual remote invocation.
   *
   * @param pv the populated <code>ProxyTicketValidator</code>
   *
   * @return the <code>TicketResponse</code>
   */
  private TicketResponse validateNow(final ProxyTicketValidator pv) {
    try {
      pv.validate();
    } catch (Exception e) {
      throw new AuthenticationServiceException(e.getMessage());
    }

    if (!pv.isAuthenticationSuccesful()) {
      throw new BadCredentialsException(pv.getErrorCode() + ": "
          + pv.getErrorMessage());
    }

    return new TicketResponse(pv.getUser(), pv.getProxyList(), pv.getPgtIou());
  }

  /* Sets the services url builder, an object that creates the url of the
   * different services.
   *
   * @param theServicesBuilder the services url builder. It cannot be null.
   */
  public void setServicesUrlBuilder(final CasServicesUrlBuilder
      theServicesBuilder) {
    servicesUrlBuilder = theServicesBuilder;
  }

  /*** Optional property which will be used to set the system property
   * <code>javax.net.ssl.trustStore</code>.
   *
   * @return the <code>javax.net.ssl.trustStore</code> that will be set
   * during bean initialization, or <code>null</code> to leave the system
   * property unchanged
   */
  public String getTrustStore() {
    return trustStore;
  }

  /*** Optional property which will be used to set the system property
   * <code>javax.net.ssl.trustStore</code>.
   *
   * @param theTrustStore the trust store, ignored if null.
   */
  public void setTrustStore(final String theTrustStore) {
    trustStore = theTrustStore;
  }
}

