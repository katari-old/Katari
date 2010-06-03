/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.parser.HTMLPageParser;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/** Renders a weblet and makes the result available as a string.
 *
 * A weblet is a fragemnt of html that modules provide to include in some part
 * of a page, generally in the decorating border.
 *
 * This class asks the container to include the request, intercepts the output
 * and gets the body of the response. It only supports html output.
 *
 * @author juan.pereyra@globant.com
 */
public class WebletRenderer {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(WebletRenderer.class);

  /** The servlet context.
   *
   * It is never null.
   */
  private final ServletContext servletContext;

  /** Standard constructor.
   *
   * @param theServletContext The servlet context. It cannot be null.
   */
  public WebletRenderer(final ServletContext theServletContext) {
    Validate.notNull(theServletContext, "The servlet context cannot be null.");
    servletContext = theServletContext;
  }

  /** A wrapper for the servlet request that changes the context path presented
   * to the module so that the module 'thinks' it is mapped directly in web xml
   * configuration file.
   */
  private static class ModuleContextRequestWrapper extends
    HttpServletRequestWrapper {

    /** The name of the module.
     *
     * It is never null.
     */
    private String moduleName;

    /** Creates a ModuleContextRequestWrapper.
     *
     * @param request The servlet request. It cannot be null.
     *
     * @param theModuleName The name of the module. It cannot be null.
     */
    public ModuleContextRequestWrapper(final HttpServletRequest request,
        final String theModuleName) {
      super(request);
      Validate.notNull(request, "The request cannot be null");
      Validate.notNull(theModuleName, "The module name cannot be null");
      moduleName = theModuleName;
    }

    /** The context path that must be presented to the weblet.
     *
     * It corresponds to the module path.
     *
     * @return a string with the context path, never null.
     */
    @Override
    public String getContextPath() {
      return super.getContextPath() + "/module/" + moduleName;
    }

    /** The http method.
     *
     * @return the string "GET".
     */
    @Override
    public java.lang.String getMethod() {
      return "GET";
    }
  }

  /** Renders the weblet to a string.
   *
   * @param moduleName The name of the module containing the weblet. It cannot
   * be null.
   *
   * @param webletName The name of the weblet. It cannot be null.
   *
   * @param instance An optional weblet instance name. The weblet uses the
   * instance as it pleases, for example, to draw different thing according to
   * the instance name. It is ignored if it is null or the empty string.
   *
   * @param request The servlet request. It cannot be null.
   *
   * @param response The servlet response. It cannot be null.
   *
   * @return a string with content of the body tag extracted from the generated
   * weblet output.
   *
   * @throws IOException in case of an io error.
   *
   * @throws ServletException in case of an unexpected error.
   */
  public String renderWebletResponse(final String moduleName,
      final String webletName, final String instance,
      final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {

    Validate.notNull(moduleName, "The module name cannot be null");
    Validate.notNull(webletName, "The weblet name cannot be null");
    Validate.notNull(request, "The request cannot be null");
    Validate.notNull(response, "The response cannot be null");

    if (log.isTraceEnabled()) {
      log.trace("Entering renderWebletResponse('" + moduleName + "', '"
          + webletName + "', '" + instance + "', ...)");
    }

    WebletResponseWrapper wrappedResponse;
    wrappedResponse = new WebletResponseWrapper(response);

    String path = "/module/" + moduleName + "/weblet/" + webletName + ".do";

    // We store the instance value in the request as an attribute. We save the
    // old value of the instance, just in case.
    Object oldInstance = null;
    if (instance != null && !"".equals(instance)) {
      oldInstance = request.getAttribute("instance");
      request.setAttribute("instance", instance);
    }

    RequestDispatcher requestDispatcher;
    requestDispatcher = servletContext.getRequestDispatcher(path);

    Object springMacroRequestContext =
      request.getAttribute("springMacroRequestContext");
    request.removeAttribute("springMacroRequestContext");
    requestDispatcher.include(new ModuleContextRequestWrapper(request,
          moduleName), wrappedResponse);
    if (springMacroRequestContext != null) {
      request.setAttribute("springMacroRequestContext",
          springMacroRequestContext);
    }

    String htmlPage = wrappedResponse.getResponseAsString();

    if (oldInstance != null) {
      request.setAttribute("instance", oldInstance);
    }

    String body = extractBody(htmlPage);

    if (log.isTraceEnabled()) {
      log.trace("Leaving renderWebletResponse with '" + body + "'");
    }
    return body;
  }

  /** Extracts the body from an html string.
   *
   * @param content the html content. It cannot be null.
   *
   * @return the content between body tags.
   *
   * @throws IOException in case of an io error.
   */
  private String extractBody(final String content) throws IOException {
    Validate.notNull(content, "The content cannot be null");
    HTMLPageParser parser = new HTMLPageParser();
    Page page = parser.parse(content.toCharArray());
    return page.getBody();
  }
}

