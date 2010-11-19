/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.application;

import org.apache.commons.lang.Validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.hibernate.coreuser.SecurityUtils;

import com.globant.katari.core.application.Command;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.CamelContext;

import com.globant.katari.hibernate.coreuser.DeleteMessage;

import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** Command to delete a user.
 */
public class DeleteUserCommand implements Command<Void> {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(DeleteUserCommand.class);

  /** The katari event bus, never null.
   */
  private CamelContext eventBus;

  /** The user repository, never null.
   */
  private UserRepository userRepository;

  /** The id of the user.
   */
  private long userId;

  /** Constructor to dynamically proxy this type of command (mainly used by
   * wicket).
  */
  protected DeleteUserCommand() {
  }

  /** The contructor with the user repository.
   *
   * @param theUserRepository The user repository. It cannot be null.
   */
  public DeleteUserCommand(final CamelContext theEventBus,
      final UserRepository theUserRepository) {
    Validate.notNull(theEventBus, "The event bus cannot be null");
    Validate.notNull(theUserRepository, "The user repository cannot be null");
    eventBus = theEventBus;
    userRepository = theUserRepository;
  }

  /** Returns the id of the user.
   *
   * @return Returns the user id.
   */
  public long getUserId() {
    return userId;
  }

  /** Sets the id of the user.
   *
   * @param theUserId The id of the user.
   */
  public void setUserId(final long theUserId) {
    userId = theUserId;
  }

  /** Removes the user with the id passed in setUserId.
   *
   * This operation will fail if the user being deleted is the same as the
   * logged on user (the user tried to delete himself).
   *
   * @return Always return null.
   */
  public Void execute() {
    log.trace("Entering execute");
    if (SecurityUtils.getCurrentUser().getId() == Long.valueOf(getUserId())) {
      throw new RuntimeException("You cannot delete yourself.");
    }
    User user = userRepository.findUser(Long.valueOf(getUserId()));

    // Notify all interested parties that this user is being deleted.
    ProducerTemplate producer = eventBus.createProducerTemplate();
    DeleteMessage response = (DeleteMessage) producer.requestBody(
        "direct:katari.user.vetoDeleteUser", new DeleteMessage(userId));
    if (!response.canDelete()) {
      throw new RuntimeException(
          "We cannot delete the user: " + response.getMessage("", "\n"));
    }
    response = (DeleteMessage) producer.requestBody(
        "direct:katari.user.deleteUser", new DeleteMessage(userId));
    // Response is ignored here.

    userRepository.remove(user);
    log.trace("Leaving execute");
    return null;
  }
}

