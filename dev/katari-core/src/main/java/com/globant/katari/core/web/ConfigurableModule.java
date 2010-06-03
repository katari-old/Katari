/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.EventListener;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.core.login.LoginProvider;
import com.globant.katari.core.security.StaticUrlToRoleMapper;
import com.globant.katari.core.security.UrlToRoleMapper;

/** A module implementation intended to be used in a spring xml configuration
 * file, with setters for all the possible properties.
 *
 * This is a convenient module implementation. You can set the properties you
 * need (leave the other ones alone) and the module will register only what you
 * specified.
 */
public class ConfigurableModule implements Module {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(ConfigurableModule.class);

  /** The default roles array to instanciate the UrlToRoleMapper by default.
   */
  private static final String[] DEFAULT_ROLES = {"IS_AUTHENTICATED_REMEMBERED"};

  /** The list of web application listeners that the module provides.
   *
   * It is null for modules that do not provide any listener.
   */
  private List<EventListener> listeners;

  /** The list of filters that the module provides.
   *
   * It is null for modules that do not provide any filter.
   */
  private List<FilterMapping> filters;

  /** The module entry points, a mapping of urls to servlets of this module.
   *
   * It is null for a module with no entry points.
   */
  private Map<String, ServletAndParameters> entryPoints;

  /** The list of weblets that the module exposes.
   *
   * It maps the weblet name to its path. It is null for a module without
   * weblets.
   */
  private Map<String, String> weblets;

  /** The menu barcontainning the module specific menues.
   *
   * It is null for a module with no menu bar.
   */
  private MenuBar menuBar;

  /**
   * The default path pattern to instanciate the UrlToRoleMapper by default.
   */
  private static final String DEFAULT_URL_PATTERN = "/**";

  /**
   * The url to role mapper.
   *
   * It cannot be null;
   */
  private UrlToRoleMapper urlToRoleMapper;

  /** The login provider. */
  private LoginProvider provider;

  /**
   * Default Constructor.
   * Performs the security elements initialization.
   *
   */
  public ConfigurableModule() {
    Map<String, String[]> roleMap = new LinkedHashMap<String, String[]>();
    roleMap.put(DEFAULT_URL_PATTERN, DEFAULT_ROLES);
    urlToRoleMapper = new StaticUrlToRoleMapper(roleMap);
  }

  /** Called by the module container when the module is being initialized.
   *
   * @param context The module context. It cannot be null.
   */
  public void init(final ModuleContext context) {
    log.trace("Entering init");
    Validate.notNull(context, "The context module cannot be null");

    if (listeners != null) {
      log.debug("Registering listeners.");
      context.registerListeners(listeners);
    }

    if (filters != null) {
      log.debug("Registering filters.");
      context.registerFilters(filters);
    }

    if (entryPoints != null) {
      log.debug("Registering entry points.");
      context.registerEntryPoints(entryPoints);
    }

    if (weblets != null) {
      log.debug("Registering weblets.");
      context.registerWeblets(weblets);
    }

    if (menuBar != null) {
      log.debug("Registering menu bar.");
      context.registerMenu(menuBar);
    }

    log.debug("Registering urlToRoleMapper");
    context.registerUrlToRoleMapper(urlToRoleMapper);

    if (provider != null) {
      log.debug("Registering login provider");
      context.setLoginProvider(provider);
    }
    log.trace("Leaving init");
  }

  /** This will be called when the module is about to be destroyed.
   *
   * This method does nothing.
   */
  public void destroy() {
  }

  /** Sets the listeners for this module.
   *
   * This is usually called by spring when creating the module.
   *
   * @param theListeners The listeners. It cannot be null.
   */
  public void setListeners(final List<EventListener> theListeners) {
    Validate.notNull(theListeners, "The listeners cannot be null.");
    listeners = theListeners;
  }

  /** Sets the filters for this module.
   *
   * This is usually called by spring when creating the module.
   *
   * @param theFilters The exposed filters. It cannot be null.
   */
  public void setFilters(final List<FilterMapping> theFilters) {
    Validate.notNull(theFilters, "The filters cannot be null.");
    filters = theFilters;
  }

  /** Sets the entry points for this module.
   *
   * This is usually called by spring when creating the module.
   *
   * @param theEntryPoints The exposed entry points. It cannot be null.
   */
  public void setEntryPoints(final Map<String, ServletAndParameters>
      theEntryPoints) {
    Validate.notNull(theEntryPoints, "The entry points cannot be null.");
    entryPoints = theEntryPoints;
  }

  /** Sets the weblets for this module.
   *
   * This is usually called by spring when creating the module.
   *
   * @param theWeblets The exposed weblets. It cannot be null.
   */
  public void setWeblets(final Map<String, String> theWeblets) {
    Validate.notNull(theWeblets, "The weblets cannot be null.");
    weblets = theWeblets;
  }

  /** Sets the menu bar for this module.
   *
   * This is usually called by spring when creating the module.
   *
   * @param theMenuBar The menu bar provided by this module. It cannot be null.
   */
  public void setMenuBar(final MenuBar theMenuBar) {
    Validate.notNull(theMenuBar, "The menu bar cannot be null.");
    menuBar = theMenuBar;
  }

  /** Sets the UrlToRoleMapper.
   *
   *  @param theUrlToRoleMapper the urlToRoleMapper to set. It cannot be null.
   */
  public void setUrlToRoleMapper(final UrlToRoleMapper theUrlToRoleMapper) {
    Validate.notNull(theUrlToRoleMapper, "The UrlToRoleMapper cannot"
        + " be null");
    this.urlToRoleMapper = theUrlToRoleMapper;
  }

  /** Sets the login provider for this module.
   * It will be the login provider for all the application.
   *
   * @param theProvider The provider to set, cannot be null.P
   */
  public void setLoginProvider(final LoginProvider theProvider) {
    Validate.notNull(theProvider, "The login provider cannot be null");
    this.provider = theProvider;
  }
}

