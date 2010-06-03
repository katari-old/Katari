/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.WebUtils;

import org.springframework.mock.web.MockServletContext;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/** Engine to test a freemarker template through unit test.
 *
 * This class makes it possible to test a freemarker template without starting
 * an application container.<br>
 *
 * The basic usage consists of creating an instance of this class with the path
 * where the test template will be loaded and the model map (a map of variables
 * that will be made available to the temaplate). Then call runAndValidate with
 * the template name and two optional lists of regular expresions. This
 * operation returns the processed template.<br>
 *
 * If a validation error occurs, runAndValidate throws a junit assertion
 * error.<br>
 *
 * For convenience, runAndValidate also saves the output of the template to a
 * file in target/freemarker-test directory. The name of this file is derived
 * from the call stack: it looks for the class and method that called
 * runAndValidate to generate the file name.<br>
 *
 * This class also supports the spring.ftl and katari.ftl macro libraries, and
 * spring binding errors. To add binding errors to be used in the template
 * do:<br>
 *
 * <pre>
 *
 * BeanPropertyBindingResult result;
 *
 * result = new BeanPropertyBindingResult(command, "command");
 *
 * result.addError(new ObjectError("command.profile.name", new String[]{"1"},
 * null, "This is the error message for the user name"));
 *
 * model.putAll(result.getModel());
 *
 * </pre>
 *
 * @author jose.dominguez
 */
public class FreemarkerTestEngine {

  /** The number of template loaders configured.
   */
  private static final int DEFAULT_LOADERS = 2;

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(FreemarkerTestEngine.class);

  /** Freemarker configuration.
   *
   * It is never null.
   */
  private Configuration cfg;

  /** Map containing the model.
   *
   * It is never null.
   */
  private Map<String, Object> model = new HashMap<String, Object>();

  /** Constructor.
   *
   * Builds a FreemarkerTestEngine with the default locale, Locale.ENGLISH.
   *
   * @param templatePath The path where the template to test will be loaded
   * from. It cannot be null.
   *
   * @param theModel The model to be processed with the template. It cannot be
   * null.
   *
   * @throws Exception in case of error.
   */
  public FreemarkerTestEngine(final String templatePath,
      final Map<String, Object> theModel) throws Exception {
    this(templatePath, Locale.ENGLISH, theModel);
  }

  /** Constructor.
   *
   * @param templatePath The path where the template to test will be loaded
   * from. It cannot be null.
   *
   * @param locale Request locale. It cannot be null.
   *
   * @param theModel The model to be processed with the template. It cannot be
   * null.
   *
   * @throws Exception in case of error.
   */
  public FreemarkerTestEngine(final String templatePath, final Locale locale,
      final Map<String, Object> theModel) throws Exception {
    this(new String [] {templatePath}, locale, theModel);
  }

  /** Constructor.
   *
   * Builds a FreemarkerTestEngine with the default locale, Locale.ENGLISH.
   *
   * @param templatePaths The paths where the template to test will be loaded
   * from. It is useful when a template imports other templates that are in a
   * different path than the one under test. It cannot be null.
   *
   * @param theModel The model to be processed with the template. It cannot be
   * null.
   *
   * @throws Exception in case of error.
   */
  public FreemarkerTestEngine(final String[] templatePaths,
      final Map<String, Object> theModel)
    throws Exception {
    this(templatePaths, Locale.ENGLISH, theModel);
  }

  /** Constructor.
   *
   * @param templatePaths The paths where the template to test will be loaded
   * from. It is useful when a template imports other templates that are in a
   * different path than the one under test. It cannot be null.
   *
   * @param locale Request locale. It cannot be null.
   *
   * @param theModel The model to be processed with the template. It cannot be
   * null.
   *
   * @throws Exception in case of error.
   */
  public FreemarkerTestEngine(final String[] templatePaths, final Locale locale,
      final Map<String, Object> theModel) throws Exception {

    Validate.notNull(templatePaths, "templatePaths cannot be null");
    Validate.notNull(locale, "locale cannot be null");
    Validate.notNull(theModel, "theModel cannot be null");

    // Creates a Freemarker configuration
    cfg = new Configuration();

    model.put("baseweb", "/katari");
    model.putAll(theModel);

    // Setting loaders
    TemplateLoader[] allLoaders;
    allLoaders = new TemplateLoader[DEFAULT_LOADERS + templatePaths.length];
    int i = 0;
    for (i = 0; i < templatePaths.length; ++i) {
      String path = templatePaths[i];
      allLoaders[i] = new ClassTemplateLoader(this.getClass(), path);
    }
    // path for spring.ftl
    allLoaders[i] = new ClassTemplateLoader(this.getClass(),
        "/org/springframework/web/servlet/view/freemarker");
    // path for katari.ftl
    allLoaders[i + 1] = new ClassTemplateLoader(this.getClass(),
        "/com/globant/katari/core/web");
    cfg.setTemplateLoader(new MultiTemplateLoader(allLoaders));

    HttpServletRequest mockServletRequest = buildMockServletRequest(locale);

    cfg.setSharedVariable("springMacroRequestContext",
        new RequestContext(mockServletRequest, model));
    cfg.setSharedVariable("request", mockServletRequest);
    cfg.setObjectWrapper(new DefaultObjectWrapper());
  }

