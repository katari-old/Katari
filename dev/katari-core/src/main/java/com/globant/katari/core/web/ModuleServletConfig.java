/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Enumeration;
import java.util.Map;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The servlet config initialized with the module parameters.
 */
class ModuleServletConfig implements ServletConfig {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ModuleServletConfig.class);

  /** The servlet context.
   *
   * It is never null.
   */
  private ServletContext servletContext;

  /** The servlet initialization parameters.
   *
   * This is a Hashtable because it user Enumerations instead of Iterators.
   * This makes it easier to use with the servlet interfaces.
   */
  private Hashtable<String, String> initParameters
    = new Hashtable<String, String>();

  /** The name of the module.
   */
  private String moduleName;

  /** The module container servlet.
   */
  private ModuleContainerServlet container;

  /** Builds a module servlet configuration.
   *
   * @param theContainer The servlet that forwards requests to modules. It
   * cannot be null.
   *
   * @param theServletContext The servlet context. It cannot be null.
   *
   * @param theModuleName The name of the module. It cannot be null.
   *
   * @param theParameters The initialization parameters of the contained
   * servlet. If null, no parameters are passed to the servlet.
   */
  ModuleServletConfig(final ModuleContainerServlet theContainer,
      final ServletContext theServletContext, final String theModuleName,
      final Map<String, String> theParameters) {

    Validate.notNull(theContainer, "The container cannot be null");
    Validate.notNull(theServletContext, "The servlet context cannot be null");
    Validate.notNull(theModuleName, "The module name cannot be null");

    container = theContainer;
    servletContext = theServletContext;
    initParameters = new Hashtable<String, String>(theParameters);
    moduleName = theModuleName;
  }

  /** Obtain an initialization parameter by name.
   *
   * @param name The name of the initialization parameter. If null, this method
   * return null.
   *
   * @return Returns the value of the named initialization parameter. If the
   * paramater was not found or the name was null, it returns null.
   */
  public String getInitParameter(final String name) {
    if (log.isTraceEnabled()) {
      log.trace("Entering getInitParameter('" + name + "')");
    }
    String value = (String) initParameters.get(name);
    log.trace("Leaving getInitParameter");
    return value;
  }

  /** Returns all the initialization parameter names.
   *
   * @return an Enumeration that can be used to iterate over all the
   * initialization parameter names.
   */
  @SuppressWarnings("unchecked")
  public Enumeration getInitParameterNames() {
    log.trace("Entering getInitParameters");
    log.trace("Leaving getInitParameters");
    return initParameters.keys();
  }

  /** Returns the servlet context.
   *
   * @return the servlet context.
   */
  public ServletContext getServletContext() {
    log.trace("Entering getServletContext");
    ModuleServletContext context = new ModuleServletContext(container,
        servletContext, moduleName);
    log.trace("Leaving getServletContext");
    return context;
  }

  /** Returns the name of the servlet.
   *
   * This implementation returns as the servlet name the module name.
   *
   * @return Returns the servlet name.
   *
   * @todo Check if is this is necessary.
   */
  public String getServletName() {
    return moduleName;
  }
}

