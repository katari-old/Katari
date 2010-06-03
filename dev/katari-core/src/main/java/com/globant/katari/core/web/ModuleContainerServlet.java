/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** This servlet is the entry point to all web requests for all modules.
 *
 * It dispatches all requests to another servlet based on the module name. This
 * servlet gets all requests from the SpringBootstrapServlet, so that spring
 * has a chance to inject all dependencies.
 *
 * The SpringBootstrapServlet is generally mapped to the 'module' path. The
 * first path component after the servlet map is the module name. This servlet
 * delegates the request to another servlet hidding all this module thing.
 */
public final class ModuleContainerServlet extends HttpServlet {

  /** The serialization version number.
   *
   * This number must change every time a new serialization incompatible change
   * is introduced in the class.
   */
  private static final long serialVersionUID = 20071005;

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(ModuleContainerServlet.class);

  /** A map of module names to module configuration.
   *
   * A module configuration is simply another map that maps a url fragment to a
   * servlet plus its configuration. It is never null.
   */
  private Map<String, Map<String, ServletAndParameters>> modulesMap
    = new HashMap<String, Map<String, ServletAndParameters>>();

  /** Adds a module to the modulesMap.
   *
   * @param moduleName The module name. It cannot be null.
   *
   * @param servletAndParams The map of urls to servlets of the module. It
   * cannot be null.
   */
  public void addModule(final String moduleName,
      final Map<String, ServletAndParameters> servletAndParams) {
    Validate.notNull(moduleName, "The module name cannot be null");
    Validate.notNull(servletAndParams, "The servlet and parameters cannot be"
        + " null");
    modulesMap.put(moduleName, servletAndParams);
  }

  /** Called by the servlet container to indicate to a servlet that it is being
   * placed into service.
   *
   * @param servletConfig The servlet's configuration and initialization
   * parameters. This object is created by the container.
   *
   * @throws ServletException if an unexpected exception occurs.
   */
  public void init(final ServletConfig servletConfig) throws ServletException {
    log.trace("Entering init");
    super.init(servletConfig);
    for (Map.Entry<String, Map<String, ServletAndParameters>> entry
        : modulesMap.entrySet()) {
      String module = entry.getKey();
      Map<String, ServletAndParameters> moduleMapping =  entry.getValue();
      initModule(servletConfig, module, moduleMapping);
    }
    log.trace("Leaving init");
  }

  /** Calls destroy on every configured servlet.
   */
  public void destroy() {
    log.trace("Entering destroy");
    for (Map<String, ServletAndParameters> mapping : modulesMap.values()) {
      for (ServletAndParameters servlet : mapping.values()) {
        servlet.getServlet().destroy();
      }
    }
    super.destroy();
    log.trace("Leaving destroy");
  }

  /** Initializes all the servlets in a module.
   *
   * @param config The servlet configuration. It cannot be null.
   *
   * @param module The module name. It cannot be null.
   *
   * @param moduleMapping The url to servlet mapping corresponding to the
   * module.
   *
   * @throws ServletException if an error occurs.
   */
  private void initModule(final ServletConfig config, final String module,
      final Map<String, ServletAndParameters> moduleMapping)
      throws ServletException {

    log.trace("Entering init");
    Validate.notNull(config, "The servlet config cannot be null");
    Validate.notNull(module, "The module name cannot be null");
    Validate.notNull(moduleMapping, "The module mapping cannot be null");

    List<HttpServlet> initializedServlets = new ArrayList<HttpServlet>();
    for (Map.Entry<String, ServletAndParameters> entry:
        moduleMapping.entrySet()) {
      ServletAndParameters servletAndParameters;
      servletAndParameters = (ServletAndParameters) entry.getValue();
      HttpServlet servlet =  servletAndParameters.getServlet();
      if (!containsServlet(initializedServlets, servlet)) {
        ModuleServletConfig moduleConfig = new ModuleServletConfig(this,
            config.getServletContext(), module,
            servletAndParameters.getParameters());
        servlet.init(moduleConfig);
        initializedServlets.add(servlet);
      }
    }
    log.trace("Leaving init");
  }

  /**
   * Looks for a servlet inside the list of servlets passed as parameter.
   *
   * It compares the servlets using the '==' operator instead of equals method
   * in order to ensure that both are the same object.
   * @param servlets the list of servlets we are looking into.
   * @param servlet the servlet we are looking for.
   * @return true if the servlets list contains the specified servlet, false
   * otherwise.
   */
  private boolean containsServlet(final List<HttpServlet> servlets,
      final HttpServlet servlet) {
    for (HttpServlet httpServlet : servlets) {
      if (httpServlet == servlet) {
        return true;
      }
    }
    return false;
  }

