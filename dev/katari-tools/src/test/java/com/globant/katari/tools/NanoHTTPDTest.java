/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

import java.util.Properties;

import junit.framework.TestCase;

import com.globant.katari.tools.NanoHTTPD;

/* Tests the cas service builder.
 */
public class NanoHTTPDTest extends TestCase {

  private NanoHTTPD server = null;

  private URLConnection connection = null;

  public void tearDown() {
    // We stop the server.
    if (server != null) {
      server.stop();
    }
  }

  /* Tests if the nanohttp server correctly returns what's expected.
   */
  public final void testServe_simple() throws IOException {

    server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            "retrieved data");
      }
    };
    assertEquals("retrieved data\n", getResponse(null, false));
  }

  public final void testServe_errorStatus() throws IOException {

    server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_NOTFOUND,
            NanoHTTPD.MIME_PLAINTEXT, null);
      }
    };
    try {
      getResponse(null, false);
      fail();
    } catch (FileNotFoundException ex) {
      // Test passed.
    }
  }

  public final void testServe_param() throws IOException {

    server = new NanoHTTPD(0) {
      // Implements a simple echo.
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            params.getProperty("echo"));
      }
    };
    assertEquals("hello\n", getResponse("echo=hello", false));
  }

  public final void testServe_header() throws IOException {

    server = new NanoHTTPD(0) {
      // Implements a simple with a header.
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        NanoHTTPD.Response response;
        response = new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT, "test");
        response.addHeader("echo", params.getProperty("echo"));
        return response;
      }
    };
    assertEquals("test\n", getResponse("echo=hello", false));
    assertEquals("hello", connection.getHeaderField("echo"));
  }

  public final void testServe_post() throws IOException {

    server = new NanoHTTPD(0) {
      // Implements a simple echo.
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            params.getProperty("echo"));
      }
    };
    assertEquals("hello\n", getResponse("echo=hello", true));
  }

  private final String getResponse(final String params, final boolean post)
    throws IOException {

    String requestUri = "http://localhost:" + server.getPort();
    if (params != null) {
      requestUri += "?" + params;
    }

    URL url = new URL(requestUri);
    connection = url.openConnection();
    connection.setRequestProperty("Connection", "close");

    if (post) {
      ((HttpURLConnection) connection).setRequestMethod("POST");
      ((HttpURLConnection) connection).setDoOutput(true);
      PrintWriter out = new PrintWriter(connection.getOutputStream());
      out.println(params);
      out.println("\r\n");
      out.println("\r\n");
      out.close();
    }

    BufferedReader reader = new BufferedReader(new
        InputStreamReader(connection.getInputStream()));
    String line;
    StringBuffer buf = new StringBuffer();
    while ((line = reader.readLine()) != null) {
      buf.append(line + "\n");
    }
    return buf.toString();
  }
}

