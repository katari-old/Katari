/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A response wrapper that intercepts the binary data written by the servlets.
 */
public abstract class ServletOutputInterceptor extends
    HttpServletResponseWrapper {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(ServletOutputInterceptor.class);

  /** Indicates whether the data is written through to the original output
   * stream.
   */
  private boolean writeThrough;

  /** A servlet output stream that makes the written data as a string.
  */
  private static final class OutputStreamWrapper extends ServletOutputStream {

    /** An output stream where the servlets store its outputs.
     *
     * This is never null.
     */
    private OutputStream outputStream;

    /** The original output stream.
     *
     * If this is not null, output will be written both here and to
     * outputStream.
     */
    private OutputStream originalOutputStream;

    /** Creates a servlet output stream that writes to the provided stream.
     *
     * @param output The output stream, it cannot be null.
     */
    private OutputStreamWrapper(final OutputStream output) {
      Validate.notNull(output, "The output stream cannot be null");
      outputStream = output;
    }

    /** Creates a servlet output stream that writes to both provided streams.
     *
     * @param output The output stream, it cannot be null.
     *
     * @param originalOutput The original output stream, it cannot be null.
     */
    private OutputStreamWrapper(final OutputStream output, final OutputStream
        originalOutput) {
      Validate.notNull(output, "The output stream cannot be null");
      Validate.notNull(originalOutput,
          "The original output stream cannot be null");
      outputStream = output;
      originalOutputStream = originalOutput;
    }

    /** Writes the specified byte to the output stream.
     *
     * @param theByte The byte to write.
     *
     * @throws IOException in case of a write error.
     */
    @Override
    public void write(final int theByte) throws IOException {
      if (originalOutputStream != null) {
        originalOutputStream.write(theByte);
      }
      outputStream.write(theByte);
    }
  };

  /** The output strean that intercepts the data written by the servlets.
   *
   * It is null until the user requests the output stream through
   * getOutputStream.
   */
  private OutputStreamWrapper outputStreamWrapper = null;

  /** A writter that intercepts characted data written by the servlets and
   * writes it to the outputStreamWrapper.
   *
   * It is null until the client requests the writer through getWriter.
   */
  private PrintWriter writer = null;

  /** Creates a new ServletOutputInterceptor.
   *
   * @param response The wrapped response. It cannot be null.
   */
  public ServletOutputInterceptor(final HttpServletResponse response) {
    super(response);
    Validate.notNull(response, "The response cannot be null");
  }

  /** Creates a new ServletOutputInterceptor that can write the output to the
   * original stream.
   *
   * @param response The wrapped response. It cannot be null.
   *
   * @param shouldWriteThrough Indicates whether the data is written through to
   * the original output stream.
   */
  public ServletOutputInterceptor(final HttpServletResponse response, final
      boolean shouldWriteThrough) {
    super(response);
    Validate.notNull(response, "The response cannot be null");
    writeThrough = shouldWriteThrough;
  }

  /** Called when ServletOutputInterceptor must create the output stream.
   *
   * Subclasses must implement this to return the output stream that receives
   * the written data.
   *
   * @return The output stream. Implementations must not return null.
   */
  protected abstract OutputStream createOutputStream();

  /** {@inheritDoc}
   */
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    log.trace("Entering getOutputStream");
    if (writer != null) {
      throw new IllegalStateException("getWriter() has already been"
          + " called for this response");
    }
    // The client can request the output stream any times he wants.
    if (outputStreamWrapper == null) {
      initializeOutputStream();
    }
    log.trace("Leaving getOutputStream");
    return outputStreamWrapper;
  }

  /** {@inheritDoc}
   */
  @Override
  public PrintWriter getWriter() throws IOException {
    log.trace("Entering getWriter");
    if (writer == null) {
      if (outputStreamWrapper != null) {
        throw new IllegalStateException("getOutputStream() has already been"
          + " called for this response");
      }
      initializeOutputStream();
      String encoding = getResponse().getCharacterEncoding();
      if (encoding == null) {
        writer = new PrintWriter(outputStreamWrapper);
      } else {
        writer = new PrintWriter(new OutputStreamWriter(
              outputStreamWrapper, encoding));
      }
    }
    log.trace("Leaving getWriter");
    return writer;
  }

  /** Initializes the wrapped output stream.
   *
   * @throws IOException in case of error.
   */
  private void initializeOutputStream() throws IOException {
    if (writeThrough) {
      outputStreamWrapper = new OutputStreamWrapper(createOutputStream(),
          getResponse().getOutputStream());
    } else {
      outputStreamWrapper = new OutputStreamWrapper(createOutputStream());
    }
  }

  /** {@inheritDoc}
   */
  @Override
  public void flushBuffer() throws IOException {
    if (writer != null) {
      writer.flush();
    }
    if (outputStreamWrapper != null) {
      outputStreamWrapper.flush();
    }
  }
}

