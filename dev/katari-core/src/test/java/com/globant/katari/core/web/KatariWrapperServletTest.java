package com.globant.katari.core.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * A test for the {@link KatariWrapperServlet}.
 * @author pablo.saavedra
 */
public class KatariWrapperServletTest {

  private static final String MOCK_CONTEXT = "classpath:/com/globant/katari/core/spring/module.xml";

  /**
   * Tests the basic lifecycle of the servlet.
   */
  @Test
  public void initDelegateDestroy() throws Exception {
    WebApplicationContext mockContext;
    mockContext = EasyMock.createNiceMock(WebApplicationContext.class);
    EasyMock.expect(mockContext.getId()).andReturn("mock context").anyTimes();
    mockContext.publishEvent(EasyMock.isA(ContextRefreshedEvent.class));
    EasyMock.replay(mockContext);

    ServletContext sc = new MockServletContext();
    sc.setAttribute(
        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
        mockContext);
    MockServletConfig config = new MockServletConfig(sc, "mock");
    config.addInitParameter("contextConfigLocation", MOCK_CONTEXT);

    Servlet delegate = EasyMock.createMock(Servlet.class);
    delegate.init(validateServletConfig());
    delegate.service(EasyMock.isA(ServletRequest.class), EasyMock
        .isA(ServletResponse.class));
    delegate.destroy();
    EasyMock.replay(delegate);

    Map<String, String> overrides = new HashMap<String, String>();
    overrides.put("override", "true");

    KatariWrapperServlet wrapper = new KatariWrapperServlet(delegate);
    wrapper.setInitParameterOverrides(overrides);
    wrapper.init(config);
    wrapper.service(new MockHttpServletRequest(), new MockHttpServletResponse());
    wrapper.destroy();

    EasyMock.verify(mockContext, delegate);
  }

  /**
   * Reports a matcher to validate the delegate servlet initialization.
   * @return null
   */
  private static ServletConfig validateServletConfig() {
    EasyMock.reportMatcher(new IArgumentMatcher() {

      @SuppressWarnings("unchecked")
      public boolean matches(Object argument) {
        if (!(argument instanceof ServletConfig)) {
          return false;
        }
        ServletConfig config = (ServletConfig) argument;
        Assert.assertEquals("mock", config.getServletName());
        Enumeration<String> parameters = config.getInitParameterNames();
        Assert.assertTrue(parameters.hasMoreElements());
        String nextElement = parameters.nextElement();
        Assert.assertEquals("contextConfigLocation", nextElement);
        Assert.assertEquals(MOCK_CONTEXT, config.getInitParameter(nextElement));
        Assert.assertFalse(parameters.hasMoreElements());
        ServletContext sc = config.getServletContext();
        if (!(sc instanceof ScopedServletContext)) {
          return false;
        }
        ScopedServletContext ssc = (ScopedServletContext) sc;
        String addedParam = ssc.getInitParameter("override");
        Assert.assertEquals("true", addedParam);
        return true;
      }

      public void appendTo(StringBuffer buffer) {
        buffer.append("Matching with a delegating servlet " +
        		"config and a ScopedServletContext");
      }
    });
    return null;
  }
}
