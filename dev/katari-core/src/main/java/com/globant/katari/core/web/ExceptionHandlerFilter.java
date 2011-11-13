package com.globant.katari.core.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/** This filter catch all the exceptions occurred in the chain down.
 * The strategy its very simple, first should log as an error the error,
 * the redirect the user to a common error page, displaying information
 * about the error (if the applications its running as a debug mode).
 *
 * NOTE: This one, should be on the top of the filter chain.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class ExceptionHandlerFilter implements Filter {

  /** The the request parameter to display the full stack-trace.*/
  protected static final String SHOW_STACK_TRACE_REQUEST_PARAMETER
    = "displayStackTrace";

  /** The class logger.*/
  private static Logger log = getLogger(ExceptionHandlerFilter.class);

  /** The Freemarker's template name. It's never null. */
  private final String viewName;

  /** The Freemarker's configuration. It's never null. */
  private final Configuration freemarkerConfiguration;

  /** Checks if the application is running with debug mode.*/
  private final boolean debugMode;

  /** Builds a new instance of the filter.
   * @param configuration the Freemarker's configuration. Cannot be null.
   * @param theViewName the default view for all errors. Cannot be null.
   * @param isInDebugMode if the application is running in debug mode.
   */
  public ExceptionHandlerFilter(final Configuration configuration,
      final String theViewName, final boolean isInDebugMode) {
    Validate.notNull(configuration,
        "The freemarker configuration cannot be null");
    Validate.notNull(theViewName,
        "The theViewName configuration cannot be null");
    freemarkerConfiguration = configuration;
    viewName = theViewName;
    debugMode = isInDebugMode;
  }

  /** {@inheritDoc}. */
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.trace("initializing the ExceptionHandlerFilter");
    log.trace("end of initialization of the ExceptionHandlerFilter");
  }

  /** Performs the filter chain, if an exception happened will log it as an
   * error, and also will write in the current response an error page.
   * This page contains information about:
   * <ul>
   *  <li>
   *    The Exception
   *  </li>
   *  <li>
   *    Current Request
   *  </li>
   *  <li>
   *    If the application runs on debug or not.
   *  </li>
   *  <li>
   *    The show stack trace parameter.
   *  </li>
   * </ul>
   *
   * {@inheritDoc}.
   */
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain chain) throws IOException,
      ServletException {
    log.trace("Entering doFilter.");
    if (!(request instanceof HttpServletRequest)) {
      throw new RuntimeException("Not an http request");
    }
    if (!(response instanceof HttpServletResponse)) {
      throw new RuntimeException("Not an http request");
    }

    HttpServletResponse httpResponse = (HttpServletResponse) response;

    try {
      chain.doFilter(request, response);
    } catch (Exception e) {
      /* here we can send a camel event. */
      log.error("ERROR!", e);

      boolean showStackTrace = BooleanUtils.toBoolean(
          request.getParameter(SHOW_STACK_TRACE_REQUEST_PARAMETER));

      Map<String, Object> model = new HashMap<String, Object>();
      model.put("exception", e);
      model.put("request", request);
      model.put(SHOW_STACK_TRACE_REQUEST_PARAMETER, showStackTrace);
      model.put("debugMode", debugMode);

      String htmlCode = createHtml(viewName, model);
      httpResponse.getWriter().write(htmlCode);
      httpResponse.flushBuffer();
    }
    log.trace("Finalizing the doFilter.");
  }

  /** Generates a new HTML representation from the given model and template.
  *
  * @param templateName the template name.
  * @param model the model of the template.
  * @return the result of the template's processing.
  */
  private String createHtml(final String templateName,
      final Map<String, Object> model) {
    try {
      StringWriter writer = new StringWriter();
      Template template = freemarkerConfiguration.getTemplate(templateName);
      template.process(model, writer);
      String out = writer.toString();
      IOUtils.closeQuietly(writer);
      return out;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    }
  }

  /** {@inheritDoc}. */
  public void destroy() {
  }

}
