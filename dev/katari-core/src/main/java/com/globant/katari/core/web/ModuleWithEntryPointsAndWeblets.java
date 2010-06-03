/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Default module implementation with entry points.
 */
@Deprecated
public class ModuleWithEntryPointsAndWeblets implements Module {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(
      ModuleWithEntryPointsAndWeblets.class);

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

  /** The list of weblets that the module exposes. It maps the weblet
   * name to its path
   *
   * It is never null.
   */
  private Map<String, String> moduleWeblets;

  /** Creates the user module.
   *
   * @param theModuleWebEntryPoints The mapping of urls to servlets. It cannot
   * be null.
   *
   * @param theModuleWeblets The weblets mapping. It cannot be null.
   */
  public ModuleWithEntryPointsAndWeblets(final Map<String,
      ServletAndParameters> theModuleWebEntryPoints, final Map<String, String>
      theModuleWeblets) {
    log.trace("Entering ModuleWithEntryPointsAndWeblets");

    Validate.notNull(theModuleWebEntryPoints, "The theModuleWebEntryPoints"
        + " cannot be null");
    Validate.notNull(theModuleWeblets, "The theModuleWeblets"
        + " cannot be null");

    moduleWebEntryPoints = theModuleWebEntryPoints;
    moduleWeblets = theModuleWeblets;

    log.trace("Leaving ModuleWithEntryPointsAndWeblets");
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

    log.debug("Registering weblets.");
    moduleContext.registerWeblets(moduleWeblets);

    log.trace("Leaving init");
  }

  /** This will be called when the module is about to be destroyed.
   *
   * This method does nothing.
   */
  public void destroy() {
  }
}

