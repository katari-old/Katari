/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import static org.easymock.EasyMock.*;

import static org.junit.Assert.*;
import org.junit.Test;

/** A test for the {@link ServletConfigWrapper}.
 *
 * @author pablo.saavedra
 */
public class ServletConfigWrapperTest {

  /** Tests that the wrapper correctly delegates all calls.
   */
  @Test
  public void delegateAll() throws Exception {

    ServletContext context = createMock(ServletContext.class);
    Enumeration<?> parameterNames = createMock(Enumeration.class);

    ServletConfig delegate = createMock(ServletConfig.class);
    expect(delegate.getInitParameter("name")).andReturn("value");
    expect(delegate.getInitParameterNames()).andReturn(parameterNames);
    expect(delegate.getServletContext()).andReturn(context);
    expect(delegate.getServletName()).andReturn("servletName");
    replay(delegate);

    ServletConfig config = new ServletConfigWrapper(delegate);

    assertEquals("value", config.getInitParameter("name"));
    assertEquals(parameterNames, config.getInitParameterNames());
    assertEquals(context, config.getServletContext());
    assertEquals("servletName", config.getServletName());

    verify(delegate);
  }

  /** Tests that the wrapper correctly returns the expected context.
   */
  @Test
  public void delegateContext() throws Exception {

    Enumeration<?> parameterNames = createMock(Enumeration.class);

    ServletConfig delegate = createMock(ServletConfig.class);
    expect(delegate.getInitParameter("name")).andReturn("value");
    expect(delegate.getInitParameterNames()).andReturn(parameterNames);
    expect(delegate.getServletName()).andReturn("servletName");
    replay(delegate);

    ServletContext context = createMock(ServletContext.class);

    ServletConfig config = new ServletConfigWrapper(delegate, context);

    assertEquals("value", config.getInitParameter("name"));
    assertEquals(parameterNames, config.getInitParameterNames());
    assertEquals(context, config.getServletContext());
    assertEquals("servletName", config.getServletName());

    verify(delegate);
  }
}

