/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.mock.web.MockServletConfig;

import junit.framework.TestCase;

import freemarker.cache.TemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.WebappTemplateLoader;

import static org.easymock.classextension.EasyMock.*;

/** Tests the FreemarkerDecoratorServlet.
 */
public class FreemarkerDecoratorServletTest extends TestCase {

  /** Very naive subclass just to get access to the internal status.
   */
  private static class ServletWithTemplateLoader extends
    FreemarkerDecoratorServlet {

    private static final long serialVersionUID = 1L;
    
    public TemplateLoader[] loaders;

    public TemplateLoader getTemplateLoader() {
      return getConfiguration().getTemplateLoader();
    }

    protected TemplateLoader[] getLoaders() throws ServletException {
      loaders = super.getLoaders();
      return loaders;
    }
  }

  /* Prepare the servlet to configure the template paths.
   */
  private ServletConfig createServletConfig(final Hashtable<String, String>
      parameters) {
    // Mocks the servlet context.
    ServletContext context = createMock(ServletContext.class);
    expect(context.getServletContextName()).andReturn("/freemarker");
    expectLastCall().anyTimes();

    // Under some conditions, the init method asks context to log the call.
    context.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(context);

    // Mocks the servlet config.
    MockServletConfig config = new MockServletConfig(context);
    for (String name: parameters.keySet()) {
      config.addInitParameter(name, parameters.get(name));
    }

    return config;
  }

  /* Tests if the servlet is initialized correctly for one additional template
   * path and no debug related attributes.
   */
  public final void testInit_noDebug() throws Exception {

    // Creates an enumeration with all the parameter names.
    Hashtable<String, String> parameters = new Hashtable<String, String>();
    parameters.put("TemplatePath", "class://com/globant/katari/sitemesh/url1");
    parameters.put("AdditionalTemplatePaths", "file:///;/test");

    // Tests the servlet.
    ServletWithTemplateLoader servlet = new ServletWithTemplateLoader();
    servlet.init(createServletConfig(parameters));
    TemplateLoader defaultLoader = servlet.getTemplateLoader();

    // The only possible thing to validate here is if the loader is a
    // MultiTemplateLoader.
    assertTrue("Not a MultiTemplateLoader",
        defaultLoader instanceof MultiTemplateLoader);
    assertEquals(3, servlet.loaders.length);
    assertTrue(servlet.loaders[0] instanceof ClassTemplateLoader);
    assertTrue(servlet.loaders[1] instanceof FileTemplateLoader);
    assertTrue(servlet.loaders[2] instanceof WebappTemplateLoader);
  }

  /* Tests if the servlet is initialized correctly.
   */
  public final void testInit_debugAndDebugPrefix() throws Exception {

    // Creates an enumeration with all the parameter names.
    Hashtable<String, String> parameters = new Hashtable<String, String>();
    parameters.put("TemplatePath", "class://com/globant");
    parameters.put("DebugPrefix", "../katari-core/src/main/resources");
    parameters.put("debug", "true");

    // Tests the servlet.
    ServletWithTemplateLoader servlet = new ServletWithTemplateLoader();
    servlet.init(createServletConfig(parameters));
    TemplateLoader defaultLoader = servlet.getTemplateLoader();

    // The only possible thing to validate here is if the loader is a
    // MultiTemplateLoader.
    assertTrue("Not a MultiTemplateLoader",
        defaultLoader instanceof MultiTemplateLoader);
    assertEquals(2, servlet.loaders.length);
    assertTrue(servlet.loaders[0] instanceof FileTemplateLoader);
    assertTrue(servlet.loaders[1] instanceof ClassTemplateLoader);
  }

  /* Tests if the servlet is initialized correctly with multiple additional
   * template paths.
   */
  public final void testInit_debug() throws Exception {

    // Creates an enumeration with all the parameter names.
    Hashtable<String, String> parameters = new Hashtable<String, String>();
    parameters.put("TemplatePath", "class://com/globant/katari/sitemesh/url1");
    parameters.put("AdditionalTemplatePaths",
        "             class://com/globant/katari/core/web;\n"
        + "             class://com/globant/katari/core/spring\n"
        + "             ");
    parameters.put("AdditionalDebugPrefixes",
        "src/main/resources;src/main/resources");
    parameters.put("debug", "true");

    // Tests the servlet.
    ServletWithTemplateLoader servlet = new ServletWithTemplateLoader();
    servlet.init(createServletConfig(parameters));
    TemplateLoader defaultLoader = servlet.getTemplateLoader();

    // The only possible thing to validate here is if the loader is a
    // MultiTemplateLoader.
    assertTrue(defaultLoader instanceof MultiTemplateLoader);
    assertEquals(5, servlet.loaders.length);
    assertTrue(servlet.loaders[0] instanceof ClassTemplateLoader);
    assertTrue(servlet.loaders[1] instanceof FileTemplateLoader);
    assertTrue(servlet.loaders[2] instanceof ClassTemplateLoader);
    assertTrue(servlet.loaders[3] instanceof FileTemplateLoader);
    assertTrue(servlet.loaders[4] instanceof ClassTemplateLoader);
  }

  /* Tests if the servlet is initialized correctly with multiple additional
   * template paths.
   */
  public final void testInit_debugFalse() throws Exception {

    // Creates an enumeration with all the parameter names.
    Hashtable<String, String> parameters = new Hashtable<String, String>();
    parameters.put("TemplatePath", "class://com/globant/katari/sitemesh/url1");
    parameters.put("DebugPrefix", "../katari-core/src/main/resources");
    parameters.put("AdditionalTemplatePaths",
        "class://com/globant/katari/core/web");
    parameters.put("AdditionalDebugPrefixes",
        "src/main/resources;src/main/resources");
    parameters.put("debug", "false");

    // Tests the servlet.
    ServletWithTemplateLoader servlet = new ServletWithTemplateLoader();
    servlet.init(createServletConfig(parameters));
    TemplateLoader defaultLoader = servlet.getTemplateLoader();

    // The only possible thing to validate here is if the loader is a
    // MultiTemplateLoader.
    assertTrue(defaultLoader instanceof MultiTemplateLoader);
    assertEquals(2, servlet.loaders.length);
    assertTrue(servlet.loaders[0] instanceof ClassTemplateLoader);
    assertTrue(servlet.loaders[1] instanceof ClassTemplateLoader);
  }
}

