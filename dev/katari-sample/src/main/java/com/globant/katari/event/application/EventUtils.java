/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.event.application;

import org.apache.commons.lang.Validate;

import org.mule.extras.spring.events.MuleApplicationEvent;

import org.springframework.context.ApplicationEventPublisher;

/** Convenience methods to raise an event through the Spring event system.
 *
 * @author Roman G. Cunci
 */
public final class EventUtils {

  /** The Spring application event publisher. */
  private static ApplicationEventPublisher eventPublisher;

  /** Private default constructor, needed for a utility class.
   */
  private EventUtils() {
  }

  /** Initialises the event publisher.
   *
   * Cannot be called twice.
   *
   * @param theEventPublisher the Spring current ApplicationEventPublisher.  It
   * cannot be null.
   */
  static void init(final ApplicationEventPublisher theEventPublisher) {
    Validate.notNull(theEventPublisher,
        "The event publisher cannot be null");
    Validate.isTrue(eventPublisher == null, "Cannot initialise twice");

    eventPublisher = theEventPublisher;
  }

  /** Raises an event.
   *
   * If no full Mule endpoint is specified, a VM endpoint is assumed. The event
   * name must be of the form {[protocol]://}[event]. The protocol and :// is
   * optional. When not specified, vm:// is assumed. vm:// is used for intra VM
   * communication.
   *
   * Before raising an event, you must call init. EventUtilsInitialized does
   * this for you.
   *
   * @param eventName the event name. It cannot be null.
   *
   * @param message the event message to send. It cannot be null.
   */
  public static void raiseEvent(final String eventName, final Object message) {
    Validate.notNull(eventPublisher, "You must call init before raising an"
        + " event");
    Validate.notNull(eventName, "The event name cannot be null");
    Validate.notNull(message, "The event message cannot be null");
    Validate.isTrue(eventName.matches("([^ ]*://)?[^ ]*"),
        "The event name must match '([^ ]*://)?[^ ]*'");

    String endpoint = eventName;
    if (!eventName.contains("://")) {
      endpoint =  "vm://" + eventName;
    }

    MuleApplicationEvent event = new MuleApplicationEvent(message, endpoint);

    eventPublisher.publishEvent(event);
  }
}

