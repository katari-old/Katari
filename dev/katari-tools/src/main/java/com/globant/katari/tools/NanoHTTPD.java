/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 server in Java
 *
 * <p> NanoHTTPD version 1.1,
 * Copyright &copy; 2001,2005-2007 Jarno Elonen (elonen@iki.fi,
 * http://iki.fi/elonen/)
 *
 * <p><b>Features + limitations: </b><ul>
 *
 *    <li> Only one Java file </li>
 *    <li> Java 1.1 compatible </li>
 *    <li> Released as open source, Modified BSD licence </li>
 *    <li> No fixed config files, logging, authorization etc. (Implement
 *    yourself if you need them.) </li>
 *    <li> Supports parameter parsing of GET and POST methods </li>
 *    <li> Supports both dynamic content and file serving </li>
 *    <li> Never caches anything </li>
 *    <li> Doesn't limit bandwidth, request time or simultaneous connections
 *    </li>
 *    <li> Default code serves files and shows all HTTP parameters and
 *    headers</li>
 *    <li> File server supports directory listing, index.html and index.htm
 *    </li>
 *    <li> File server does the 301 redirection trick for directories without
 *    '/'</li>
 *    <li> File server supports simple skipping for files (continue download)
 *    </li>
 *    <li> File server uses current directory as a web root </li>
 *    <li> File server serves also very long files without memory overhead
 *    </li>
 *    <li> Contains a built-in list of most common mime types </li>
 *    <li> All header names are converted lowercase so they don't vary between
 *    browsers/clients </li>
 *
 * </ul>
 *
 * <p><b>Ways to use: </b><ul>
 *
 *    <li> Subclass serve() and embed to your own program </li>
 *
 * </ul>
 *
 * See the end of the source file for distribution license
 * (Modified BSD licence)
 */
public abstract class NanoHTTPD {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(NanoHTTPD.class);

  /** The buffer size for reading and writing to the client.
   */
  private static final int BUFFER_SIZE = 2048;

  /** The end of line sequence.
   */
  private static final String EOL = "\r\n";

  // ==================================================
  // API parts
  // ==================================================

  /** Override this to customize the server.
   *
   * @param uri  Percent-decoded URI without parameters, for example
   * "/index.cgi"
   *
   * @param method "GET", "POST" etc.
   *
   * @param parms  Parsed, percent decoded parameters from URI and, in case of
   * POST, data.
   *
   * @param header Header entries, percent decoded
   *
   * @return HTTP response, see class Response for details
   */
  protected abstract Response serve(final String uri, final String method,
      final Properties header, final Properties parms);

  /** HTTP response.
   *
   * Return one of these from serve().
   */
  public static class Response {

    /** HTTP status code after processing, for example "200 OK", HTTP_OK.
     */
    private String status;

    /** MIME type of content, e.g. "text/html".
     */
    private String mimeType;

    /** Data of the response, may be null.
     */
    private InputStream data;

    /** Headers for the HTTP response.
     *
     * Use addHeader() to add lines.
     */
    private Properties header = new Properties();

    /** Default constructor: response = HTTP_OK, data = mime = 'null'.
     */
    public Response() {
      status = HTTP_OK;
    }

    /** Basic constructor.
     *
     * @param theStatus the response status code (for example, HTTP_OK).
     *
     * @param theMimeType the mime type of the response.
     *
     * @param theData the Data to send to the client.
     */
    public Response(final String theStatus, final String theMimeType, final
        InputStream theData) {
      status = theStatus;
      mimeType = theMimeType;
      data = theData;
    }

    /** Convenience method that makes an InputStream out of given text.
     *
     * @param theMimeType the mime type of the response.
     *
     * @param txt the Data to send to the client.
     */
    public Response(final String theMimeType, final String txt) {
      status = HTTP_OK;
      mimeType = theMimeType;
      data = new ByteArrayInputStream(txt.getBytes());
    }

    /**
     * Adds given line to the header.
     *
     * @param theName The header name.
     *
     * @param theValue The header value.
     */
    public void addHeader(final String theName, final String theValue) {
      header.put(theName, theValue);
    }

    /** HTTP status code after processing, for example "200 OK", HTTP_OK.
     *
     * @return The http status code.
     */
    public String getStatus() {
      return status;
    }


    /** MIME type of content, e.g. "text/html".
     *
     * @return The mime content type.
     */
    public String getMimeType() {
      return mimeType;
    }

    /** Data of the response, may be null.
     *
     * @return The data to send to the client.
     */
    public InputStream getData() {
      return data;
    }

    /** Headers for the HTTP response.
     *
     * @return the http headers.
     */
    public Properties getHeader() {
     return header;
    }
  }

  /** Some HTTP response status codes.
   */
  public static final String
    HTTP_OK = "200 OK",
    HTTP_REDIRECT = "301 Moved Permanently",
    HTTP_FORBIDDEN = "403 Forbidden",
    HTTP_NOTFOUND = "404 Not Found",
    HTTP_BADREQUEST = "400 Bad Request",
    HTTP_INTERNALERROR = "500 Internal Server Error",
    HTTP_NOTIMPLEMENTED = "501 Not Implemented";

  /** Common mime types for dynamic content.
   */
  public static final String
    MIME_PLAINTEXT = "text/plain",
    MIME_HTML = "text/html",
    MIME_DEFAULT_BINARY = "application/octet-stream";

  // ==================================================
  // Socket & server code
  // ==================================================

  /** The thread that waits for client requests.
   */
  private Thread serverThread = null;

  /** The connection established by a client.
   */
  private ServerSocket serverSocket = null;

  /** Starts a HTTP server to given port.
   *
   * @param port The port number to listen on.
   */
  public NanoHTTPD(final int port) {

    try {
     serverSocket = new ServerSocket(port);
     myTcpPort = serverSocket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException("Error creating server socket", e);
    }
    serverThread = new Thread(new Runnable() {
      public void run() {
        log.trace("Entering run");
        try {
          while (true) {
            Socket socket = serverSocket.accept();
            if (serverThread != null) {
              new HTTPSession(socket);
            } else {
              break;
            }
          }
        } catch (IOException e) {
          if (!serverSocket.isClosed()) {
            throw new RuntimeException("Error accepting connections", e);
          } else {
            serverSocket = null;
          }
        }
        log.trace("Leaving run");
      }
    });
    serverThread.setDaemon(true);
    serverThread.start();
  }

  /** Stops the server.
   *
   * This ends the serving thread and closes the listening socket.
   */
  public void stop() {
    if (serverThread != null) {
      Thread threadToStop = serverThread;
      serverThread = null;
      threadToStop.interrupt();
      try {
        serverSocket.close();
      } catch (IOException e) {
        throw new RuntimeException("Unable to close server socket", e);
      }
      try {
        threadToStop.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /** Handles one session, parsing the HTTP request and returning the response.
   */
  private class HTTPSession implements Runnable {

    /** Builds a session.
     *
     * @param s The socket that this session processes.
     */
    public HTTPSession(final Socket s) {
      mySocket = s;
      Thread t = new Thread(this);
      t.setDaemon(true);
      t.start();
    }

    /** Waits for client data and processes the request.
     */
    public void run() {
      try {
        InputStream is = mySocket.getInputStream();
        if (is == null) {
          return;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        // Read the request line
        StringTokenizer st = new StringTokenizer(in.readLine());
        if (!st.hasMoreTokens()) {
          sendError(HTTP_BADREQUEST,
              "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
        }

        String method = st.nextToken();

        if (!st.hasMoreTokens()) {
          sendError(HTTP_BADREQUEST,
              "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
        }

        String uri = decodePercent(st.nextToken());

        // Decode parameters from the URI
        Properties parms = new Properties();
        int qmi = uri.indexOf('?');
        if (qmi >= 0) {
          decodeParms(uri.substring(qmi + 1), parms);
          uri = decodePercent(uri.substring(0, qmi));
        }

        // If there's another token, it's protocol version,
        // followed by HTTP headers. Ignore version but parse headers.
        // NOTE: this now forces header names uppercase since they are
        // case insensitive and vary by client.
        Properties header = new Properties();
        if (st.hasMoreTokens()) {
          String line = in.readLine();
          while (line != null && line.trim().length() > 0) {
            int p = line.indexOf(':');
            header.put(line.substring(0, p).trim().toLowerCase(),
                line.substring(p + 1).trim());
            line = in.readLine();
          }
        }

        // If the method is POST, there may be parameters
        // in data section, too, read it:
        if (method.equalsIgnoreCase("POST")) {
          long contentLength = Long.MAX_VALUE;
          String contentLengthHeader = header.getProperty("content-length");
          if (contentLengthHeader != null) {
            try {
              contentLength = Integer.parseInt(contentLengthHeader);
            } catch (NumberFormatException ex) {
              log.error("Ignoring content-length header.", ex);
            }
          }
          String postLine = "";
          char[] buf = new char[BUFFER_SIZE];
          int read = in.read(buf);
          while (read >= 0 && contentLength > 0 && !postLine.endsWith(EOL)) {
            contentLength -= read;
            postLine += String.valueOf(buf, 0, read);
            if (contentLength > 0) {
              read = in.read(buf);
            }
          }
          postLine = postLine.trim();
          decodeParms(postLine, parms);
        }

        // Ok, now do the serve()
        Response r = serve(uri, method, header, parms);
        if (r == null) {
          sendError(HTTP_INTERNALERROR,
              "SERVER INTERNAL ERROR: Serve() returned a null response.");
        } else {
          sendResponse(r.getStatus(), r.getMimeType(), r.getHeader(),
              r.getData());
        }

        in.close();
      } catch (IOException ioe) {
        try {
          sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: "
              + ioe.getMessage());
        } catch (Exception t) {
          log.error("Error processing a request.", t);
        }
      } catch (InterruptedException ie) {
        // Thrown by sendError, ignore and exit the thread.
        log.error("Ignoring a thred interruption", ie);
      }
    }

    /** The radix for hexadecimal numbers.
     */
    private static final int HEX_RADIX = 16;

    /** Decodes the percent encoding scheme.
     *
     * @param str the string to decode, for example: "an+example%20string" gets
     * decoded to "an example string"
     *
     * @return returns the decoded string.
     *
     * @throws InterruptedException when another thread calls interrupt on this
     * thread.
     */
    private String decodePercent(final String str)
        throws InterruptedException {

      try {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
          char c = str.charAt(i);
          switch (c) {
            case '+':
              sb.append(' ');
              break;
            case '%':
              // Skip the %.
              ++i;
              sb.append((char) Integer.parseInt(
                    str.substring(i, i + 2), HEX_RADIX));
              ++i;
              break;
            default:
              sb.append(c);
              break;
          }
        }
        return new String(sb.toString().getBytes());
      } catch (Exception e) {
        sendError(HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.");
        return null;
      }
    }

    /** Decodes parameters in percent-encoded URI-format.
     *
     * (e.g.  "name=Jack%20Daniels&pass=Single%20Malt") and adds them to given
     * Properties.
     *
     * @param parms The parameters. If null, this operation does nothing.
     *
     * @param p The property to decode.
     *
     * @throws InterruptedException when another thread calls interrupt on this
     * thread.
     */
    private void decodeParms(final String parms, final Properties p)
      throws InterruptedException {
      if (parms == null) {
        return;
      }

      StringTokenizer st = new StringTokenizer(parms, "&");
      while (st.hasMoreTokens()) {
        String e = st.nextToken();
        int sep = e.indexOf('=');
        if (sep >= 0) {
          p.put(decodePercent(e.substring(0, sep)).trim(),
              decodePercent(e.substring(sep + 1)));
        }
      }
    }

    /**
     * Returns an error message as a HTTP response and
     * throws InterruptedException to stop furhter request processing.
     *
     * @param status The status to send to the client.
     *
     * @param msg The error message.
     *
     * @throws InterruptedException when another thread calls interrupt on this
     * thread.
     */
    private void sendError(final String status, final String msg) throws
        InterruptedException {
      sendResponse(status, MIME_PLAINTEXT, null,
          new ByteArrayInputStream(msg.getBytes()));
      throw new InterruptedException();
    }

    /** Sends given response to the socket.
     *
     * @param status The response status.
     *
     * @param mime The mime content type.
     *
     * @param header The headers to send.
     *
     * @param data The data to send to the client.
     */
    private void sendResponse(final String status, final String mime, final
        Properties header, final InputStream data) {

      try {
        if (status == null) {
          throw new Error("sendResponse(): Status can't be null.");
        }

        OutputStream out = mySocket.getOutputStream();
        PrintWriter pw = new PrintWriter(out);
        pw.print("HTTP/1.0 " + status + " \r\n");

        if (mime != null) {
          pw.print("Content-Type: " + mime + EOL);
        }

        if (header == null || header.getProperty("Date") == null) {
          pw.print("Date: " + gmtFrmt.format(new Date()) + EOL);
        }

        if (header != null) {
          Enumeration<?> e = header.keys();
          while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = header.getProperty(key);
            pw.print(key + ": " + value + EOL);
          }
        }

        pw.print(EOL);
        pw.flush();

        if (data != null) {
          byte[] buff = new byte[BUFFER_SIZE];
          while (true) {
            int read = data.read(buff, 0, BUFFER_SIZE);
            if (read <= 0) {
              break;
            }
            out.write(buff, 0, read);
          }
        }
        out.flush();
        out.close();
        if (data != null) {
          data.close();
        }
      } catch (IOException ioe) {
        // Couldn't write? No can do.
        try {
          mySocket.close();
        } catch (Exception t) {
          // We ignore, but log, the exception.
          log.error("Error writing to client.", t);
        }
      }
    }

    /** The socket for this session.
     */
    private Socket mySocket;
  };

  /** The tcp port that the server is listening on.
   */
  private int myTcpPort;

  /** Returns the port the server is listening on.
   *
   * @return a port number.
   */
  public int getPort() {
    return myTcpPort;
  }

  /** GMT date formatter.
   */
  private static java.text.SimpleDateFormat gmtFrmt;

  static {
    gmtFrmt = new java.text.SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'",
        Locale.US);
    gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /** The distribution licence.
   */
  public static final String LICENCE =
    "Copyright (C) 2001,2005 by Jarno Elonen <elonen@iki.fi>\n"
    + "\n"
    + "Redistribution and use in source and binary forms, with or without\n"
    + "modification, are permitted provided that the following conditions\n"
    + "are met:\n"
    + "\n"
    + "Redistributions of source code must retain the above copyright"
    + " notice,\n"
    + "this list of conditions and the following disclaimer. Redistributions"
    + " in\n"
    + "binary form must reproduce the above copyright notice, this list of\n"
    + "conditions and the following disclaimer in the documentation and/or"
    + " other\n"
    + "materials provided with the distribution. The name of the author may"
    + " not\n"
    + "be used to endorse or promote products derived from this software"
    + " without\n"
    + "specific prior written permission. \n"
    + " \n"
    + "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n"
    + "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED"
    + " WARRANTIES\n"
    + "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE"
    + " DISCLAIMED.\n"
    + "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
    + "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,"
    + " BUT\n"
    + "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF"
    + " USE,\n"
    + "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n"
    + "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
    + "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n"
    + "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
}

