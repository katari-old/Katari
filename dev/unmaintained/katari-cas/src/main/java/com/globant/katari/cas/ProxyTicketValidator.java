/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/** Validates PTs and optionally retrieves PGT IOUs.
 *
 *  Subclassed instead of collapsed into parent because we don't want users to
 *  accidentally accept a proxy ticket when they mean only to accept service
 *  tickets. That is, proxy targets need to know that they're proxy targets,
 *  not first-level web applications.
 */
public class ProxyTicketValidator extends HttpServiceTicketValidator {

  /** The list of proxies.
   */
  @SuppressWarnings("unchecked")
  private List proxyList;

  /** Retrieves a list of proxies involved in the current authentication.
   *
   * @return a list of proxies. It is null after the validator is cleared.
   */
  @SuppressWarnings("unchecked")
  public List getProxyList() {
    return proxyList;
  }

  /** Creates a listener of sax events.
   *
   * @return the sax event listener.
   */
  protected DefaultHandler newHandler() {
    return new ProxyHandler();
  }

  /** Parses the cas message.
   */
  protected class ProxyHandler extends HttpServiceTicketValidator.Handler {

    //**********************************************
    // Constants

    /** The name of the proxies xml element: cas:proxies.
     */
    private static final String PROXIES = "cas:proxies";

    /** The name of the proxy xml element: cas:proxy.
     */
    private static final String PROXY = "cas:proxy";

    //**********************************************
    // Parsing state

    /** The list of proxies.
     *
     * It is never null.
     */
    private List<String> proxyList = new ArrayList<String>();

    /** A state variable that indicates if we are parsing the PROXIES element.
     */
    private boolean proxyFragment = false;

    //**********************************************
    // Parsing logic

    /** {@inheritDoc}
     */
    public void startElement(final String ns, final String ln, final String qn,
        final Attributes a) {
      super.startElement(ns, ln, qn, a);
      if (getAuthenticationSuccess() && qn.equals(PROXIES)) {
        proxyFragment = true;
      }
    }

    /** {@inheritDoc}
     */
    public void endElement(final String ns, final String ln, final String qn)
        throws SAXException {
      super.endElement(ns, ln, qn);
      if (qn.equals(PROXIES)) {
        proxyFragment = false;
      } else if (proxyFragment && qn.equals(PROXY)) {
        proxyList.add(getCurrentText());
      }
    }

    /** {@inheritDoc}
     */
    public void endDocument() throws SAXException {
      super.endDocument();
      if (getAuthenticationSuccess()) {
        ProxyTicketValidator.this.proxyList = proxyList;
      }
    }
  }

  //*********************************************************************
  // Utility methods

  /** Clears internally manufactured state.
   */
  protected void clear() {
    super.clear();
    proxyList = null;
  }

  /** Implements the toString operation.
   *
   * It shows the proxy ticket validator class and the proxy list for debugging
   * purposes.
   *
   * @return a string representation of the state of the class.
   */
  public String toString() {
    StringBuffer sb  = new StringBuffer();
    sb.append("[");
    sb.append(ProxyTicketValidator.class.getName());
    sb.append(" proxyList=[");
    sb.append(this.proxyList);
    sb.append("] ");
    sb.append(super.toString());
    sb.append("]");
    return sb.toString();
  }
}

