/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.ui.cas.CasProcessingFilter;


/** Processes a CAS service ticket to authenticate a user.
 *
 * When a successful authentication takes place, it stores the ticket in the
 * {@link CasTicketRegistry}.
 *
 * @author pruggia
 */
public class CasTicketRegisteringProcessingFilter extends CasProcessingFilter {

  /**
   * Registry used to bind CAS tickets with user sessions.
   */
  private CasTicketRegistry casTicketRegistry;

  /**
   * Sets the registry for CAS tickets.
   * @param theCasTicketRegistry a CAS Ticket.
   */
  public void setCasTicketRegistry(final CasTicketRegistry
      theCasTicketRegistry) {
    casTicketRegistry = theCasTicketRegistry;
  }

  /**
   * {@inheritDoc}. If a ticket registry is defined, this method tells the
   * ticket registry to store the ticket.
   */
  @Override
  protected void onSuccessfulAuthentication(final HttpServletRequest request,
      final HttpServletResponse response, final Authentication authResult)
      throws IOException {
    super.onSuccessfulAuthentication(request, response, authResult);
    if (casTicketRegistry != null) {
      casTicketRegistry.registerTicket((String) authResult.getCredentials(),
          request.getSession());
    }
  }
}

