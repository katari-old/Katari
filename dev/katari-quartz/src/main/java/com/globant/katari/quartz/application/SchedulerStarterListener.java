/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.application;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.Validate;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/** Starts the quartz scheduler.
 *
 * The scheduler needs to be started after the spring application context is
 * fully initialized. The SchedulerFactoryBean in the spring configuration file
 * must be configured with autoStartup in false. This listener starts the
 * scheduler after the web application is fully initialized.
 */
public class SchedulerStarterListener implements ServletContextListener {

  /** The quartz scheduler to start, never null.
   */
  private Scheduler scheduler;

  /** Constructor, builds a SchedulerStarterListener.
   *
   * @param theScheduler The scheduler to start. It cannot be null.
   */
  public SchedulerStarterListener(final Scheduler theScheduler) {
    Validate.notNull(theScheduler);
    scheduler = theScheduler;
  }

  /** Starts the scheduler.
   *
   * @param sce this parameter is ignored.
   */
  public void contextInitialized(final ServletContextEvent sce) {
    try {
      scheduler.start();
    } catch (SchedulerException ex) {
      throw new RuntimeException("Error starting scheduler", ex);
    }
  }

  /** Stops the scheduler.
   *
   * @param sce this parameter is ignored.
   */
  public void contextDestroyed(final ServletContextEvent sce) {
    try {
      scheduler.shutdown();
    } catch (SchedulerException ex) {
      throw new RuntimeException("Error stopping scheduler", ex);
    }
  }
}

