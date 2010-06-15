/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.EventListener;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.login.LoginConfigurationSetter;
import com.globant.katari.core.login.LoginProvider;
import com.globant.katari.core.security.UrlToRoleMapper;

/** The module context is the way a module interacts with the module container.
 *
 * Through this class, modules can declare to the container the entry points,
 * weblets and menus.
 */
public class ModuleContext {

  /** Class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ModuleContext.class);

  /** Module name.
   *
   * It is never null.
   */
  private final String name;

  /** The application ModuleContainerServlet.
   *
   * It is never null.
   */
  private final ModuleContainerServlet containerServlet;

  /** The application-wise ModuleListenerProxy.
   *
   * Modules register their listeners in this proxy. It is never null.
   */
  private final ModuleListenerProxy listenerProxy;

  /** The application-wise ModuleFilterProxy.
   *
   * Modules register their filters in this proxy. It is never null.
   */
  private final ModuleFilterProxy filterProxy;

  /** The menu bar where the module merges its own menu bar.
   *
   * This is provided by the container. It can be an empty menu bar or it can
   * contain some non-leaf menu nodes. This is used to allow the container to
   * define the display order of the non-leaf nodes of a menu. It is never
   * null.
   */
  private final MenuBar menuBar;

  /** Maps a bean name to the url fragmente where the corresponding module is
   * mapped.
   *
   * It is never null.
   */
  private final Map<String, String> beanToModuleNames;

  /** The list of weblets that the module exposes.
   *
   * It maps the weblet name to its path. It is never null.
   */
  @SuppressWarnings("unused")
  private Map<String, String> moduleWeblets = new HashMap<String, String>();

  /** A url to role mapper that maps all urls to an authenticated user.
   */
  private static final UrlToRoleMapper DEFAULT_MAPPER = new UrlToRoleMapper() {
    public String[] getRolesForUrl(final String menuNode) {
      return new String[]{"IS_AUTHENTICATED_FULLY"};
    }
  };

  /** Maps urls to the roles that has access to that url.
   *
   * It is never null. By default, it is defaultMapper, a mapper that expects
   * the user to be authenticated.
   */
  private UrlToRoleMapper urlToRoleMapper = DEFAULT_MAPPER;

  /** The login configurer. */
  private final LoginConfigurationSetter loginConfigurer;

  /** Builds a ModuleContext.
   *
   * The module context includes a menu bar. This menu bar is provided by the
   * container. It can be an empty menu bar or it can contain some non-leaf
   * menu nodes. This is used to allow the container to define the display
   * order of the non-leaf nodes of a menu.
   *
   * @param theModuleName The name of the module. It cannot be null.
   *
   * @param theListenerProxy The module listener proxy. It cannot be null.
   *
   * @param theFilterProxy The module filter proxy. It cannot be null.
   *
   * @param theContainerServlet The module container servlet. It cannot be
   * null.
   *
   * @param theMenuBar The menu bar where the module merges its own menu bar.
   *
   * @param theBeanToModuleNames Maps a spring bean name to the url fragment
   * that the module is mapped to. It cannot be null.
   *
   * @param theLoginConfigurer the login configuration. It cannot be null.
   */
  ModuleContext(final String theModuleName,
      final ModuleListenerProxy theListenerProxy,
      final ModuleFilterProxy theFilterProxy,
      final ModuleContainerServlet theContainerServlet,
      final MenuBar theMenuBar,
      final Map<String, String> theBeanToModuleNames,
      final LoginConfigurationSetter theLoginConfigurer) {

    log.trace("Entering ModuleContext");

    Validate.notNull(theModuleName, "The module name cannot be null");
    Validate.notNull(theListenerProxy, "The module listener proxy cannot"
        + " be null");
    Validate.notNull(theFilterProxy, "The module filter proxy cannot"
        + " be null");
    Validate.notNull(theContainerServlet, "The module contair servlet cannot"
        + " be null");
    Validate.notNull(theMenuBar, "The menu bar cannot be null");
    Validate.notNull(theBeanToModuleNames, "The bean to module names map"
        + " cannot be null");
    Validate.notNull(theLoginConfigurer, "The login"
        + "configuration cannot be null");

    name = theModuleName;
    listenerProxy = theListenerProxy;
    filterProxy = theFilterProxy;
    containerServlet = theContainerServlet;
    menuBar = theMenuBar;
    beanToModuleNames = theBeanToModuleNames;

    loginConfigurer = theLoginConfigurer;
    log.trace("Leaving ModuleContext");
  }

