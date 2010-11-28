/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import org.acegisecurity.AcegiMessageSource;
import org.acegisecurity.Authentication;
import org.acegisecurity.BadCredentialsException;

import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.acegisecurity.ui.cas.CasProcessingFilter;

import org.acegisecurity.userdetails.UserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.util.Assert;

import org.acegisecurity.providers.cas.TicketResponse;
import org.acegisecurity.providers.cas.StatelessTicketCache;
import org.acegisecurity.providers.cas.CasProxyDecider;
import org.acegisecurity.providers.cas.CasAuthenticationToken;
import org.acegisecurity.providers.cas.CasAuthoritiesPopulator;

/** A customized cas authentication provider that uses katari
 * CasProxyTicketValidator, that does not need to be configured with the url of
 * the server (this application).
 */
public class CasAuthenticationProvider implements AuthenticationProvider,
       InitializingBean, MessageSourceAware {

  /** The class logger.
  */
  private static Logger log = LoggerFactory.getLogger(
      CasAuthenticationProvider.class);

  /** Populates the user details (mainly, the authorities).
   *
   * Must be set to non null in setCasAuthoritiesPopulator before
   * authenticating users.
   */
  private CasAuthoritiesPopulator casAuthoritiesPopulator;

  /** Validates if the requestor of a proxy ticket is trusted.
   *
   * Must be set to non null in setCasProxyDecider before authenticating users.
   */
  private CasProxyDecider casProxyDecider;

  /** Helper class to access messages from a MessageSource.
   *
   * Must be set to non null in setMessageSource before authenticating users.
   */
  private MessageSourceAccessor messages = AcegiMessageSource.getAccessor();

  /** Caches success tickets for stateless servers.
   *
   * Must be set to non null in setStatelessTicketCache before authenticating
   * users.
   */
  private StatelessTicketCache statelessTicketCache;

  /** A string to identify this provider in this application.
   *
   * Must be set to non null in setKey before authenticating users.
   */
  private String key;

  /** Uses CAS' ProxyTicketValidator to validate a service ticket.
   *
   * Must be set to non null in setTicketValidator before authenticating users.
   */
  private CasProxyTicketValidator ticketValidator;

  /** {@inheritDoc}
   */
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(casAuthoritiesPopulator,
        "A casAuthoritiesPopulator must be set");
    Assert.notNull(ticketValidator, "A ticketValidator must be set");
    Assert.notNull(casProxyDecider, "A casProxyDecider must be set");
    Assert.notNull(statelessTicketCache, "A statelessTicketCache must be set");
    Assert.notNull(key, "A Key is required so CasAuthenticationProvider can"
        + " identify tokens it previously authenticated");
    Assert.notNull(messages, "A message source must be set");
  }

  /** {@inheritDoc}
   */
  public Authentication authenticate(final Authentication authentication) {
    log.trace("Entering authenticate");
    if (!supports(authentication.getClass())) {
      return null;
    }

    // Check if it is a user/pass authentication token. If this is the case,
    // just skip it.
    if (authentication instanceof UsernamePasswordAuthenticationToken
        && (!CasProcessingFilter.CAS_STATEFUL_IDENTIFIER.equals(
            authentication.getPrincipal().toString())
          && !CasProcessingFilter.CAS_STATELESS_IDENTIFIER.equals(
            authentication.getPrincipal().toString()))) {
      // UsernamePasswordAuthenticationToken not CAS related
      log.trace("Leaving authenticate");
      return null;
    }

    // If an existing CasAuthenticationToken, just check we created it
    if (authentication instanceof CasAuthenticationToken) {
      if (key.hashCode() == ((CasAuthenticationToken)
            authentication).getKeyHash()) {
        log.trace("Leaving authenticate");
        return authentication;
      } else {
        throw new BadCredentialsException(messages.getMessage(
              "CasAuthenticationProvider.incorrectKey",
              "The presented CasAuthenticationToken does not contain the"
              + " expected key"));
      }
    }

    // Ensure credentials are presented
    if ((authentication.getCredentials() == null)
        || "".equals(authentication.getCredentials())) {
      throw new BadCredentialsException(messages.getMessage(
            "CasAuthenticationProvider.noServiceTicket",
            "Failed to provide a CAS service ticket to validate"));
    }

    boolean stateless = false;

    if (authentication instanceof UsernamePasswordAuthenticationToken
        && CasProcessingFilter.CAS_STATELESS_IDENTIFIER.equals(
          authentication.getPrincipal())) {
      stateless = true;
    }

    CasAuthenticationToken result = null;

    if (stateless) {
      // Try to obtain from cache
      result = statelessTicketCache.getByTicketId(
          authentication.getCredentials().toString());
    }

    if (result == null) {
      result = authenticateNow(authentication);
    }

    if (stateless) {
      // Add to cache
      statelessTicketCache.putTicketInCache(result);
    }

    log.trace("Leaving authenticate");
    return result;
  }

  /** Authenticates the provided authentication asking the CAS server if the
   * request token is valid.
   *
   * @param authentication The acegi representation of the CAS token. It cannot
   * be null.
   *
   * @return An autenticated token, never null.
   */
  private CasAuthenticationToken authenticateNow(final Authentication
      authentication) {
    // Validate

    log.trace("Entering authenticate");

    TicketResponse response = ticketValidator.confirmTicketValid(
        authentication);

    // Check proxy list is trusted
    casProxyDecider.confirmProxyListTrusted(response.getProxyList());

    // Lookup user details
    UserDetails userDetails = casAuthoritiesPopulator.getUserDetails(
        response.getUser());

    // Construct CasAuthenticationToken
    CasAuthenticationToken result = new CasAuthenticationToken(key,
        userDetails, authentication.getCredentials(),
        userDetails.getAuthorities(), userDetails, response.getProxyList(),
        response.getProxyGrantingTicketIou());

    log.trace("Leaving authenticate");

    return result;
  }

  /** Returns the object that populates the user details (mainly, the
   * authorities).
   *
   * @return a CasAuthenticationToken, null until initialized, never null
   * afterwards.
   */
  public CasAuthoritiesPopulator getCasAuthoritiesPopulator() {
    return casAuthoritiesPopulator;
  }

  /** Sets the object that populates the user details (mainly, the
   * authorities).
   *
   * @param theCasAuthoritiesPopulator the authorities populator, it cannot be
   * null.
   */
  public void setCasAuthoritiesPopulator(final CasAuthoritiesPopulator
      theCasAuthoritiesPopulator) {
    casAuthoritiesPopulator = theCasAuthoritiesPopulator;
  }

  /** Returns the object that validates if the requestor of a proxy ticket is
   * trusted.
   *
   * @return a CasProxyDecider, null until initialized, never null afterwards.
   */
  public CasProxyDecider getCasProxyDecider() {
    return casProxyDecider;
  }

  /** Sets the object that validates if the requestor of a proxy ticket is
   * trusted.
   *
   * @param theCasProxyDecider the cas decider, it cannot be null.
   */
  public void setCasProxyDecider(final CasProxyDecider theCasProxyDecider) {
    casProxyDecider = theCasProxyDecider;
  }

  /** Returns a string to identify this provider in this application.
   *
   * @return a String, null until initialized, never null afterwards.
   */
  public String getKey() {
    return key;
  }

  /** Sets the string that identifies this provider in this application.
   *
   * @param theKey the key, it cannot be null.
   */
  public void setKey(final String theKey) {
    key = theKey;
  }

  /** Returns a cache of success tickets for stateless servers.
   *
   * @return a StatelessTicketCache, null until initialized, never null
   * afterwards.
   */
  public StatelessTicketCache getStatelessTicketCache() {
    return statelessTicketCache;
  }

  /** Set the cache of success tickets for stateless servers.
   *
   * @param theStatelessTicketCache the cache. It cannot be null.
   */
  public void setStatelessTicketCache(final StatelessTicketCache
      theStatelessTicketCache) {
    statelessTicketCache = theStatelessTicketCache;
  }

  /** Returns the object that uses CAS' ProxyTicketValidator to validate a
   * service ticket.
   *
   * @return a CasProxyTicketValidator, null until initialized, never null
   * afterwards.
   */
  public CasProxyTicketValidator getTicketValidator() {
    return ticketValidator;
  }

  /** Sets the object that uses CAS' ProxyTicketValidator to validate a service
   * ticket.
   *
   * @param theTicketValidator the validator. It cannot be null.
   */
  public void setTicketValidator(final CasProxyTicketValidator
      theTicketValidator) {
    ticketValidator = theTicketValidator;
  }


  /** Sets the helper class to access messages from a MessageSource.
   *
   * @param messageSource the message source. It cannot be null.
   */
  public void setMessageSource(final MessageSource messageSource) {
    messages = new MessageSourceAccessor(messageSource);
  }

  /** {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public boolean supports(final Class authentication) {
    if (UsernamePasswordAuthenticationToken.class.isAssignableFrom(
          authentication)) {
      return true;
    }
    return CasAuthenticationToken.class.isAssignableFrom(authentication);
  }
}

