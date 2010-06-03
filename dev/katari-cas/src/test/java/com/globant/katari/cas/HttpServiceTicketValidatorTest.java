/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import java.util.Properties;

import junit.framework.TestCase;

import com.globant.katari.cas.HttpServiceTicketValidator;
import com.globant.katari.tools.NanoHTTPD;

/* Tests the cas ticket validator.
 */
public class HttpServiceTicketValidatorTest extends TestCase {

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
    HttpServiceTicketValidator validator = new HttpServiceTicketValidator();
    validator.setCasValidateUrl("http://localhost:" + server.getPort());
    validator.setServiceTicket("TGT:SOME-TICKET");
    validator.validate();

    assertTrue(validator.isAuthenticationSuccesful());
    assertEquals("admin", validator.getUser());
    assertEquals("SOME-TICKET", validator.getPgtIou());

    // toString is just for debugging. We do not really care, we only increment
    // coverage here.
    validator.toString();
  }

  /* Tests that the HttpServiceTicketValidatorTest correctly fails an invalid
   * ticket.
   */
  public final void testValidate_error() throws Exception {

    NanoHTTPD server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties header, final Properties parms) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n"
            + "  <cas:authenticationFailure code='SOME-CODE'>\n"
            + "    SOME-ERROR\n"
            + "  </cas:authenticationFailure>\n"
            + "</cas:serviceResponse>\n");
      }
    };
    HttpServiceTicketValidator validator = new HttpServiceTicketValidator();
    validator.setCasValidateUrl("http://localhost:" + server.getPort());
    validator.setServiceTicket("TGT:SOME-TICKET");
    validator.setService("http://some-service");
    validator.setProxyCallbackUrl("http://some-proxy-callback");
    validator.setRenew(true);
    validator.validate();

    assertFalse(validator.isAuthenticationSuccesful());
    assertEquals("SOME-CODE", validator.getErrorCode());
    assertEquals("SOME-ERROR", validator.getErrorMessage());

    // toString is just for debugging. We do not really care, we only increment
    // coverage here.
    validator.toString();
  }
}

