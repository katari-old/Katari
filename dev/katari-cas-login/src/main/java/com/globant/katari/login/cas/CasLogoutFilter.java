/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;
import org.springframework.util.Assert;

/** Filter that logs out a user using CAS.
 *
 * When the users issues a request to /logout, this filter initiates the cas
 * logout. Once the logout finishes, this filter redirects to the service
 * parameter, for example:<br>
 *
 * /logout?service=/katari-sample/module/home.do<br>
 *
 * redirects the user to the home.do controller after logout. If no service
 * parameter is found, it redirects to the context path.
 *
 * When a user logs out from a service, CAS posts for each server a logout
 * request at the same url which CAS redirected after the successful login.<br>
 *
 * The post is made by the CAS server, so it doesn't belong to the user
 * sessions. That is the reason why this filter depends on
 * {@link CasTicketRegistry}, an object that stores which tickets belongs to
 * what sessions.<br>
 *
 * CAS send a logout request with the ticket issued and this filter invalidates
 * the session that is related to that ticket.
 *
 * @author pruggia
 */
public class CasLogoutFilter implements Filter {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(CasLogoutFilter.class);

  /** Start of a valid logout request, used to know if this filter has to be
   * activated.
   */
  private static final String LOGOUT_REQUEST_XML_START = "<samlp:LogoutRequest";

  /** Text just before the ticket string in the logout request.
   */
  private static final String LOGOUT_TICKET_START = "<samlp:SessionIndex>";

  /** Text just after the ticket string in the logout request.
   */
  private static final String LOGOUT_TICKET_END = "</samlp:SessionIndex>";

  /** Substring that this filter tries to match in order to be active.
   */
  private String filterProcessesUrl = "/j_acegi_cas_security_check";

  /** Substrig that indicates the logout path. */
  private String filterLogoutUrl = "/logout";

  /** Registry used to bind CAS tickets with user sessions.
   */
  private CasTicketRegistry casTicketRegistry;

  /** A creator of all the necessary service urls.
   */
  private ServicesUrlBuilder servicesUrlBuilder = null;

  /** The CasTicketRegistry constructor.
   *
   * @param theCasTicketRegistry The cas ticket registry. It cannot be null.
   *
   * @param aServicesUrlBuilder A builder of all the necessary service urls. It
   * cannot be null.
   */
  public CasLogoutFilter(final CasTicketRegistry theCasTicketRegistry, final
      ServicesUrlBuilder aServicesUrlBuilder) {
    Validate.notNull(theCasTicketRegistry);
    Validate.notNull(aServicesUrlBuilder);
    casTicketRegistry = theCasTicketRegistry;
    servicesUrlBuilder = aServicesUrlBuilder;
  }

  /** {@inheritDoc}
   */
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain chain) throws IOException, ServletException {

    log.trace("Entering doFilter");

    if (!(request instanceof HttpServletRequest)) {
      throw new ServletException("Can only process HttpServletRequest");
    }
    if (!(response instanceof HttpServletResponse)) {
      throw new ServletException("Can only process HttpServletResponse");
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (isRequestToUri(httpRequest, filterLogoutUrl)) {
      // The user pressed the logout button, redirect to CAS logout.
      String redirectUrl = servicesUrlBuilder.buildCasLogoutUrl();
      String service = httpRequest.getParameter("service");
      if (service == null) {
        service = httpRequest.getContextPath();
      }
      httpResponse.sendRedirect(redirectUrl + "?service=" + service);
      log.trace("Leaving doFilter with redirect to {}", redirectUrl);
      return;
    }

    String ticket = getTicketFromRequest(httpRequest);

    if (ticket != null) {
      log.debug("Processing logout request from CAS");
      // We process a logout request from CAS, invalidate the session
      // corresponding to the ticket.

      HttpSession session = casTicketRegistry.getSession(ticket);

      if (session != null) {
        // TODO The handlers are not called here. Basically the problem is that
        // the request is not issued by the user, it's made by the cas server,
        // so the cookies of the user and the session of the user are not
        // reachable from here. To solve the session problem we can create a
        // request wrapper that rewrites the getSession() method, but there is
        // no simple hack for the cookies.

        // for (int i = 0; i < handlers.length; i++) {
        // handlers[i].logout(new CasLogoutRequestWrapper(httpRequest,
        // session), httpResponse, auth);
        // }
        session.invalidate();
      }
      log.trace("Leaving doFilter");
      return;
    }
    chain.doFilter(request, response);
    log.trace("Leaving doFilter");
  }

  /**
   * Reads the post searching for a "logoutRequest" parameter.
   * @param request The {@link HttpServletRequest}.
   * @return null if the logout request is not a valid one, a String if the
   *         logout request is valid.
   * @throws IOException If there was any reading error.
   */
  protected String readLogoutRequest(final HttpServletRequest request)
      throws IOException {
    // now we check if cas is posting a login or a logout
    if (!request.getMethod().equals("POST")) {
      return "";
    }
    return request.getParameter("logoutRequest");
  }

  /** Extracts the ticket from inside the logout request.
   *
   * @param request The {@link HttpServletRequest}.
   *
   * @return A String representing the ticket that belongs to the user that
   *         wants to logout, null if the ticket couldn't be found.
   *
   * @throws IOException If there was any reading error.
   */
  protected String getTicketFromRequest(final HttpServletRequest request)
      throws IOException {

    if (!isRequestToUri(request, filterProcessesUrl)) {
      return null;
    }

    String post = readLogoutRequest(request);

    if (post.length() < LOGOUT_REQUEST_XML_START.length()
        || !post.substring(0, LOGOUT_REQUEST_XML_START.length()).equals(
            LOGOUT_REQUEST_XML_START)) {
      return null;
    }

    int ticketStart = post.indexOf(LOGOUT_TICKET_START);
    int ticketEnd = post.indexOf(LOGOUT_TICKET_END);
    if (ticketStart < 0 || ticketEnd < 0) {
      return null;
    }

    return post.substring(ticketStart + LOGOUT_TICKET_START.length(),
        ticketEnd);
  }

  /** Sets the substring that this filter tries to match in order to be active.
   *
   * @param theFilterProcessesUrl The url, cannot be null.
   */
  public void setFilterProcessesUrl(final String theFilterProcessesUrl) {
    Assert.hasText(theFilterProcessesUrl, "FilterProcessesUrl required");
    filterProcessesUrl = theFilterProcessesUrl;
  }

  /** {@inheritDoc}
   */
  public void init(final FilterConfig arg0) throws ServletException {

  }

  /** {@inheritDoc}
   */
  public void destroy() {
  }

  /** This method indicated if the given request is related to the given url.
   *
   * @param request the request. It cannot be null.
   *
   * @param url the url. It cannot be null.
   *
   * @return if the given request is related to the given url.
   */
  protected boolean isRequestToUri(final HttpServletRequest request
      , final String url) {
    String uri = request.getRequestURI();

    int pathParamIndex = uri.indexOf(';');

    if (pathParamIndex > 0) {
      // strip everything after the first semi-colon
      uri = uri.substring(0, pathParamIndex);
    }
    return uri.endsWith(request.getContextPath() + url);
  }
}

