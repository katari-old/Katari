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

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.tools.NanoHTTPD;

/* Tests the cas service builder.
 */
public class NanoHTTPDTest {

  private NanoHTTPD server = null;

  private URLConnection connection = null;

  @After
  public void tearDown() {
    // We stop the server.
    if (server != null) {
      server.stop();
    }
  }

  @Test
  public final void serve_simple() throws IOException {

    server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            "retrieved data");
      }
    };
    assertThat(getResponse(null, false), is("retrieved data\n"));
  }

  @Test(expected = FileNotFoundException.class)
  public final void serve_errorStatus() throws IOException {

    server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_NOTFOUND,
            NanoHTTPD.MIME_PLAINTEXT, null);
      }
    };

    getResponse(null, false);
  }

  @Test
  public final void serve_param() throws IOException {

    server = new NanoHTTPD(0) {
      // Implements a simple echo.
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            params.getProperty("echo"));
      }
    };
    assertThat(getResponse("echo=hello", false), is("hello\n"));
  }

  @Test
  public final void serve_header() throws IOException {

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
    assertThat(getResponse("echo=hello", false), is("test\n"));
    assertThat(connection.getHeaderField("echo"), is("hello"));
  }

  @Test
  public final void serve_post() throws IOException {
 
    server = new NanoHTTPD(0) {
      // Implements a simple echo.
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT,
            params.getProperty("echo"));
      }
    };
    assertThat(getResponse("echo=hello", true), is("hello\n"));
  }

  @Test
  public final void serve_postNoBody() throws IOException {

    server = new NanoHTTPD(0) {
      // Implements a simple echo.
      public Response serve(final String uri, final String method, final
          Properties header, final Properties params) {
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT, "echo");
      }
    };
    assertThat(getResponse(null, true), is("echo\n"));
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
      if (params != null) {
        out.println(params);
        out.println("\r\n");
        out.println("\r\n");
      }
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

