/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.functionaltest;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/** Test that the ping servlet aswers.
 */
public class PingTest {

  @Test
  public final void ping() throws Exception {

    HttpClient client = null;
    try {
      HttpGet method = null;
      client = new DefaultHttpClient();
      method = new HttpGet(SimplePageVerifier.getBaseUrl() + "/ping"); 
      HttpResponse response = client.execute(method);
      String responseBody = EntityUtils.toString(response.getEntity());

      assertTrue("Response doesn't match 'Loading spring context: SUCCESS'",
          responseBody.matches("(?s).*Loading spring context: SUCCESS.*"));
      assertTrue("Response doesn't match 'Application started successfully'",
          responseBody.matches("(?s).*Application started successfully.*"));
      assertTrue("Response doesn't match 'Database query: SUCCESS'",
          responseBody.matches("(?s).*Database query: SUCCESS.*"));
      assertTrue("Response doesn't match 'this is a development database'",
          responseBody.matches("(?s).*this is a development database.*"));
    } finally {
      if (client != null) {
        client.getConnectionManager().shutdown();
      }
    }
  }
}

