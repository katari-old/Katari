package com.globant.katari.core.web;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.globant.katari.core.SpringTestUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class ExceptionHandlerFilterTest {

  private static final String REQUEST_PARAMETER =
      ExceptionHandlerFilter.SHOW_STACK_TRACE_REQUEST_PARAMETER;
  HttpServletRequest request;
  HttpServletResponse response;
  FilterChain chain = new FilterChainMock();
  Configuration configuration;
  ExceptionHandlerFilter filter;
  Template template;
  MockPrintWriter writer;
  ByteArrayOutputStream stream;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    request = createMock(HttpServletRequest.class);
    response = createMock(HttpServletResponse.class);
    configuration = (Configuration) SpringTestUtils.getBean(
        "exceptionHandlerFilter.freemarkerConfig");
    template = createMock(Template.class);
    stream = new ByteArrayOutputStream();
    writer = new MockPrintWriter(stream);
  }

  @After
  public void after() throws Exception {
    verify(request);
    verify(response);
  }

  @Test
  public void testDoFilter() throws Exception {
    expect(request.getParameter(REQUEST_PARAMETER)).andReturn("true");

    expect(response.getWriter()).andReturn(writer);
    response.flushBuffer();

    replay(request);
    replay(response);

    filter = new ExceptionHandlerFilter(configuration, "error.ftl", true);

    filter.doFilter(request, response, chain);

    String result = writer.getResult();
    if (!result.contains("1 - Message")) {
      fail("Template is not parsing the exception message");
    }
  }

  private static class FilterChainMock implements FilterChain {
    public void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, ServletException {
      throw new RuntimeException("1 - Message",
          new RuntimeException("2 - Message",
              new RuntimeException("3 - Message"))) ;
    }
  }

  private static class MockPrintWriter extends PrintWriter {

    StringBuffer sb = new StringBuffer();

    public MockPrintWriter(ByteArrayOutputStream stream) {
      super(stream);
    }

    @Override
    public void write(String s) {
      sb.append(s);
      super.write(s);
    }

    public String getResult() {
      return sb.toString();
    }
  }

}