  /** Processes a template and validates it with regular expressions.
   *
   * If one of the regular expressions fails to match, this method throws an
   * AssertionFailedError, in other words, it uses junit assertions to check
   * for the regular expressions.
   *
   * @param templateName The name of the template to test. It cannot be null.
   *
   * @param valid List of regular expressions to match against the result of
   * processing the template. It cannot be null.
   *
   * @param invalid List of regular expressions that must not match against the
   * result of processing the template. It cannot be null.
   *
   * @return Returns a string with the result of expanding the template.
   *
   * @throws Exception in case of error.
   */
  public String runAndValidate(final String templateName, final List<String>
      valid, final List<String> invalid) throws Exception {
    Validate.notNull(templateName, "The template name cannot be null.");
    Validate.notNull(valid, "The list of valid regular expressions cannot be"
        + " null.");
    Validate.notNull(valid, "The list of invalid regular expressions cannot be"
        + " null.");

    Template template = cfg.getTemplate(templateName);
    Writer out = new StringWriter();
    template.process(model, out);
    String result = out.toString();
    dumpResult(result);
    log.debug("Output of processing '" + templateName + "':\n" + result);
    for (String regex : valid) {
      Pattern pattern;
      pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
      Matcher matcher = pattern.matcher(result);
      Assert.assertTrue("The valid regular expression '" + regex + "' did not"
          + " match the template output.", matcher.matches());
    }
    for (String regex : invalid) {
      Pattern pattern;
      pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
      Matcher matcher = pattern.matcher(result);
      Assert.assertFalse("The invalid regular expression '" + regex
          + "' matched the template output.", matcher.matches());
    }
    return result;
  }

  /** Dumps the result of processing the freemarker template to a file.
   *
   * It dumps the file to target/freemarker-test directory. It builds the file
   * name from the provided stack trace. The file name is built with the class
   * name (including the package), and the name of the method.
   *
   * To choose the specific stack trace entry, this method simply finds the
   * call to runAndValidate method and picks the next one. This implies that
   * the name of the file is generated based on the method that called
   * runAndValidate.
   *
   * @param output The freemarker generated output. It cannot be null.
   *
   * @throws Exception in case of error.
   */
  private void dumpResult(final String output) throws Exception {
    Validate.notNull(output, "The output cannot be null");

    // thisName identifies the stack trace entry of the runAndValidate method.
    String thisName;
    thisName = FreemarkerTestEngine.class.getName() + ".runAndValidate.html";

    // Creates the output directory if it does not exist.
    File directory = new File("target/freemarker-test");
    directory.mkdirs();

    // Iterates the stack trace of the current thread.
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    for (int i = 0; i < stack.length; ++i) {
      String name = buildOutputName(stack[i]);
      // Finds the runAndValidate method call in the stack trace.
      if (name.equals(thisName)) {
        // Writes the output to the file.
        name = buildOutputName(stack[i + 1]);
        FileOutputStream stream = null;
        try {
          stream = new FileOutputStream("target/freemarker-test/" + name);
          stream.write(output.getBytes());
        } finally {
          if (stream != null) {
            stream.close();
          }
        }
      }
    }
  }

  /** Builds an output file name base on a stack trace element.
   *
   * The name is the fully qualified class name, followed by a dot, followed by
   * the method name, with an '.html' extension.
   *
   * @param entry The stack trace element used to generate de file name. It
   * cannot be null.
   *
   * @return a string with the output file name, never returns null.
   */
  private String buildOutputName(final StackTraceElement entry) {
    Validate.notNull(entry, "The stack trace entry cannot be null.");
    return entry.getClassName() + "." + entry.getMethodName() + ".html";
  }

  /**
   * Creates a mocked HttpServletRequest.
   *
   * @param locale Request Locale, it cannot be null.
   *
   * @return the mocked HttpServletRequest. It never returns null.
   */
  private HttpServletRequest buildMockServletRequest(final Locale locale) {
    Validate.notNull(locale, "locale cannot be null");

    MockServletContext servletContext = new MockServletContext();
    servletContext.addInitParameter(WebUtils.HTML_ESCAPE_CONTEXT_PARAM,
        WebUtils.HTML_ESCAPE_CONTEXT_PARAM);

    // Creates a Web Application Context.
    GenericWebApplicationContext context = new GenericWebApplicationContext();
    context.setServletContext(servletContext);
    context.refresh();

    // Creates a LocaleResolver.
    LocaleResolver localeResolver = new FixedLocaleResolver();

    // Mocks the HttpServletRequest.
    HttpServletRequest servletRequest = createMock(HttpServletRequest.class);
    expect(servletRequest
            .getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE))
        .andReturn(localeResolver);
    expect(servletRequest
            .getAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE))
         .andReturn(context);
    expect(servletRequest.getContextPath()).andReturn("testcontext/");
    replay(servletRequest);
    return servletRequest;
  }
}

