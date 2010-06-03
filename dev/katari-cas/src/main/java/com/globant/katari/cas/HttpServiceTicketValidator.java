/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.Validate;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/** Validates STs and optionally retrieves PGT IOUs.
 *
 * Does not force https.
 */
public class HttpServiceTicketValidator {

  /** The url to use to validate a ticket.
   */
  private String casValidateUrl = null;

  private String proxyCallbackUrl;
  private String st = null;
  private String service;
  private String pgtIou;
  private String user;
  private String errorCode;
  private String errorMessage;
  private String entireResponse;
  private boolean renew = false;
  private boolean successfulAuthentication;

  /** Sets the CAS validation URL to use when validating tickets and retrieving
   * PGT IOUs.
   */
  public void setCasValidateUrl(final String x) {
    this.casValidateUrl = x;
  }

  /**
   * Gets the CAS validation URL to use when validating tickets and
   * retrieving PGT IOUs.
   */
  public String getCasValidateUrl() {
    return this.casValidateUrl;
  }

  /**
   * Sets the callback URL, owned logically by the calling service, to
   * receive the PGTid/PGTiou mapping.
   */
  public void setProxyCallbackUrl(final String x) {
    this.proxyCallbackUrl = x;
  }

  /** Sets the "renew" flag on authentication.
   *
   * When set to "true", authentication will only succeed if this was an
   * initial login (forced by the "renew" flag being set on login).
   */
  public void setRenew(final boolean b) {
    this.renew = b;
  }

  /**
   * Gets the callback URL, owned logically by the calling service, to
   * receive the PGTid/PGTiou mapping.
   */
  public String getProxyCallbackUrl() {
    return this.proxyCallbackUrl;
  }

  /** Sets the ST to validate.
   *
   * @param x the service ticket.
   */
  public void setServiceTicket(final String x) {
    this.st = x;
  }

  /**
   * Sets the service to use when validating.
   */
  public void setService(final String x) {
    this.service = x;
  }

  /**
   * Returns the strongly authenticated username.
   */
  public String getUser() {
    return this.user;
  }

  /**
   * Returns the PGT IOU returned by CAS.
   *
   * @return a string with the PGT IOU returned by CAS.
   */
  public String getPgtIou() {
    return this.pgtIou;
  }

  /** Checks if the authentication was successful.
   *
   * @return <tt>true</tt> if the most recent authentication attempted
   * succeeded, <tt>false</tt> otherwise.
   */
  public boolean isAuthenticationSuccesful() {
    return this.successfulAuthentication;
  }

  /**
   * Returns an error message if CAS authentication failed.
   *
   * @return a string with the error message.
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * Returns CAS's error code if authentication failed.
   *
   * @return a string with the error code.
   */
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * Retrieves CAS's entire response, if authentication was succsesful.
   */
  public String getResponse()  {
    return this.entireResponse;
  }

  //*********************************************************************
  //Actuator

  public void validate()
      throws IOException, SAXException, ParserConfigurationException {

    Validate.notNull(casValidateUrl, "The cas validate url cannot be null");
    Validate.notNull(st, "The ticket cannot be null");

    clear();
    StringBuffer sb = new StringBuffer();
    sb.append(casValidateUrl);
    if (casValidateUrl.indexOf('?') == -1) {
      sb.append('?');
    } else {
      sb.append('&');
    }
    sb.append("service=" + service + "&ticket=" + st);
    if (proxyCallbackUrl != null) {
      sb.append("&pgtUrl=" + proxyCallbackUrl);
    }
    if (renew) {
      sb.append("&renew=true");
    }
    String url = sb.toString();
    String response = SecureUrl.retrieve(url);
    this.entireResponse = response;

    // parse the response and set appropriate properties
    if (response != null) {
      XMLReader r =
        SAXParserFactory.newInstance().newSAXParser().getXMLReader();
      r.setFeature("http://xml.org/sax/features/namespaces", false);
      r.setContentHandler(newHandler());
      r.parse(new InputSource(new StringReader(response)));
    }
  }


  //*********************************************************************
  // Response parser

  protected DefaultHandler newHandler() {
    return new Handler();
  }

  protected class Handler extends DefaultHandler {

    //**********************************************
    // Constants

