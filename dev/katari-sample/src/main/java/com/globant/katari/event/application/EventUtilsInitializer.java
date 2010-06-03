/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.event.application;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** Initialises the EventUtils with the current Spring {@link
 * ApplicationEventPublisher}.
 *
 * Must be called after the {@link ApplicationContext} has been created. This
 * is intended to be configured after the spring ContextLoaderListener.
 *
 * @author Roman G. Cunci
 */
public class EventUtilsInitializer implements ApplicationContextAware {

  /** Called when the bean is created.
   *
   * It initializes the EventUtils with the spring application context passed
   * as parameter.
   *
   * @param context The application context. It cannot be null.
   */
  public void setApplicationContext(final ApplicationContext context) {
    EventUtils.init(context);
  }
}

