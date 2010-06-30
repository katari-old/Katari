/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig;

import java.util.List;
import java.util.LinkedList;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

/** Initializes the guice injector and makes it available in the servlet
 * context.
 *
 * This listener is intended to be used in a spring bean factory, not directly
 * in web.xml.
 *
 * It stores the injector under the name "guice-injector", the same place
 * expected by shindig.
 */
public class GuiceInitializerListener implements ServletContextListener {

  /** The name that the injector is stored in the servlet context.
   */
  public static final String INJECTOR_ATTRIBUTE_NAME = "guice-injector";

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(GuiceInitializerListener.class);

  /** The list of guice module names to initialize the injectors with.
   *
   * Each module name corresponds to the name of a class that implements
   * Module. This is never null.
   */
  private List<String> moduleNames;

  /** Creates a listener that initializes guice with the provided modules.
   *
   * @param guiceModuleNames the list of guice module names. The name
   * corresponds to a class that implements Module (from guice). It cannot be
   * null.
   */
  public GuiceInitializerListener(final List<String> guiceModuleNames) {
    Validate.notNull(guiceModuleNames, "The list of modules cannot be null.");
    moduleNames = guiceModuleNames;
  }

  /** Initializes the guice injector with the provided modules, and stores it
   * in the servlet context.
   *
   * @param event The triggered event. It cannot be null.
   */
  public void contextInitialized(final ServletContextEvent event) {
    log.trace("Entering init");

    ServletContext servletContext = event.getServletContext();
    if (servletContext.getAttribute(INJECTOR_ATTRIBUTE_NAME) != null) {
      throw new RuntimeException("There already is an attribute named "
          + INJECTOR_ATTRIBUTE_NAME + " in the servlet context.");
    }

    List<Module> modules = new LinkedList<Module>();
    for (String moduleName : moduleNames) {
      try {
        moduleName = moduleName.trim();
        modules.add((Module) Class.forName(moduleName).newInstance());
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException("Error adding new module to guice", e);
      }
    }
    Injector injector = Guice.createInjector(Stage.PRODUCTION, modules);
    servletContext.setAttribute(INJECTOR_ATTRIBUTE_NAME, injector);
    log.trace("Leaving init");
  }

  /** Called by the container when the context is about to be destroyed.
   *
   * This implementation destroys the guice injector.
   */
  public void contextDestroyed(ServletContextEvent event) {
    log.trace("Entering destroy");
    ServletContext context = event.getServletContext();
    context.removeAttribute(INJECTOR_ATTRIBUTE_NAME);
    log.trace("Leaving destroy");
  }
}

