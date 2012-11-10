/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

/** Filter that passes all generated html through jtidy, an html validator.
 */
public class HtmlValidationFilter implements Filter {

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(HtmlValidationFilter.class);

  /** Defines if the filter performs validation or not.
   *
   * The filter is enabled by default.
   */
  private boolean enabled = true;

  /** The list of url pattern to ignore, some frameworks or custom pages can
   * conflict with validation, by default it is an empty list.
   */
  private List<String> ignoredUrlpatterns = Collections.emptyList();

  /** The list of evaluators, by default it is an empty list. */
  private List<HtmlTidyEvaluator> evaluators =
      new LinkedList<HtmlTidyEvaluator>();

  /** A response wrapper that provides access to the data submitted to the
   * client.
   */
  private static class ResponseWrapper extends ServletOutputInterceptor {

    /** The output stream that holds the data that has been sent to the client.
     *
     * It is never null.
     */
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    /** Constructor.
     *
     * @param response the wrapped response.
     */
    public ResponseWrapper(final HttpServletResponse response) {
      super(response, false);
    }

    /** Returns the generated response as a byte array.
     *
     * @return the byte array of the generated response, never null.
     */
    public byte[] toByteArray() {
      return output.toByteArray();
    }

    /** Prepares the content to be shown in an html page, escaping html tags,
     * adding line numbers and line breaks.
     *
     * @return the formatted content, never null.
     */
    public String getFormattedContent() {
      StringBuilder result = new StringBuilder(output.size());
      try {

        InputStream capturedOutput;
        capturedOutput = new ByteArrayInputStream(output.toByteArray());
        LineIterator lines;
        lines = IOUtils.lineIterator(capturedOutput, getCharacterEncoding());

        try {
          int lineNumber = 0;
          while (lines.hasNext()) {
            String line = lines.nextLine();
            lineNumber ++;
            /// do something with line
            result.append(String.valueOf(lineNumber));
            result.append(": ");
            result.append(StringEscapeUtils.escapeHtml(line));
            result.append("<br>");
          }
        } finally {
          LineIterator.closeQuietly(lines);
        }
      } catch (IOException e) {
        throw new RuntimeException("Error reading output", e);
      }
      return result.toString();
    }

    /** {@inheritDoc}
    */
    protected OutputStream createOutputStream() {
      return output;
    }
  };

  /** Listens to the validation errors.*/
  private static final class ErrorListener implements TidyMessageListener {

    /** The list of errors received from the validator.
     *
     * It is never null.
     */
    private List<TidyMessage> errors = new LinkedList<TidyMessage>();

    private List<HtmlTidyEvaluator> evaluators =
        new LinkedList<HtmlTidyEvaluator>();

    /** Builds an ErrorListener.
     *
     * @param mustIgnoreValidatorAttribute true if the listener will ignore the
     * unknown attribute "validator" message, useful to validate tapestry
     * pages.
     */
    private ErrorListener(final List<HtmlTidyEvaluator> theEvaluators) {
      evaluators.addAll(theEvaluators);
      evaluators.add(new HtmlTidyEvaluator("0:#:(?s).*\"validation\"(?s).*"));
    }

    /** Called by tidy when a warning or error occurs.
     *
     * It explicitly skips the error related to the attribute 'validator'.
     * This makes it possible to validate tapestry pages that adds the
     * 'validator' attribute to some elements.
     *
     * @param message The error/warning message. It cannot be null.
     */
    public void messageReceived(final TidyMessage message) {
      Validate.notNull(message, "The message cannot be null.");
      boolean skip = false;
      for (HtmlTidyEvaluator evaluator : evaluators) {
        if (skip == true) {
          break;
        }
        skip = evaluator.skip(message);
      }
      if (!skip) {
        errors.add(message);
      }
    }

    /** Indicates if the filter found validation errors.
     *
     * @return true if there were validation errors.
     */
    public boolean hasErrors() {
      return !errors.isEmpty();
    }

    /** Formats all the errors received into a string.
     *
     * This operation creates a string with lines separated by \n, each line of
     * the form:
     *
     * line 247 column 10 - Warning: unknown attribute "validator"
     *
     * It must only be called if there where errors, ie, hasErrors returns
     * true.
     *
     * @return The errors as a string, never returns null.
     */
    public String getErrorMessage() {
      Validate.notEmpty(errors);
      StringBuilder output = new StringBuilder();
      for (TidyMessage message : errors) {
        output.append("line ").append(message.getLine());
        output.append(" column ").append(message.getColumn());
        output.append(" - ").append(message.getLevel());
        output.append(": ");
        output.append(StringEscapeUtils.escapeHtml(message.getMessage()));
        output.append("<br>\r");
      }
      return output.toString();
    }
  };