    private static final String AUTHENTICATION_SUCCESS =
      "cas:authenticationSuccess";
    private static final String AUTHENTICATION_FAILURE =
      "cas:authenticationFailure";
    private static final String PROXY_GRANTING_TICKET =
      "cas:proxyGrantingTicket";
    private static final String USER = "cas:user";

    //**********************************************
    // Parsing state

    private StringBuffer currentText = new StringBuffer();
    private boolean authenticationSuccess = false;
    private boolean authenticationFailure = false;
    private String pgtIou;
    private String errorCode;
    private String errorMessage;

    protected boolean getAuthenticationSuccess() {
      return authenticationSuccess;
    }

    protected String getCurrentText() {
      return currentText.toString().trim();
    }

    //**********************************************
    // Parsing logic

    public void startElement(final String ns, final String ln, final String qn,
        final Attributes a) {
      // clear the buffer
      currentText = new StringBuffer();

      // check outer elements
      if (qn.equals(AUTHENTICATION_SUCCESS)) {
        authenticationSuccess = true;
      } else if (qn.equals(AUTHENTICATION_FAILURE)) {
        authenticationFailure = true;
        errorCode = a.getValue("code");
        if (errorCode != null) {
          errorCode = errorCode.trim();
        }
      }
    }

    public void characters(final char[] ch, final int start,
        final int length) {
      // store the body, in stages if necessary
      currentText.append(ch, start, length);
    }

    public void endElement(final String ns, final String ln, final String qn)
        throws SAXException {
      if (authenticationSuccess) {
        if (qn.equals(USER)) {
          user = currentText.toString().trim();
        }
        if (qn.equals(PROXY_GRANTING_TICKET)) {
          pgtIou = currentText.toString().trim();
        }
      } else if (authenticationFailure && qn.equals(AUTHENTICATION_FAILURE)) {
        errorMessage = currentText.toString().trim();
      }
    }

    public void endDocument() throws SAXException {
      // save values as appropriate
      if (authenticationSuccess) {
        HttpServiceTicketValidator.this.user = user;
        HttpServiceTicketValidator.this.pgtIou = pgtIou;
        HttpServiceTicketValidator.this.successfulAuthentication = true;
      } else if (authenticationFailure) {
        HttpServiceTicketValidator.this.errorMessage = errorMessage;
        HttpServiceTicketValidator.this.errorCode = errorCode;
        HttpServiceTicketValidator.this.successfulAuthentication = false;
      } else {
        throw new SAXException("no indication of success or failure from CAS");
      }
    }
 }

  //*********************************************************************
  // Utility methods

  /**
   * Clears internally manufactured state.
   */
  protected void clear() {
   user = null;
   pgtIou = null;
   errorMessage = null;
   successfulAuthentication = false;
  }

  /** Is this HttpServiceTicketValidator configured to pass renew=true on the
   * ticket validation request?  @return true if renew=true on validation
   * reqeust, false otherwise.
   */
  public boolean isRenew() {
    return renew;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    sb.append(HttpServiceTicketValidator.class.getName());
    if (casValidateUrl != null) {
      sb.append(" casValidateUrl=[");
      sb.append(casValidateUrl);
      sb.append("]");
    }

    if (proxyCallbackUrl != null) {
      sb.append(" proxyCallbackUrl=[");
      sb.append(proxyCallbackUrl);
      sb.append("]");
    }
    if (st != null) {
      sb.append(" ticket=[");
      sb.append(st);
      sb.append("]");
    }
    if (service != null) {
      sb.append(" service=[");
      sb.append(service);
      sb.append("]");
    }
    if (pgtIou != null) {
      sb.append(" pgtIou=[");
      sb.append(pgtIou);
      sb.append("]");
    }
    if (user != null) {
      sb.append(" user=[");
      sb.append(user);
      sb.append("]");
    }
    if (errorCode != null) {
      sb.append(" errorCode=[");
      sb.append(errorCode);
      sb.append("]");
    }
    if (errorMessage != null) {
      sb.append(" errorMessage=[");
      sb.append(errorMessage);
      sb.append("]");
    }
    sb.append(" renew=");
    sb.append(renew);
    if (entireResponse != null) {
      sb.append(" entireResponse=[");
      sb.append(entireResponse);
      sb.append("]");
    }
    sb.append("]");
    return sb.toString();
  }
}

