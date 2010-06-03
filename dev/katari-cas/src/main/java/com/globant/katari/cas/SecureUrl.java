/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

import org.apache.commons.lang.Validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A class housing some utility functions exposing secure URL validation and
 * content retrieval.
 *
 * TODO This is no longer 'secure' - see what should be done here.
 *
 * The rules are intended to be about as restrictive as a common browser with
 * respect to server-certificate validation.
 */
public final class SecureUrl {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(SecureUrl.class);

  /** A private default constuctor to avoid creationg instances of this
   * utility.
   */
  private SecureUrl() {
  }

  /** Retrieve the contents from the given URL as a String, assuming the
   * URL's server matches what we expect it to match.
   *
   * @param url The url to obtain the content from. It cannot be null.
   *
   * @return Returns the content read from the url.
   *
   * @throws IOException in case of error reading from the url.
   */
  public static String retrieve(final String url) throws IOException {
    Validate.notNull(url, "The url cannot be null");
    if (log.isTraceEnabled()) {
      log.trace("Entering retrieve(" + url + ")");
    }
    BufferedReader r = null;
    try {
      URL u = new URL(url);
/*
      if (!u.getProtocol().equals("https")){
        // IOException may not be the best exception we could throw here
        // since the problem is with the URL argument we were passed, not
        // IO. -awp9
        log.error("retrieve(" + url
          + ") on an illegal URL since protocol was not https.");
        throw new IOException("only 'https' URLs are valid for this method");
      }
*/
      URLConnection uc = u.openConnection();
      uc.setRequestProperty("Connection", "close");

      try {
        r = new BufferedReader(new InputStreamReader(uc.getInputStream()));
      } catch (IOException e) {
        if (log.isDebugEnabled() && uc instanceof HttpURLConnection) {
          r = new BufferedReader(new InputStreamReader(
                ((HttpURLConnection) uc).getErrorStream()));
          String line;
          StringBuffer buf = new StringBuffer();
          while ((line = r.readLine()) != null) {
            buf.append(line).append("\n");
          }
          log.debug("Error reading from url connection: " + buf.toString());
        }
        throw e;
      }
      String line;
      StringBuffer buf = new StringBuffer();
      while ((line = r.readLine()) != null) {
        buf.append(line).append("\n");
      }
      String result = buf.toString();
      if (log.isTraceEnabled()) {
        log.trace("Leaving retrieve with '" + result + "'");
      }
      return result;
    } finally {
      try {
        if (r != null) {
          r.close();
        }
      } catch (IOException e) {
        log.warn("Exception closing url connection - ignored", e);
      }
    }
  }
}

