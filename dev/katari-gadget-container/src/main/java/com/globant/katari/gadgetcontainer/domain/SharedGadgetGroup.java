/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/** A gadget group that is shared between diferent users.
 *
 * Static gadget groups do not have an owner, and are accesible to everybody.
 */
@Entity
@DiscriminatorValue("shared")
public class SharedGadgetGroup extends GadgetGroup {

  /** Hibernate constructor.
   */
  SharedGadgetGroup() {
  }

  /** Builds a gadget group template.
   *
   * @param groupName name of the group. It cannot be null
   *
   * @param viewName name of the view. This gadget will only contain gadgets
   * that support this view or the default view. It cannot be null
   *
   * @param columns the number of columns in the group. It must be 1 or
   * greater.
   */
  public SharedGadgetGroup(final String groupName, final String viewName,
      final int columns) {
    super(groupName, viewName, columns);
  }

  /** Tells if this gadget group is customizable.
   *
   * A customizable gadget group allows the user to move, add and remove
   * gadgets.
   *
   * @return true if the gadget group is customizable, false otherwise.
   */
  @Override
  public boolean isCustomizable() {
    return false;
  }
}

