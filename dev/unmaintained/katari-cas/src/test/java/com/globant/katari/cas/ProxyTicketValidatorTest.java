/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import java.util.Properties;

import junit.framework.TestCase;

import com.globant.katari.cas.ProxyTicketValidator;
import com.globant.katari.tools.NanoHTTPD;

/* Tests the cas ticket validator.
 */
public class ProxyTicketValidatorTest extends TestCase {

  private NanoHTTPD server = null;

  public void tearDown() {
    // We stop the server.
    if (server != null) {
      server.stop();
    }
  }

  /* Tests that the HttpServiceTicketValidatorTest correctly validates a
   * ticket.
   */
  public final void testValidate_success() throws Exception {

    server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties header, final Properties parms) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n"
            + "  <cas:authenticationSuccess>\n"
            + "    <cas:user>admin</cas:user>\n"
            + "    <cas:proxyGrantingTicket>SOME-TICKET</cas:proxyGrantingTicket>\n"
            + "    <cas:proxies>\n"
            + "      <cas:proxy>SOME-PROXY</cas:proxy>\n"
            + "    </cas:proxies>\n"
            + "  </cas:authenticationSuccess>\n"
            + "</cas:serviceResponse>");
      }
    };
    ProxyTicketValidator validator = new ProxyTicketValidator();
    validator.setCasValidateUrl("http://localhost:" + server.getPort());
    validator.setServiceTicket("TGT:SOME-TICKET");
    validator.validate();

    assertTrue(validator.isAuthenticationSuccesful());
    assertEquals("admin", validator.getUser());
    assertEquals("SOME-TICKET", validator.getPgtIou());
    assertEquals(1, validator.getProxyList().size());

    // toString is just for debugging. We do not really care, we only increment
    // coverage here.
    validator.toString();
  }
}

