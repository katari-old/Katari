/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import com.globant.katari.gadgetcontainer.domain.GadgetGroup;

/** Decides if a user can see the gadget group owned by another user.
 *
 * Modules can implement this interface and give it to the gadgetcontainer to
 * allow users to see gadget groups that they do not own.
 */
public interface ViewerOwnerRestriction {

  /** Tells if the viewer can access a group owned by the provided owner.
   *
   * @param group the group that the viewer is trying to access. It is never
   * null.
   *
   * @param ownerId the id of the owner of the group. If the gadget group is
   * customizable, then this parameter corresponds the group's owner attribute.
   * It is never 0.
   *
   * @param viewerId the id of the user trying to view the gadget group. It is
   * never 0.
   *
   * @return implementations should return true if the viewer is allowed to see
   * the gadget group.
   */
  boolean canView(final GadgetGroup group, final long ownerId,
      final long viewerId);
}

