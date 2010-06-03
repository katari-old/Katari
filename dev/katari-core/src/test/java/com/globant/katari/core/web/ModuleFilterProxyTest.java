/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import java.io.IOException;

import junit.framework.TestCase;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

import static org.easymock.EasyMock.*;

/* Tests the request dispatcher servlet. We use jmock here. With jdk-1.5 we
 * should have gone with easy mock instead.
 */
public class ModuleFilterProxyTest extends TestCase {

  private List<FilterMapping> filters;

  private HttpServletRequest request;

  private ServletResponse response;

  private FilterConfig config;

  private FilterChain chain;

  private int filterOrder = 0;

  private class TestFilter implements Filter {

    public boolean chain = true;

    public boolean called = false;

    public int order = 0;

    public String parameterValue = null;

    public int parameterCount = 0;

    public void init(final FilterConfig config) {
      parameterValue = config.getInitParameter("parameter");
      assertEquals(parameterValue,
          config.getServletContext().getInitParameter("parameter"));
      parameterCount = 0;
      Enumeration<?> enumeration = config.getInitParameterNames();
      while (enumeration.hasMoreElements()) {
        enumeration.nextElement();
        parameterCount ++;
      }
      called = true;
    }
    public void doFilter(final ServletRequest request, final ServletResponse
        response, final FilterChain filterChain) throws ServletException,
           IOException {
       called = true;
       if (order != 0) {
         throw new RuntimeException("doFilter called more than once");
       }
       ++ filterOrder;
       order = filterOrder;
       if (chain) {
         filterChain.doFilter(request, response);
       }
    }
    public void destroy() {
      called = true;
    }
  };

  private TestFilter filter1;
  private TestFilter filter2;
  private TestFilter filter3;

  protected void setUp() throws Exception {

    org.apache.log4j.PropertyConfigurator.configure(
        "src/test/resources/log4j.properties");

    filter1 = new TestFilter();
    filter2 = new TestFilter();
    filter3 = new TestFilter();

    filterOrder = 0;

    // Mocks the servlet request and response.
    request = createMock(HttpServletRequest.class);
    expect(request.getServletPath()).andReturn("/module/user/user.do");
    expect(request.getPathInfo()).andReturn("/id/100");
    expect(request.getServletPath()).andReturn("/module/user/user.do");
    expect(request.getPathInfo()).andReturn("/id/100");
    expect(request.getServletPath()).andReturn("/module/user/user.do");
    expect(request.getPathInfo()).andReturn("/id/100");
    replay(request);
    response = createNiceMock(ServletResponse.class);
    replay(response);

    // Mocks the servlet context.
    ServletContext context = createMock(ServletContext.class);
    expect(context.getServletContextName()).andReturn("/module");
    expectLastCall().anyTimes();

    // Under some conditions, the init method asks context to log the call.
    context.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(context);

    // Mocks the servlet config.
    config = createMock(FilterConfig.class);
    expect(config.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    replay(config);

    // A sample configuration mapping.
    filters = new LinkedList<FilterMapping>();
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter1)));
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter2)));
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter3)));
  }

  /* Tests if init is properly disptached to each filter.
  */
  public final void testInit() throws Exception {

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.init(config);

    assertTrue(filter1.called);
    assertTrue(filter2.called);
    assertTrue(filter3.called);
  }

  /* Tests if doFilter is properly disptached to each filter.
  */
  public final void testDoFilter_all() throws Exception {

    // Mocks the filter chain. Expect a call to doFilter.
    chain = createMock(FilterChain.class);
    chain.doFilter(request, response);
    replay(chain);

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.doFilter(request, response, chain);

    assertTrue(filter1.called);
    assertTrue(filter2.called);
    assertTrue(filter3.called);
    verify(chain);
  }

  /* Tests if doFilter is not chained if the first filter decides to.
   *
   * The first filter does not call chain.doFilter.
   */
  public final void testDoFilter_firstOnly() throws Exception {

    // Mocks the filter chain. We do not expect any call.
    chain = createMock(FilterChain.class);
    replay(chain);

    filter1.chain = false;

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.doFilter(request, response, chain);

    assertTrue(filter1.called);
    assertTrue(!filter2.called);
    assertTrue(!filter3.called);
    verify(chain);
  }

  /* Tests if doFilter is not chained if the second filter decides to.
   *
   * The first filter does not call chain.doFilter.
   */
  public final void testDoFilter_firstAndSecond() throws Exception {

    // Mocks the filter chain. We do not expect any call.
    chain = createMock(FilterChain.class);
    replay(chain);

    filter2.chain = false;

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.doFilter(request, response, chain);

    assertTrue(filter1.called);
    assertTrue(filter2.called);
    assertTrue(!filter3.called);
    verify(chain);
  }

  /* Tests if doFilter is called only on matching requests.
   */
  public final void testDoFilter_match() throws Exception {

    chain = createMock(FilterChain.class);
    chain.doFilter(request, response);
    replay(chain);

    filters = new LinkedList<FilterMapping>();
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter1)));
    filters.add(new FilterMapping("wont match.*",
          new FilterAndParameters(filter2)));
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter3)));

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.doFilter(request, response, chain);

    assertTrue(filter1.called);
    assertTrue(!filter2.called);
    assertTrue(filter3.called);
    verify(chain);
  }

  /* Tests that the filters are called in the correct priority order.
   */
  public final void testDoFilter_priority() throws Exception {

    chain = createMock(FilterChain.class);
    chain.doFilter(request, response);
    replay(chain);

    filters = new LinkedList<FilterMapping>();
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter1), 1));
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter2), 3));
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter3), 2));

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.doFilter(request, response, chain);

    assertTrue(filter1.called);
    assertEquals(1, filter1.order);
    assertTrue(filter2.called);
    assertEquals(3, filter2.order);
    assertTrue(filter3.called);
    assertEquals(2, filter3.order);
    verify(chain);
  }

  /* Tests that the parameters are passed to the filters.
   */
  public final void testDoFilter_parameters() throws Exception {

    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("parameter", "parameter value");
    parameters.put("name", "value");

    filters = new LinkedList<FilterMapping>();
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter1,
            parameters)));
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter2)));
    filters.add(new FilterMapping(".*", new FilterAndParameters(filter3)));

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.init(config);

    assertTrue(filter1.called);
    assertEquals("parameter value", filter1.parameterValue);
    assertEquals(2, filter1.parameterCount);
    assertTrue(filter2.called);
    assertEquals(null, filter2.parameterValue);
    assertEquals(0, filter2.parameterCount);
    assertTrue(filter3.called);
    assertEquals(null, filter3.parameterValue);
    assertEquals(0, filter3.parameterCount);
  }

  /* Tests if destroy is properly disptached to each filter.
  */
  public final void testDestroy() throws Exception {

    ModuleFilterProxy filter = new ModuleFilterProxy();
    filter.addFilters(filters);

    filter.destroy();

    assertTrue(filter1.called);
    assertTrue(filter2.called);
    assertTrue(filter3.called);
  }
}

