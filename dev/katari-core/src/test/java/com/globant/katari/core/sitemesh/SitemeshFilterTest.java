/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import java.util.Hashtable;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.*;

/* Tests the custom sitemesh filter.
 */
public class SitemeshFilterTest  extends TestCase {

  /* Tests the init method.
   */
  public final void testInit() throws Exception {

    // Creates an enumeration with all the parameter names.
    Hashtable<String, String> parameters = new Hashtable<String, String>();
    parameters.put("sitemesh.configfile",
        "com/globant/katari/core/sitemesh/sitemesh.xml");

    // Mocks the servlet context.
    ServletContext context = createMock(ServletContext.class);
    expect(context.getServletContextName()).andReturn("/sitemesh");
    expectLastCall().anyTimes();
    expect(context.getInitParameter("sitemesh.configfile")).andReturn(
        parameters.get("sitemesh.configfile"));
    expectLastCall().anyTimes();
    expect(context.getResourceAsStream(isA(String.class))).andReturn(null);
    expectLastCall().anyTimes();

    // Under some conditions, the init method asks context to log the call.
    context.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(context);

    // Mocks the servlet config.
    FilterConfig config;
    config = createMock(FilterConfig.class);
    expect(config.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    expect(config.getInitParameterNames()).andReturn(parameters.keys());
    expect(config.getInitParameter("sitemesh.configfile")).andReturn(
        parameters.get("sitemesh.configfile"));
    expectLastCall().anyTimes();
    expect(config.getInitParameter(isA(String.class))).andReturn(null);
    expectLastCall().anyTimes();
    replay(config);

    SitemeshFilter filter = new SitemeshFilter();

    filter.init(config);

    // We only expect that no exception is thrown here, otherwise, the filter
    // was not able to find sitemesh.xml and decorators.xml files.
  }
}

