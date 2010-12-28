/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/** A template to create new gadget groups.
 *
 * Template gadget groups are not intended to be shown to the user, they serve
 * as the basis to create new gadget groups for users.
 */
@Entity
@DiscriminatorValue("template")
public class GadgetGroupTemplate extends GadgetGroup {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      GadgetGroupTemplate.class);

  /** Hibernate constructor.
   */
  GadgetGroupTemplate() {
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
  public GadgetGroupTemplate(final String groupName, final String viewName,
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

  /** Creates a new gadget group from this template for the provided owner.
   *
   * This operation can only be called on gadget group templates.
   *
   * @param user the user that will own the new gadget group. It cannot be
   * null.
   *
   * @return a new gadged group, never null.
   */
  public CustomizableGadgetGroup createFromTemplate(final CoreUser user) {
    log.trace("Entering createFromTemplate");
    Validate.notNull(user, "The user cannot be null.");
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, getName(), getView(),
        getNumberOfColumns());

    // Adds all the gadgetInstances.
    for (GadgetInstance gadget: getGadgets()) {
      group.add(new GadgetInstance(gadget));
    }

    log.trace("Leaving createFromTemplate");
    return group;
  }
}

