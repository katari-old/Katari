/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.event.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.lang.Validate;

import org.mule.extras.spring.events.MuleSubscriptionEventListener;

import org.springframework.context.ApplicationEvent;

/** Handler for the test event.
 *
 * @author roman
 */
public class TestHandler implements MuleSubscriptionEventListener {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(TestHandler.class);

  /** Event subscriptions.
   *
   * It is never null.
   */
  private String [] subscriptions;

  public String message = null;

  /** Constructor.
   *
   * @param theSubscriptions Event subscriptions. Cannot be null.  Cannot have
   * null elements. Cannot be empty.
   */
  public TestHandler(final String[] theSubscriptions) {
    Validate.notNull(theSubscriptions);
    Validate.notEmpty(theSubscriptions);
    Validate.noNullElements(theSubscriptions);

    subscriptions = theSubscriptions;
  }

  /** {@inheritDoc}
   */
  public String[] getSubscriptions() {
    return subscriptions;
  }

  /** {@inheritDoc}
   */
  public void onApplicationEvent(final ApplicationEvent applicationEvent) {
    log.trace("Entering onApplicationEvent");
    log.debug("Source: " +  applicationEvent.getSource().getClass());
    if (applicationEvent.getSource() instanceof String) {
      test((String) applicationEvent.getSource());
    }
    log.trace("Leaving onApplicationEvent");
  }

  /** Shouldn't use this.
   *
   * @param theSubscriptions event subscriptions.
   */
  public void setSubscriptions(final String[] theSubscriptions) {
    throw new RuntimeException(
        "Shouldn't use this setter. Use the constructor instead.");
  }

  /* The 'service'. This should be in its own class, decoupled from the event
   * management system.
   */
  public void test(final String input) {
    if (log.isTraceEnabled()) {
      log.trace("Entering test('" + input + "')");
    }
    message = input;
    log.trace("Leaving test");
  }
}

