/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.functionaltest;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/** Test that the ping servlet aswers.
 */
public class PingTest {

  @Test
  public final void testPing() throws Exception {

    HttpMethod method = null;
    try {
      HttpClient client = new HttpClient();
      method = new GetMethod(SimplePageVerifier.getBaseUrl() + "/ping");

      client.executeMethod(method);
      String responseBody = method.getResponseBodyAsString();
      assertTrue("Response doesn't match 'Loading spring context: SUCCESS'",
          responseBody.matches("(?s).*Loading spring context: SUCCESS.*"));
      assertTrue("Response doesn't match 'Application started successfully'",
          responseBody.matches("(?s).*Application started successfully.*"));
      assertTrue("Response doesn't match 'Database query: SUCCESS'",
          responseBody.matches("(?s).*Database query: SUCCESS.*"));
      assertTrue("Response doesn't match 'this is a development database'",
          responseBody.matches("(?s).*this is a development database.*"));
    } finally {
      method.releaseConnection();
    }
  }
}

