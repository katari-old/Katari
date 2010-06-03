/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import com.globant.katari.cas.SecureUrl;
import com.globant.katari.tools.NanoHTTPD;

/* Tests the cas service builder.
 */
public class SecureUrlTest extends TestCase {

  private NanoHTTPD server = null;

  public void tearDown() {
    // We stop the server.
    if (server != null) {
      server.stop();
    }
  }

  /* Tests that the SecureUrl obtains data from an url. This test starts an
   * embeded web server and requests data from it.
   */
  public final void testRetrieve() throws IOException {

    server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties header, final Properties parms) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            "retrieved data");
      }
    };

    String result = SecureUrl.retrieve("http://localhost:" + server.getPort());

    // SecureUrl.retrieve adds a new line at the end.
    assertEquals("retrieved data\n", result);
  }
}

