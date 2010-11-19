/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.hibernate.coreuser.DeleteMessage;

import org.apache.commons.lang.Validate;

/** Listens for user delete events and removes the data held by the user.
 */
public class DeleteUserListener {

  /** The gadget group repository that stores the user gadgets.
   *
   * This is never null.
   */
  private GadgetGroupRepository gadgetRepository;

  /** Constructor.
   *
   * @param theGadgetRepository The gadget group repository, never null.
   */
  public DeleteUserListener(final GadgetGroupRepository theGadgetRepository) {
    Validate.notNull(theGadgetRepository,
        "The gadget group repository cannot be null.");
    gadgetRepository = theGadgetRepository;
  }

  /** Deletes the user specified in message.
   *
   * @param message The delete event message sent by the user module when a
   * user is about to be deleted.DeleteMessage
   *
   * @return returns a goAhead message, never null.
   */
  public DeleteMessage remove(final DeleteMessage message) {
    gadgetRepository.removeGroupsFromUser(message.getUserId());
    return message.goAhead();
  }
}

