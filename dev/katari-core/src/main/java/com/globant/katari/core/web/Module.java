package com.globant.katari.core.web;

/** Interface that allows you to declare a module and register it in the
 * application.
 *
 * Upon initialization you will get a <code>ModuleContext</code> to be able to
 * interact with the application's environment.
 */
public interface Module {

  /** This methods constitutes a point of entry to the initialization
   * of your module.
   *
   * It is called just after properties have been set by the Spring container.
   * Also, a <code>ModuleContext</code> is handed out to you to be able to
   * communicate with the application environment.
   *
   * @param context Your context. It cannot be null.
   */
  void init(final ModuleContext context);

  /** This method will be called upon destruction of the application. */
  void destroy();
}

