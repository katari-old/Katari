/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Default module implementation with entry points and menu support.
 */
@Deprecated
public class ModuleWithEntryPoints implements Module {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(ModuleWithEntryPoints.class);

  /** The module context.
   *
   * It is never null.
   */
  private ModuleContext moduleContext;

  /** The mapping of urls to servlets of this module.
   *
   * It is never null.
   */
  private Map<String, ServletAndParameters> moduleWebEntryPoints;

  /** The menu barcontainning the module specific menues.
   *
   * It is never null.
   */
  private MenuBar menuBar;

  /** Creates the default module with entry points.
   *
   * @param theModuleWebEntryPoints The mapping of urls to servlets. It cannot
   *        be null.
   * @param theMenuBar the menu bar containning the module specific menues.
   */
  public ModuleWithEntryPoints(final Map<String, ServletAndParameters>
      theModuleWebEntryPoints, final MenuBar theMenuBar) {
    log.trace("Entering ModuleWithEntryPoints");

    Validate.notNull(theModuleWebEntryPoints, "The theModuleWebEntryPoints"
        + " cannot be null");
    Validate.notNull(theMenuBar, "The root menu cannot be null");

    moduleWebEntryPoints = theModuleWebEntryPoints;
    this.menuBar = theMenuBar;

    log.trace("Leaving ModuleWithEntryPoints");
  }

  /** Called by the module container when the module is being initialized.
   *
   * Registers the module entry points to the module container.
   *
   * @param context The module context. It cannot be null.
   */
  public void init(final ModuleContext context) {
    log.trace("Entering init");
    Validate.notNull(context, "The context module cannot be null");

    moduleContext = context;

    log.debug("Registering entry points.");
    moduleContext.registerEntryPoints(moduleWebEntryPoints);
    this.moduleContext.registerMenu(this.menuBar);

    log.trace("Leaving init");
  }

  /** This will be called when the module is about to be destroyed.
   *
   * This method does nothing.
   */
  public void destroy() {
  }
}

