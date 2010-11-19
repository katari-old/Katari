/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.globant.katari.hibernate.coreuser.DeleteMessage;

/** Aggregates the result of two endpoints.
 *
 * This aggregator works only for messages of type DeleteMessage.
 */
public class DeleteMessageAggregator implements AggregationStrategy {

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(DeleteMessageAggregator.class);

  /** Aggregates both messages.
   *
   * @param oldExchange the result of the previous aggregations. If null, this
   * is the call to the first listener.
   *
   * @param newExchange the result of the listener. It is never null.
   *
   * @return returns the result of aggegating both exchanges, never null.
   */
  public Exchange aggregate(final Exchange oldExchange,
      final Exchange newExchange) {

    log.trace("Entering aggregate({}, {})", oldExchange, newExchange);

    DeleteMessage newMessage;
    newMessage = newExchange.getIn().getBody(DeleteMessage.class);

    DeleteMessage oldMessage;
    if (oldExchange == null) {
      oldMessage = newMessage;
    } else {
      oldMessage = oldExchange.getIn().getBody(DeleteMessage.class);
      log.debug("Old message is {})", oldMessage);
      oldMessage = oldMessage.aggregate(newMessage);
    }

    newExchange.getIn().setBody(oldMessage);
    log.trace("Leaving aggregate");
    return newExchange;
  }
}