  /** Returns the name of the module.
   *
   * @return the name of the module. It never returns null.
   */
  public String getModuleName() {
    return name;
  }

  /** Registers the module entry points in the module container servlet.
   *
   * @param entryPoints A map of regex that match a url, to the servlet
   * responsible to handling the request to that url. It cannot be null.
   */
  public void registerEntryPoints(final Map<String, ServletAndParameters>
      entryPoints) {
    Validate.notNull(entryPoints, "The entry points cannot be null");
    containerServlet.addModule(name, entryPoints);
  }

  /** Registers the module menus in the module container servlet.
   *
   * The module menu will be added into the context root menu. The context
   * root menu represents the application root menu so each module needs to
   * register each one of his menus on it and then all module cotext root will
   * be merged in the registrar.
   *
   * @param theMenuBar the menu bar to be registered. It cannot be null.
   */
  public void registerMenu(final MenuBar theMenuBar) {
    Validate.notNull(theMenuBar, "The root menu cannot be null");
    menuBar.merge(theMenuBar, beanToModuleNames, "module/" + name);
  }

  /** Registers the weblets exposed by the module.
   *
   * @param theWeblets A map of the weblets names and its paths. It cannot be
   * null.
   *
   * TODO This is currently unused, because we base the weblet location on a
   * url naming convention (/weblet/weblet-name. This is error prone, so we
   * should ask the context for the weblets. The naming convention is anyway
   * convenient, because we do not want to decorate weblets.
   */
  public void registerWeblets(final Map<String, String> theWeblets) {
    Validate.notNull(theWeblets, "The weblets cannot be null");
    moduleWeblets = theWeblets;
  }

  /** Registers the listeners exposed by the module.
   *
   * @param listeners The list of listeners provided by the module. It cannot
   * be null.
   */
  public void registerListeners(final List<EventListener> listeners) {
    Validate.notNull(listeners, "The listeners cannot be null");
    listenerProxy.addListeners(listeners);
  }

  /** Registers the filters exposed by the module.
   *
   * The filters are called on each matching url in priority order. The
   * priority is specified in the FilterMapping of each filter.
   *
   * @param filters The list of filters provided by the module. It cannot be
   * null.
   */
  public void registerFilters(final List<FilterMapping> filters) {
    Validate.notNull(filters, "The filters cannot be null");
    filterProxy.addFilters(filters);
  }

  /** It registers the <code>UrlToRoleMapper</code>.
   *
   * @param theUrlToRoleMapper the map of urls to the roles that has access to
   * that url. It cannot be null.
   */
  public void registerUrlToRoleMapper(
    final UrlToRoleMapper theUrlToRoleMapper) {
    Validate.notNull(theUrlToRoleMapper, "The UrlToRoleMapper cannot"
            + " be null");
    this.urlToRoleMapper = theUrlToRoleMapper;
  }

  /** It gets the urlToMapper.
   *
   * @return the urlToRoleMapper. It never returns null.
   */
  public UrlToRoleMapper getUrlToRoleMapper() {
    return urlToRoleMapper;
  }

  /** Returns the map that matches the names of the weblets this context exposes
   * and its urls.
   *
   * @return webletMap
   */
  /*
  public Map<String, String> getWebletMap() {
    return moduleWeblets;
  }
  */

  /** returns the menu bar for this module as it was initialized by the module
   * itself.
   *
   * @return the menu bar for this module.
   */
  /*
   * public MenuBar getMenuBar() { return menuBar; }
   */

  /** Perform any needed validations on the class and adds it to the
   * corresponding Set. Neither parameter can be <code>null</code>.
   *
   * @param targetSet
   *          The set to add the class to.
   * @param clazz
   *          The class to add
   */
  protected void doAdd(final Set<Class<?>> targetSet,
      final Class<?> clazz) {
    Validate.notNull(clazz, "The class cannot be null");
    targetSet.add(clazz);
  }

  /** Sets the login provider for the application.
   *
   * There can be only one login provider in the application or you will get
   * some unexpected results. This is not explicitly checked.
   *
   * @param provider the login provider. It cannot be null.
   */
  public void setLoginProvider(final LoginProvider provider) {
    Validate.notNull(provider, "The provider cannot be null");
    loginConfigurer.setLoginConfiguration(provider);
  }
}

