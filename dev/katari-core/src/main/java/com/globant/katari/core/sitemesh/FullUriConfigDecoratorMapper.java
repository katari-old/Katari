/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.ConfigLoader;

/** A sitemesh decorator mapper.
 *
 * Reads decorators and mappings from the <code>config</code> property (default
 * '/WEB-INF/decorators.xml'). This is almost the same as the default decorator
 * mapper, except that it matches against the request including the path info.
 */
public class FullUriConfigDecoratorMapper extends AbstractDecoratorMapper {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      FullUriConfigDecoratorMapper.class);

  /** The configuration loader.
   *
   * This is never null once this class is initialized..
   */
  private ConfigLoader configLoader = null;

  /** Create new ConfigLoader using '/WEB-INF/decorators.xml' file.
   *
   * @param config The sitemesh configuration.
   *
   * @param properties The filter properties.
   *
   * @param parent The parent of this mapper. If this mapper cannot resolve the
   * decorator, it delegates to the parent mapper.
   *
   * @throws InstantiationException in case an error ocurred loading the
   * configuration.
   */
  public void init(final Config config, final Properties properties, final
      DecoratorMapper parent) throws InstantiationException {
    log.trace("Entering init");
    super.init(config, properties, parent);
    try {
      String fileName = properties.getProperty("config",
          "/WEB-INF/decorators.xml");
      if (log.isDebugEnabled()) {
        log.debug("Loading decorators from " + fileName);
      }
      configLoader = new ConfigLoader(fileName, config);
    } catch (Exception e) {
      InstantiationException instantiation = new InstantiationException("Error"
          + " loading configuration: " + e.toString());
      instantiation.initCause(e);
      throw instantiation;
    }
    log.trace("Leaving init");
  }

  /** Sets the configuration loader.
   *
   * This is a package access metthod used for testing purposes only.
   *
   * @param loader The config loader. It cannot be null.
   */
  void setConfigLoader(final ConfigLoader loader) {
    configLoader = loader;
  }

  /** Retrieve {@link com.opensymphony.module.sitemesh.Decorator} based on
   * 'pattern' tag.
   *
   * @param request The user request. It cannot be null.
   *
   * @param page The content of the undecorated page. It cannot be null.
   *
   * @return the decorator to use to decorate the page.
   */
  public Decorator getDecorator(final HttpServletRequest request, final Page
      page) {
    log.trace("Entering getDecorator");
    String thisPath = request.getServletPath();
    String pathInfo = request.getPathInfo();

    // getServletPath() returns null unless the mapping corresponds to a servlet
    if (thisPath == null) {
      thisPath = request.getRequestURI();
    } else if (pathInfo != null) {
      thisPath += pathInfo;
    }

    if (log.isDebugEnabled()) {
      log.debug("Request uri: " + request.getRequestURI());
      log.debug("Servlet context path: " + request.getContextPath());
      log.debug("Searching decorator for url:" + thisPath);
    }

    String name = null;
    try {
      name = configLoader.getMappedName(thisPath);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }

    Decorator result = getNamedDecorator(request, name);
    if (result == null) {
      result = super.getDecorator(request, page);
    }
    if (result == null) {
      log.trace("Leaving getDecorator with null decorator");
    } else {
      log.trace("Leaving getDecorator with non null decorator");
    }
    return result;
  }

  /** Retrieve Decorator named in 'name' attribute.
   *
   * Checks the role if specified.
   *
   * @param request the user http request. It cannot be null.
   *
   * @param name The name of the decorator to obtain. It cannot be null.
   *
   * @return Returns the named decorator, or null if not found.
   */
  public Decorator getNamedDecorator(final HttpServletRequest request, final
      String name) {
    Decorator result = null;
    try {
      result = configLoader.getDecoratorByName(name);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }

    if (result == null || (result.getRole() != null
          && !request.isUserInRole(result.getRole()))) {
      // if the result is null or the user is not in the role
      return super.getNamedDecorator(request, name);
    } else {
      return result;
    }
  }
}

