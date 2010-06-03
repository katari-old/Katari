/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Class that's responsible for initializing the modules.
 * @author pablo.saavedra
 */
public class ModuleInitializer implements ApplicationListener,
    ApplicationContextAware {

  /**
   * The logger, featuring slf4j.
   */
  private static Logger log = LoggerFactory.getLogger(ModuleInitializer.class);

  /**
   * The module registry, where the modules to initialize will be located.
   */
  private ModuleContextRegistrar contextRegistrar;

  /** The application context this bean runs in.
   *
   * This avoid reacting to events originated by other (children) application
   * contexts and initializing twice. This cannot be null once initialized.
   */
  private ApplicationContext context;

  /** Creates a module initializer with the given module context registrar.
   *
   * @param registry The module registry, cannot be null.
   */
  public ModuleInitializer(final ModuleContextRegistrar registry) {
    Validate.notNull(registry, "The registry cannot be null");
    contextRegistrar = registry;
  }

  /** Method executed when an application event is raised.
   *
   * If the event is a {@link ContextRefreshedEvent}, this bean will perform
   * module initialization. This also initializes the session factory if it was
   * defined in the application context.
   *
   * @param event The event, may be null (in which case the event is ignored).
   */
  public void onApplicationEvent(final ApplicationEvent event) {
    log.trace("Entering onApplicationEvent");

    Validate.notNull(context, "The application context has not been set");

    if (!(event instanceof ContextRefreshedEvent)) {
      log.trace("Leaving onApplicationEvent");
      return;
    }
    ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent) event;
    if (refreshEvent.getApplicationContext() != context) {
      log.debug("Received a context refreshed event from another application"
          + " context, ignoring it", refreshEvent.getApplicationContext());
      log.trace("Leaving onApplicationEvent");
      return;
    }
    if (contextRegistrar.isInitialized()) {
      log.trace("Leaving onApplicationEvent");
      return;
    }
    log.debug("Starting module initialization");
    // Gets all the modules and calls init on them.
    for (String beanName : contextRegistrar.getModuleBeanNames()) {
      Object moduleBean = context.getBean(beanName);
      if (!(moduleBean instanceof Module)) {
        throw new IllegalArgumentException("The module " + beanName
            + " does not implement Module");
      }
      Module module = (Module) moduleBean;
      log.debug("Initializing module {}", beanName);
      String name = ModuleUtils.getModuleNameFromBeanName(beanName);
      ModuleContext moduleContext = contextRegistrar.getNewModuleContext(name);
      module.init(moduleContext);
    }
    contextRegistrar.setInitialized();
    log.trace("Leaving onApplicationEvent");
  }

  /** Sets the application context this bean runs in.
   *
   * @param appContext The application context, cannot be null.
   */
  public void setApplicationContext(final ApplicationContext appContext) {
    Validate.notNull(appContext, "The application context cannot be null");
    context = appContext;
  }
}

