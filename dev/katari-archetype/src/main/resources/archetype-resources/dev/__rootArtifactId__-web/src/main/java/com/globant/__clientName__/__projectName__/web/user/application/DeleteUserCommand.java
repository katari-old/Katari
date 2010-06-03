#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.user.application;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.${clientName}.${projectName}.web.integration.SecurityUtils;

import com.globant.katari.core.application.Command;

import com.globant.${clientName}.${projectName}.web.user.domain.User;
import com.globant.${clientName}.${projectName}.web.user.domain.UserRepository;

/** Command to delete a user.
 *
 * @author nicolas.frontini
 */
public class DeleteUserCommand implements Command<Void> {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(DeleteUserCommand.class);

  /** The user repository.
   */
  private UserRepository userRepository;

  /** The id of the user.
   */
  private String userId;

  /** The contructor with the user repository.
   *
   * @param theUserRepository The user repository. It cannot be null.
   */
  public DeleteUserCommand(final UserRepository theUserRepository) {
    Validate.notNull(theUserRepository, "The user repository cannot be null");
    userRepository = theUserRepository;
  }

  /** Returns the id of the user.
   *
   * @return Returns the user id.
   */
  public String getUserId() {
    return userId;
  }

  /** Sets the id of the user.
   *
   * @param theUserId The id of the user. It cannot be null.
   */
  public void setUserId(final String theUserId) {
    Validate.notNull(theUserId, "The user id cannot be null.");
    userId = theUserId;
  }

  /** Update a user with the midified data.
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
    userRepository.remove(user);
    log.trace("Leaving execute");
    return null;
  }
}

