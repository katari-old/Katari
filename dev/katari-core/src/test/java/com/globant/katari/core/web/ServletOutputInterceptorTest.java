/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class ServletOutputInterceptorTest {

  /* Tests that the writer correctly writes to the output stream and that the
   * original output stream is not touched.
   */
  @Test
  public final void testWriter() throws Exception {
    HttpServletResponse response = createMock(HttpServletResponse.class);
    expect(response.getCharacterEncoding()).andReturn("utf-8");
    expectLastCall().times(2);
    replay(response);

    final OutputStream stream = new ByteArrayOutputStream();
    ServletOutputInterceptor wrapper = new ServletOutputInterceptor(response) {
      protected OutputStream createOutputStream() {
        return stream;
      }
    };
    wrapper.getWriter().print("test");
    wrapper.flushBuffer();

    assertEquals("test", stream.toString());
  }

  /* Tests that the writer correctly writes to both the output stream and to
   * the original output stream.
   */
  @Test
  public final void testWriter_writeThrough() throws Exception {

    final OutputStream original = new ByteArrayOutputStream();
    ServletOutputStream outputStream = new ServletOutputStream() {
      public void write(final int theByte) throws IOException {
        original.write(theByte);
      }
    };

    HttpServletResponse response = createMock(HttpServletResponse.class);
    expect(response.getCharacterEncoding()).andReturn("utf-8");
    expect(response.getOutputStream()).andReturn(outputStream);
    response.flushBuffer();
    replay(response);

    final OutputStream stream = new ByteArrayOutputStream();
    ServletOutputInterceptor wrapper = new ServletOutputInterceptor(response,
        true) {
      protected OutputStream createOutputStream() {
        return stream;
      }
    };
    wrapper.getWriter().print("test");
    wrapper.flushBuffer();

    assertEquals("test", stream.toString());
    assertEquals(original.toString(), stream.toString());
    verify(response);
  }

  /* Tests that two successive calls to getOutputStream succeeds.
   */
  @Test
  public final void testGetOutpuStream() throws Exception {
    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    expect(response.getCharacterEncoding()).andReturn("utf-8");
    replay(response);

    WebletResponseWrapper wrapper = new WebletResponseWrapper(response);
    ServletOutputStream stream;
    stream = wrapper.getOutputStream();
    assertNotNull(stream);
    stream = wrapper.getOutputStream();
    assertNotNull(stream);
  }

  /* Tests that two successive calls to getWriter succeeds.
   */
  @Test
  public final void testGetWriter() throws Exception {
    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    expect(response.getCharacterEncoding()).andReturn("utf-8");
    replay(response);

    WebletResponseWrapper wrapper = new WebletResponseWrapper(response);
    PrintWriter writer;
    writer = wrapper.getWriter();
    assertNotNull(writer);
    writer = wrapper.getWriter();
    assertNotNull(writer);
  }

  /* Tests that calling getOutputStream followed by getWriter fails.
   */
  @Test(expected = IllegalStateException.class)
  public final void testGetWriter_afterOutputStream() throws Exception {
    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    expect(response.getCharacterEncoding()).andReturn("utf-8");
    replay(response);

    WebletResponseWrapper wrapper = new WebletResponseWrapper(response);
    wrapper.getOutputStream();
    wrapper.getWriter();
  }

  /* Tests that calling getWriter followed by getOutputStream fails.
   */
  @Test(expected = IllegalStateException.class)
  public final void testGetOutpuStream_afterGetWriter() throws Exception {
    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    expect(response.getCharacterEncoding()).andReturn("utf-8");
    replay(response);

    WebletResponseWrapper wrapper = new WebletResponseWrapper(response);
    wrapper.getWriter();
    wrapper.getOutputStream();
  }
}

