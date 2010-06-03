/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.globant.katari.core.login.LoginConfigurationSetter;

/** This class builds and maintains a ModuleContext registry.
 *
 * Right now it just builds new <code>ModuleContext</code>s.
 */
public class ModuleContextRegistrar {

  /** The registry information, maps a module name to it's module context.
   *
   * It is never null.
   */
  private Map<String, ModuleContext> registry
    = new HashMap<String, ModuleContext>();

  /** A map of bean names to module names.
   *
   * This is never null.
   */
  private Map<String, String> beanToModuleName = new HashMap<String, String>();

  /** The listener proxy that aggregates the listeners provided by all modules.
   *
   * It is never null.
   */
  private ModuleListenerProxy moduleListenerProxy;

  /** The fiter proxy that aggregates the filters provided by all modules.
   *
   * It is never null.
   */
  private ModuleFilterProxy moduleFilterProxy;

  /** The module container servlet that dispatches requests to the modules.
   *
   * It is never null.
   */
  private ModuleContainerServlet moduleContainerServlet;

  /** The menu bar for the whole application.
   *
   * In this node all modules will register his modules
   */
  private MenuBar menuBar = new MenuBar("root", "root");

  /** The login provider. */
  private LoginConfigurationSetter loginConfiguration;

  /** A map of the weblet names tho their entry points, for all modules.
   *
   * It is never null.
   */
  // private Map<String, String> webletMap = new HashMap<String, String>();

  /** Flag that states if ModuleInitializer has called init and registered all
   * modules.
   */
  private boolean isInitialized = false;

  /** Builds a new <code>ModuleContextRegistrar</code>.
   *
   * @param theModuleContainerServlet The module container servlet. It cannot
   * be null.
   *
   * @deprecated
   */
  public ModuleContextRegistrar(final ModuleContainerServlet
      theModuleContainerServlet) {
    Validate.notNull(theModuleContainerServlet, "The module container servlet"
        + " cannot be null");
    moduleContainerServlet = theModuleContainerServlet;
  }

  /** Builds a new <code>ModuleContextRegistrar</code>.
   *
   * @param theModuleContainerServlet The module container servlet. It cannot
   * be null.
   *
   * @param theMenuBar The initial menu bar that will be merged with the
   * modules. This can be used to set the order of the menu containers. It
   * cannot be null.
   *
   * @deprecated
   */
  public ModuleContextRegistrar(final ModuleContainerServlet
      theModuleContainerServlet, final MenuBar theMenuBar) {
    Validate.notNull(theModuleContainerServlet, "The module container servlet"
        + " cannot be null");
    Validate.notNull(theMenuBar, "The menu bar cannot be null");
    moduleContainerServlet = theModuleContainerServlet;
    menuBar = theMenuBar;
  }

  /** Builds a new <code>ModuleContextRegistrar</code>.
   *
   * @param theModuleListenerProxy The listener proxy that aggregates all the
   * listeners provided by the modules. It cannot be null.
   *
   * @param theModuleFilterProxy The filter proxy that aggregates all the
   * filters provided by the modules. It cannot be null.
   *
   * @param theModuleContainerServlet The module container servlet. It cannot
   * be null.
   *
   * @param theMenuBar The initial menu bar that will be merged with the
   * modules. This can be used to set the order of the menu containers. It
   * cannot be null.
   *
   * @param theLoginConfiguration The login configuration. It cannot be null.
   */
  public ModuleContextRegistrar(final ModuleListenerProxy
      theModuleListenerProxy, final ModuleFilterProxy theModuleFilterProxy,
      final ModuleContainerServlet theModuleContainerServlet, final MenuBar
      theMenuBar, final LoginConfigurationSetter theLoginConfiguration) {

    Validate.notNull(theModuleListenerProxy, "The module listener proxy cannot"
        + " be null");
    Validate.notNull(theModuleFilterProxy, "The module filter proxy cannot be"
        + " null");
    Validate.notNull(theModuleContainerServlet, "The module container servlet"
        + " cannot be null");
    Validate.notNull(theMenuBar, "The menu bar cannot be null");
    Validate.notNull(theLoginConfiguration, "The login provider "
        + "cannot be null");
    moduleListenerProxy = theModuleListenerProxy;
    moduleFilterProxy = theModuleFilterProxy;
    moduleContainerServlet = theModuleContainerServlet;
    menuBar = theMenuBar;
    loginConfiguration = theLoginConfiguration;
  }

