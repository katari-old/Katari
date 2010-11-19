/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser;

import org.apache.commons.lang.Validate;

import java.util.List;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A message to notify that a user is being deleted, and its response.
 */
public class DeleteMessage {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(DeleteMessage.class);

  /** The id of the user to delete.
   */
  private long userId;

  /** The list of aggregated causes that stops the entity from being deleted.
   *
   * This is never null.
   */
  private List<String> causes = new LinkedList<String>();

  /** Marks this delete message as a delete request.
   */
  private boolean request = true;

  /** Creates a message to notify listeners that a user is being deleted.
   *
   * @param theUserId the id of the user to delete.
   */
  public DeleteMessage(final long theUserId) {
    userId = theUserId;
  }

  /** Constructor for a 'goAhead' delete message response.
   *
   * @param theUserId the id of the user to delete.
   *
   * @param response ignored. This is a 'mark' parameter to distinguish this
   * constructor from the delete user request constructor.
   */
  private DeleteMessage(final long theUserId, final boolean response) {
    request = false;
    userId = theUserId;
  }

  /** Constructor for a 'reject' delete message response.
   *
   * @param theUserId the id of the user to delete.
   *
   * @param theCause The motive of the rejection. It cannot be null.
   */
  private DeleteMessage(final long theUserId, final String theCause) {
    Validate.notNull(theCause, "The cause cannot be null.");
    request = false;
    userId = theUserId;
    causes.add(theCause);
  }

  /** Constructor for an aggregated 'reject' delete message response.
   *
   * @param theUserId the id of the user to delete.
   *
   * @param theCauses The motives of the rejection. It cannot be null.
   */
  private DeleteMessage(final long theUserId, final List<String> theCauses) {
    Validate.notNull(theCauses, "The causes cannot be null.");
    request = false;
    userId = theUserId;
    causes.addAll(theCauses);
  }

  /** Obtains the id of the user to delete.
   *
   * @return the user id.
   */
  public long getUserId() {
    return userId;
  }

  /** Generates a go ahead message response to this request.
   *
   * @return a delete message response to allow the delete operation.
   */
  public DeleteMessage goAhead() {
    Validate.isTrue(request, "This only makes sense for a request.");
    return new DeleteMessage(userId, true);
  }

  /** Generates a reject message response to this request.
   *
   * @param theCause The reason that this delete request is rejected. It cannot
   * be null.
   *
   * @return a delete message response to reject the delete operation.
   */
  public DeleteMessage reject(final String theCause) {
    Validate.isTrue(request, "This only makes sense for a request.");
    return new DeleteMessage(userId, theCause);
  }

  /** True if the user can be safely deleted.
   *
   * This operation also returns true if this message corresponds to a request
   * event. This is helpful if you want to use the default null event listener,
   * that just copies the input to its output.
   *
   * @return true if the user can be deleted.
   */
  public boolean canDelete() {
    return causes.isEmpty();
  }

  /** A message to show to the admin if the user could not be deleted.
   *
   * Can only be callod if canDelete returns false.
   *
   * @param prefix the prefix to prepend to each of the reject causes. It
   * cannot be null.
   *
   * @param suffix the suffix to append to each of the reject causes. It cannot
   * be null.
   *
   * @return a string with the message to show.
   */
  public String getMessage(final String prefix, final String suffix) {
    Validate.notNull(prefix, "The prefix cannot be null.");
    Validate.notNull(suffix, "The suffix cannot be null.");
    StringBuilder builder = new StringBuilder();
    for (String cause : causes) {
      builder.append(prefix).append(cause).append(suffix);
    }
    return builder.toString();
  }

  /** Creates a new message that results in merging this message with the
   * provided one.
   *
   * The resulting message can only be a go ahead message if this message and
   * the provided one are both go ahead messages. Otherwise, the returned
   * message in a rejection.
   *
   * The new message contains all the causes of both messages.
   *
   * @param message The message to merge with this. It cannot be null.
   *
   * @return a new aggregated message, never null.
   */
  public DeleteMessage aggregate(final DeleteMessage message) {
    log.trace("Entering aggregate({})", message);
    Validate.notNull(message, "The message cannot be null.");
    Validate.isTrue(!request, "This only makes sense for a response.");
    DeleteMessage result = this;
    if (!message.canDelete()) {
      List<String> allCauses = new LinkedList<String>();
      if (canDelete()) {
        allCauses.addAll(message.causes);
        result = new DeleteMessage(userId, allCauses);
      } else {
        allCauses.addAll(causes);
        allCauses.addAll(message.causes);
        result = new DeleteMessage(userId, allCauses);
      }
    }
    log.trace("Leaving aggregate");
    return result;
  }

  /** Returns a human readable representation of this message.
   *
   * @return a string, never null.
   */
  public String toString() {
    if (request) {
      return "Request to delete user " + userId;
    } else if (causes.isEmpty()) {
      return "Request to delete user " + userId + " allowed.";
    } else {
      return "Request to delete user " + userId + " rejected:"
        + getMessage("", "\n");
    }
  }
}

