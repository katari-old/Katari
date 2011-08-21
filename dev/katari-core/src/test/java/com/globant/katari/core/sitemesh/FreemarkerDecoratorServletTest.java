/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.view.AbstractTemplateView;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import freemarker.cache.TemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

import static org.easymock.EasyMock.*;

/** Tests the FreemarkerDecoratorServlet.
 */
public class FreemarkerDecoratorServletTest {

  private MessageSource messageSource = createNiceMock(MessageSource.class);

  /** Very naive subclass just to get access to the internal status.
   */
  private class ServletWithTemplateLoader extends FreemarkerDecoratorServlet {

    public ServletWithTemplateLoader() {
      super(messageSource);
    }

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
    MockServletContext context = new MockServletContext();
    context.setServletContextName("/freemarker");

    GenericWebApplicationContext applicationContext;
    applicationContext = new GenericWebApplicationContext();
    applicationContext.refresh();
    context.setAttribute(
        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
        applicationContext);

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
  @Test
  public final void init_noDebug() throws Exception {

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
  @Test
  public final void init_debugAndDebugPrefix() throws Exception {

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
  @Test
  public final void init_debug() throws Exception {

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
  @Test
  public final void init_debugFalse() throws Exception {

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

  @Test
  public void preTemplateProcess() throws Exception {
    ServletWithTemplateLoader servlet = new ServletWithTemplateLoader();
    servlet.init(createServletConfig(new Hashtable<String, String>()));
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    File ftl = new File(
        "src/main/resources/com/globant/katari/core/web/katari.ftl");
    Template template = new Template("t", new FileReader(ftl), null, null);
    SimpleHash model = new SimpleHash();
    servlet.preTemplateProcess(request, response, template, model);
    assertThat(model.get(
        AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE),
        is(notNullValue()));
  }
}