  /** Called by the servlet container to allow the servlet to respond to a
   * request.
   *
   * @param request The HttpServletRequest object that contains the client's
   * request.
   *
   * @param response The HttpServletResponse object that contains the
   * servlet's response
   *
   * @throws IOException if an input or output exception occurs.
   *
   * @throws ServletException if some other error occurs.
   */
  protected void service(final HttpServletRequest request, final
      HttpServletResponse response) throws ServletException, IOException {

    log.trace("Entering service");

    /* This is the info for a web.xml mapped struts servlet.

       requestURL = http://localhost:8080/katari-i/welcome.do
       requestURI = /katari-i/welcome.do
       contextPath = /katari-i
       servletPath = /welcome.do
       pathInfo = null
       */

    if (log.isDebugEnabled()) {
      log.debug("requestURL = " + request.getRequestURL());
      log.debug("requestURI = " + request.getRequestURI());
      log.debug("contextPath = " + request.getContextPath());
      log.debug("servletPath = " + request.getServletPath());
      log.debug("pathInfo = " + request.getPathInfo());
      log.debug("servletContextName = "
          + getServletContext().getServletContextName());
    }

    String pathInfo;
    if (isIncluded(request)) {
      pathInfo = (String) request.getAttribute(
          "javax.servlet.include.path_info");
    } else {
      pathInfo = request.getPathInfo();
    }

    ServletData servletData = getServletFromUri(pathInfo);
    ModuleRequestWrapper requestWrapper = new ModuleRequestWrapper(request,
          servletData.getModuleName(), servletData.getServletPath());

    Object savedRequest = request.getAttribute("request");
    Object savedResponse = request.getAttribute("response");
    servletData.getServlet().service(requestWrapper, response);
    if (savedRequest != null) {
      request.setAttribute("request", savedRequest);
    }
    if (savedResponse != null) {
      request.setAttribute("response", savedResponse);
    }

    log.trace("Leaving service");
  }

  /** Obtains the servlet configuration from a url path fragment.
   *
   * @param path The url fragment that follows this servlet path.
   *
   * @return Returns all the data needed to forward the request to the
   * correct servlet. It never returns null.
   */
  public ServletData getServletFromUri(final String path) {
    Validate.notNull(path, "The path cannot be null");

    // Iterates over all the modules.
    for (Map.Entry<String, Map<String, ServletAndParameters>> entry:
        modulesMap.entrySet()) {
      String module = entry.getKey();
      if (path.startsWith("/" + module + "/") || path.equals("/"
            + module)) {
        // Found the module, now iterate over the servlets.
        if (log.isDebugEnabled()) {
          log.debug("Dispatching request to module: " + module);
        }
        Map<String, ServletAndParameters> moduleMapping = entry.getValue();
        String modulePath = path.substring(module.length() + 1);

        return getServletFromModule(moduleMapping, module, modulePath);
      }
    }
    throw new RuntimeException("No module configuration found for path "
        + path);
  }

  /** Obtains the servlet configuration from a url path fragment.
   *
   * @param moduleMapping The uri to servlet mapping.
   *
   * @param moduleName The name of the module.
   *
   * @param path The url fragment that follows the name of the module.
   *
   * @return Returs all the data needed to forward the request to the correct
   * servlet. It never returns null.
   */
  private ServletData getServletFromModule(
      final Map<String, ServletAndParameters> moduleMapping,
      final String moduleName, final String path) {

    /*
       requestURL = http://localhost:8080/katari-i/welcome.do
       requestURI = /katari-i/welcome.do
       contextPath = /katari-i
       servletPath = /welcome.do
       pathInfo = null
     */

    Validate.notNull(path, "The path cannot be null");

    for (Map.Entry<String, ServletAndParameters> entry:
        moduleMapping.entrySet()) {
      String uri = entry.getKey();
      if (log.isDebugEnabled()) {
        log.debug("Verifying if path " + path + " matches " + uri);
      }
      Pattern pattern = Pattern.compile(uri);
      Matcher matcher = pattern.matcher(path);
      if (matcher.lookingAt()) {
        if (log.isDebugEnabled()) {
          log.debug("Matched " + uri);
        }
        ServletAndParameters servletAndParameters = entry.getValue();
        HttpServlet servlet =  servletAndParameters.getServlet();
        String servletPath = matcher.group();
        return new ServletData(servlet, moduleName, servletPath);
      }
    }
    throw new RuntimeException("No servlet found for path " + path);
  }

  /** Checks if the request correponds to a servlet include.
   *
   * @param request The request to check for include. It cannot be null.
   *
   * @return true if it is an include, false otherwise.
   */
  private boolean isIncluded(final HttpServletRequest request) {
    Validate.notNull(request, "The request cannot be null");
    return request.getAttribute("javax.servlet.include.request_uri") != null;
  }

  /** This class contains the data necessary to forward a request to a
   * servlet.
   */
  public static final class ServletData {

    /** The servlet.
     *
     * It is never null.
     */
    private HttpServlet servlet;

    /** The module name.
     *
     * It is never null.
     */
    private String moduleName;

    /** The path where this servlet is mapped.
     *
     * It is never null.
     */
    private String servletPath;

    /** Creates a servlet data.
     *
     * @param theServlet The servlet that handles the requests. It cannot
     * be null.
     *
     * @param theModuleName The name of the module containing this servlet.
     * It cannot be null.
     *
     * @param theServletPath The url path where this servlet is mapped. It
     * cannot be null.
     */
    public ServletData(final HttpServlet theServlet, final String
        theModuleName, final String theServletPath) {

      Validate.notNull(theServlet, "The servlet cannot be null");
      Validate.notNull(theModuleName, "The module name cannot be null");
      Validate.notNull(theServletPath, "The servlet path cannot be null");

      servlet = theServlet;
      moduleName = theModuleName;
      servletPath = theServletPath;
    }

    /** Returns the servlet.
     *
     * @return the servlet. It never returns null.
     */
    public HttpServlet getServlet() {
      return servlet;
    }

    /** Returns the module name.
     *
     * @return the module name. It never returns null.
     */
    public String getModuleName() {
      return moduleName;
    }

    /** Returns the servlet path.
     *
     * @return the servlet path. It never returns null.
     */
    public String getServletPath() {
      return servletPath;
    }
  }
}

