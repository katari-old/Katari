/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;

import org.acegisecurity.providers.cas.TicketValidator;

import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.BadCredentialsException;

import org.acegisecurity.providers.cas.TicketResponse;

import org.acegisecurity.Authentication;

import edu.yale.its.tp.cas.client.ProxyTicketValidator;

/** A replacement of acegisecurity CasProxyTicketValidator that allows for more
 * flexible service url configuration.
 *
 * Cas 3.1 is supposed to be more flexible in this subject. Anyway,
 * acegisecurity is not yet supporting this. This is supposed to be ready for
 * version 1.1.
 *
 * @see ProxyTicketValidator.
 */
public class CasProxyTicketValidator implements TicketValidator,
       InitializingBean {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      CasProxyTicketValidator.class);

  /** Builder for the cas related urls (cas login, logout, etc).
   *
   * This must be set with setServicesUrlBuilder before using this class.
   */
  private ServicesUrlBuilder servicesUrlBuilder = null;

  /** Stores the certificates that the application will trust, even if not
   * signed by a well known CA.
   *
   * If null, we will only accept certificates from well known CAs.
   */
  private String trustStore = null;

  /** {@inheritDoc}
   */
  public void afterPropertiesSet() throws Exception {
    if ((trustStore != null) && (!"".equals(trustStore))) {
      log.debug(
          "Setting system property 'javax.net.ssl.trustStore' to value [{}]",
          trustStore);
      System.setProperty("javax.net.ssl.trustStore", trustStore);
    }
  }

  /** Not used, always returns null.
   *
   * @param serviceTicket ignored.
   *
   * @return null.
   */
  public TicketResponse confirmTicketValid(final String serviceTicket) {
    return null;
  }

  /** Attempts to validate presented ticket using CAS' ProxyTicketValidator
   * class.
   *
   * @param authentication the acegi authentication. This implementation needs
   * an instance of FullWebAuthenticationDetails that contains the request done
   * by the user. It cannot be null.
   *
   * @return an object representing CAS response, never null.
   */
  public TicketResponse confirmTicketValid(final Authentication
      authentication) {
    log.trace("Entering confirmTicketValid");

    // Builds the relevant url based on the request url.
    FullWebAuthenticationDetails details;
    details = (FullWebAuthenticationDetails) authentication.getDetails();

    log.debug("URL path: {}", details.getRequest().getRequestURL());
    log.debug("Query string: {}", details.getRequest().getQueryString());
    log.debug("Context path: {}", details.getRequest().getContextPath());
    log.debug("Path info: {}", details.getRequest().getPathInfo());

    String casValidate = servicesUrlBuilder.buildCasValidatorUrl();
    String service = servicesUrlBuilder.buildServiceUrl(details.getRequest());

    log.debug("Cas validate url: {}", casValidate);
    log.debug("Service: {}", service);

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
      throw new AuthenticationServiceException(e.getMessage(), e);
    }

    if (!pv.isAuthenticationSuccesful()) {
      throw new BadCredentialsException(pv.getErrorCode() + ": "
          + pv.getErrorMessage());
    }

    return new TicketResponse(pv.getUser(), pv.getProxyList(), pv.getPgtIou());
  }

  /** Sets the services url builder, an object that creates the url of the
   * different services.
   *
   * @param urlsBuilder the services url builder. It cannot be null.
   */
  public void setServicesUrlBuilder(final ServicesUrlBuilder urlsBuilder) {
    servicesUrlBuilder = urlsBuilder;
  }

  /** Optional property which will be used to set the system property
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