  /** {@inheritDoc}
   *
   * It currently does nothing.
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.trace("Entering init");
    // Do nothing.
    log.trace("Leaving init");
  }

  /** {@inheritDoc}
   *
   * Validates that the output is valid html and throws a ServletException if
   * not.
   *
   * It only processes text/html files.
   */
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain chain) throws IOException,
      ServletException {

    log.trace("Entering doFilter.");

    if (!(response instanceof HttpServletResponse)) {
      throw new ServletException(
          "This filter can only be applied to http requests.");
    }

    if (!(request instanceof HttpServletRequest)) {
      throw new ServletException(
          "This filter can only be applied to http requests.");
    }

    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String requestUri = ((HttpServletRequest) request).getRequestURI();
    boolean ignored = false;
    Iterator<String> iterator = ignoredUrlpatterns.iterator();
    while (iterator.hasNext() && !ignored) {
      String regexp = iterator.next();
      log.debug("Checking if {} matches {}.", requestUri, regexp);
      ignored = requestUri.matches(regexp);
    }
    if (enabled && !ignored) {

      ResponseWrapper wrapper = new ResponseWrapper(httpResponse);

      chain.doFilter(request, wrapper);

      wrapper.flushBuffer();

      String contentType = httpResponse.getContentType();
      if (contentType != null && contentType.startsWith("text/html")) {
        Tidy tidy = new Tidy();
        tidy.setQuiet(true);

        // Set the error output and ignore it.
        tidy.setErrout(new PrintWriter(new ByteArrayOutputStream()));

        ErrorListener errors = new ErrorListener(evaluators);
        tidy.setMessageListener(errors);

        InputStream inputStream;
        inputStream = new ByteArrayInputStream(wrapper.toByteArray());

        // We ignore the output.
        tidy.parse(inputStream, new ByteArrayOutputStream());

        if (errors.hasErrors()) {
          // jtidy found an error. Logger it with the page to make it easier to
          // trace.
          //
          // TODO This is using a non localized string conversion.
          String message = "There where validation errors for "
            + requestUri + ":<br>\r"
            + errors.getErrorMessage() + "<br>\r"
            + "The html output was:<br>\r<pre>"
            + new String(wrapper.getFormattedContent())
            + "</pre>";

          log.debug(message);
          httpResponse.setStatus(500);
          PrintWriter out = httpResponse.getWriter();
          out.print("<html><head><title>Validation error</title></head>");
          out.print("<body style='font-family: monospace;'/>");
          out.print(message);
          out.print("</body></html>");
        } else {
          // No error, send the response to the client.
          response.getOutputStream().write(wrapper.toByteArray());
        }
      } else {
        // Unknown content type, send the response to the client.
        response.getOutputStream().write(wrapper.toByteArray());
      }
    } else {
      chain.doFilter(request, response);
    }

    log.trace("Leaving doFilter.");
  }

  /** Called by the container when the filter is about to be destroyed.
   *
   * This implementation is empty.
   */
  public void destroy() {
    log.trace("Entering destroy");
    // Do nothing.
    log.trace("Leaving destroy");
  }

  /** Enables or disables the validation.
   *
   * @param isEnabled if true, it enables the filter, if false, it disables it.
   */
  public void setEnabled(final boolean isEnabled) {
    enabled = isEnabled;
  }

  /** Returns true if validation is enabled.
   *
   * @return if html validation is enabled.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /** Configures the list of patters for the urls that should be ignored on the
   * validation process.
   * @param theIgnoredUrlpatterns the list of url patterns, it cannot be null.
   */
  public void setIgnoredUrlpatterns(final List<String> theIgnoredUrlpatterns) {
    Validate.notNull(theIgnoredUrlpatterns, "The pattern list cannot be null.");
    ignoredUrlpatterns = theIgnoredUrlpatterns;
  }

  /** Adds expressions into the validation's skip list.
   * @param expressions the list of expressions, it cannot be null.
   */
  public void setSkipExpressions(final List<String> expressions) {
    Validate.notNull(expressions, "The list of expressions cannot be null");
    for (String expression : expressions) {
      evaluators.add(new HtmlTidyEvaluator(expression));
    }
  }

  /** This evaluator checks within the validation flow, if the given tidy
   * message should propate an error or not.
   *
   * The expression given should be something like this:
   *
   * {int: error code}:#:{String: expression]
   *
   * for example: 32:#:(?s).*\"letter not allowed here\"(?s).*
   */
  private static class HtmlTidyEvaluator {

    /** The user wants to ignore the error code, will provide this value
     * within the given expression.*/
    private static final int IGNORE_ERROR_CODE = 0;

    /** The user wants to ignore the error message, will provide this value
     * within the given expression.*/
    private static final String IGNORE_ERROR_MESSAGE = "?";

    /** The user error code.*/
    private final int userErrorCode;

    /** The error message.*/
    private final String userMessage;

    /** Creastes a new instance of the tidy html evaluator.
     * @param expression the expression, cannot be null.
     */
    public HtmlTidyEvaluator(final String expression) {
      Validate.notNull(expression, "The expression cannot be null");
      String[] values = expression.split(":#:");
      userErrorCode = Integer.parseInt(values[0]);
      userMessage = values[1];
    }

    /** Evaluates the given tidy message in order to skip or not the error.
     * @param message the tidy message, cannot be null.
     * @return true if should skipt the message.
     */
    boolean skip(final TidyMessage message) {
      String tidyMessage = message.getMessage();
      int tidyErrorCode = message.getErrorCode();
      if (userErrorCode == IGNORE_ERROR_CODE) {
        return tidyMessage.equals(userMessage)
            || tidyMessage.matches(userMessage);
      } else if (userMessage.equals(IGNORE_ERROR_MESSAGE)) {
        return userErrorCode == tidyErrorCode;
      } else {
        return (userErrorCode == tidyErrorCode
            && (tidyMessage.equals(userMessage) ||
                tidyMessage.matches(userMessage)));
      }
    }
  }

}

