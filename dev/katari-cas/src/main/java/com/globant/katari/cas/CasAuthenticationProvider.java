/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import org.acegisecurity.AcegiMessageSource;
import org.acegisecurity.Authentication;
import org.acegisecurity.BadCredentialsException;

import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.acegisecurity.ui.cas.CasProcessingFilter;

import org.acegisecurity.userdetails.UserDetails;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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


public class CasAuthenticationProvider implements AuthenticationProvider,
       InitializingBean, MessageSourceAware {

  private static Log log = LogFactory.getLog(CasAuthenticationProvider.class);

  private CasAuthoritiesPopulator casAuthoritiesPopulator;

  private CasProxyDecider casProxyDecider;
  private MessageSourceAccessor messages = AcegiMessageSource.getAccessor();
  private StatelessTicketCache statelessTicketCache;
  private String key;
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

  public CasAuthoritiesPopulator getCasAuthoritiesPopulator() {
    return casAuthoritiesPopulator;
  }

  public CasProxyDecider getCasProxyDecider() {
    return casProxyDecider;
  }

  public String getKey() {
    return key;
  }

  public StatelessTicketCache getStatelessTicketCache() {
    return statelessTicketCache;
  }

  public CasProxyTicketValidator getTicketValidator() {
    return ticketValidator;
  }

  public void setCasAuthoritiesPopulator(final CasAuthoritiesPopulator
      theCasAuthoritiesPopulator) {
    casAuthoritiesPopulator = theCasAuthoritiesPopulator;
  }

  public void setCasProxyDecider(final CasProxyDecider theCasProxyDecider) {
    casProxyDecider = theCasProxyDecider;
  }

  public void setKey(final String theKey) {
    key = theKey;
  }

  public void setMessageSource(final MessageSource messageSource) {
    messages = new MessageSourceAccessor(messageSource);
  }

  public void setStatelessTicketCache(final StatelessTicketCache
      theStatelessTicketCache) {
    statelessTicketCache = theStatelessTicketCache;
  }

  public void setTicketValidator(final CasProxyTicketValidator
      theTicketValidator) {
    ticketValidator = theTicketValidator;
  }

  @SuppressWarnings("unchecked")
  public boolean supports(final Class authentication) {
    if (UsernamePasswordAuthenticationToken.class.isAssignableFrom(
          authentication)) {
      return true;
    }
    return CasAuthenticationToken.class.isAssignableFrom(authentication);
  }
}