  /** Builds a new <code>ModuleContext</code>.
   *
   * @param moduleName The name of the module to build the context for. It
   * cannot be null.
   *
   * @return a module context for the provided module name. It never returns
   * null.
   */
  public ModuleContext getNewModuleContext(final String moduleName) {
    Validate.notNull(moduleName, "The module name cannot be null");
    ModuleContext context;
    context = new ModuleContext(moduleName, moduleListenerProxy,
        moduleFilterProxy, moduleContainerServlet, menuBar, beanToModuleName,
        loginConfiguration);
    registry.put(moduleName, context);
    return context;
  }

  /** Registers a module base name.
   *
   * @param beanName The name of the spring bean that defines the module. It
   * cannot be null.
   *
   * @param moduleName The name of the module.
   */
  void addModuleName(final String beanName, final String moduleName) {
    Validate.notNull(beanName, "The bean name cannot be null");
    Validate.notNull(moduleName, "The module name cannot be null");
    beanToModuleName.put(beanName, moduleName);
  }

  /** Forwards a request to a weblet and renders the output to the response.
   *
   * @param moduleName The name of the module. It cannot be null.
   *
   * @param webletName The name of the weblet. It cannot be null.
   *
   * @param theRequest The original request. It cannot be null.
   *
   * @param theResponse The response where the weblet will render the output.
   * It cannot be null.
   *
   * @throws IOException in case of an io error.
   *
   * @throws ServletException in case of an unexpected error.
   */
  /*
  public void getWebletResponse(final String moduleName, final String
      webletName, final HttpServletRequest theRequest, final
      HttpServletResponse theResponse) throws IOException, ServletException {

    Validate.notNull(moduleName, "The module name cannot be null");
    Validate.notNull(webletName, "The weblet name cannot be null");
    Validate.notNull(theRequest, "The request cannot be null");
    Validate.notNull(theResponse, "The response cannot be null");

    // ModuleContext context = registry.get(moduleName);
    // context.getWebletResponse(webletName, theRequest, theResponse);
  }
  */

  /** Return the application menu in the form of his menu bar.
   *
   * @return the application menu in the form of his menu bar.
   */
  public MenuBar getMenuBar() {
    return this.menuBar;
  }

  /**
   * Returns a Set with the registered module names or an empty set if no
   * modules were registered.
   * @return A set with the names of the modules or an empty Set. Never
   *         <code>null</code>.
   */
  public Set<String> getModuleNames() {
    return registry.keySet();
  }

  /**
   * Returns the {@link ModuleContext} for the given module name.
   * @param moduleName
   *            The name of the module, cannot be <code>null</code>.
   * @return Returns the corresponding {@link ModuleContext} or
   *         <code>null</code> if the module does not exist.
   */
  public ModuleContext getModuleContext(final String moduleName) {
    Validate.notNull(moduleName, "The module name cannot be null");
    return registry.get(moduleName);
  }

  /**
   * Returns the bean names (as registered in Spring) of the registered
   * modules.
   * @return A set of modules or an empty set. Never <code>null</code>.
   */
  public Set<String> getModuleBeanNames() {
    return beanToModuleName.keySet();
  }

  /** Called by ModuleInitializer after it initialized the modules.
   *
   * This is a really nasty hack. We two initialization cases: with hibernate
   * and without hibernate. So we have two initializers: ModuleInitializer and
   * HibernateInitializer. The problem is that there is no way to define the
   * order that spring triggers the refresh event, and the HibernateInitializer
   * needs to be run after ModuleInitializer. So we make HibernateInitializer
   * extend ModuleInitializer and make ModuleInitializer check if it has
   * already been called.
   */
  public void setInitialized() {
    isInitialized = true;
  }

  /** Returns if the ModuleInitializer has already registered the modules.
   *
   * See setModulesRegistered for the rationale of this operation.
   *
   * @return true if the modules has been registered.
   */
  public boolean isInitialized() {
    return isInitialized;
  }
}

