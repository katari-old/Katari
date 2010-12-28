/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.DiscriminatorValue;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.shindig.domain.Application;

/** A gadget group that can be customized by the user.
 *
 * Customizable gadget groups always have an owner and the ownar can reorganize
 * the gadgets, add and remove gadgets.
 */
@Entity
@DiscriminatorValue("customizable")
public class CustomizableGadgetGroup extends GadgetGroup {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      CustomizableGadgetGroup.class);

  /** The owner of this gadget group.
   *
   * If null, this is a static group.
   */
  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  private CoreUser owner = null;

  /** Hibernate constructor.
   */
  CustomizableGadgetGroup() {
  }

  /** Constructor.
   *
   * @param user the user that owns the gadget group. It cannot be null.
   *
   * @param groupName name of the group. It cannot be null
   *
   * @param viewName name of the view. This gadget will only contain gadgets
   * that support this view or the default view. It cannot be null
   *
   * @param columns the number of columns in the group. It must be 1 or
   * greater.
   */
  public CustomizableGadgetGroup(final CoreUser user, final String groupName,
      final String viewName, final int columns) {
    super(groupName, viewName, columns);
    Validate.notNull(user, "The user name cannot be null.");
    owner = user;
  }

  /** Returns the owner of a customizable group.
   *
   * @return the group owner, never return null.
   */
  public CoreUser getOwner() {
    return owner;
  }

  /** Tells if this gadget group is customizable.
   *
   * A customizable gadget group allows the user to move, add and remove
   * gadgets.
   *
   * @return this implementation always returns true.
   */
  @Override
  public boolean isCustomizable() {
    return true;
  }

  /** Adds a gadget to the group.
   *
   * The gadget's column must be between 0 and the number of columns in this
   * group. The gadget must support this column's view.
   *
   * @param instance the gadget to add to this group. It cannot be null.
   */
  public void add(final GadgetInstance instance) {
    Validate.notNull(instance, "The gadget instance cannot be null.");
    Validate.isTrue(instance.getColumn() < getNumberOfColumns(),
        "The gadget group " + getName() + " has " + getNumberOfColumns()
        + " columns, and you are trying to add gadget "
        + instance.getApplication().getUrl() + " at column "
        + instance.getColumn());
    Validate.isTrue(instance.getApplication().isViewSupported(getView()),
        "The gadget " + instance.getApplication().getUrl()
        + " does not support group " + getName() + "'s view.");
    for (GadgetInstance gadget: getGadgets()) {
      if (gadget.getColumn() == instance.getColumn()
          && gadget.getOrder() >= instance.getOrder()) {
        gadget.move(gadget.getColumn(), gadget.getOrder() + 1);
      }
    }
    super.add(instance);
  }

  /** Removes the gadget instance with the provided id from the group.
   *
   * @param instanceId the id of the gadget instance to remove.
   *
   * @return true if the gadget was removed, false if it was not in the group.
   */
  public boolean remove(final long instanceId) {
    for (GadgetInstance gadget: getGadgets()) {
      if (gadget.getId() == instanceId) {
        super.remove(gadget);
        return true;
      }
    }
    return false;
  }

  /** Moves a gadget in the group to a new column and position in the column.
   *
   * The gadget instance id must exist in the gadget group, and the column must
   * be lower than numberOfColumns.
   *
   * @param gadgetInstanceId The id of the gadget instance to move.
   *
   * @param column The column to move the gadget to, starting from 0.
   *
   * @param order The position of the gadget in the column, starting from 0.
   */
  public void move(final long gadgetInstanceId, final int column, final int
      order) {
    Validate.isTrue(isCustomizable(), "The group is not customizable");
    Validate.isTrue(column < getNumberOfColumns(),
        "You cannot move past the last column");
    Validate.isTrue(column >= 0, "Negative columns are not accepted.");
    Validate.isTrue(column >= 0, "Negative orders are not accepted.");

    log.trace("Entering move({}, ...)", gadgetInstanceId);

    // The list of gadgets in the target column.
    List<GadgetInstance> gadgetsInColumn = new LinkedList<GadgetInstance>();

    // Finds all the gagdets in the same column. Also find the gadget to move.
    // The gadget to move is not in gadgetsInColumn.
    GadgetInstance gadgetToMove = null;
    for (GadgetInstance gadget : getGadgets()) {
      if (gadget.getId() == gadgetInstanceId) {
        gadgetToMove = gadget;
      } else if (gadget.getColumn() == column) {
        log.debug("Adding gadget id = {} for column {}.", gadget.getId(),
            column);
        gadgetsInColumn.add(gadget);
      }
    }

    Validate.notNull(gadgetToMove, "The gadget to move was not found.");

    // Sorts them by order.
    Collections.sort(gadgetsInColumn, new Comparator<GadgetInstance>() {
      public int compare(final GadgetInstance g1, final GadgetInstance g2) {
        return g1.getOrder() - g2.getOrder();
      }
    });

    // Inserts the new gadget.
    if (order < gadgetsInColumn.size()) {
      log.debug("Inserting gadget id = {} in position {}.",
          gadgetToMove.getId(), order);
      gadgetsInColumn.add(order, gadgetToMove);
    } else {
      log.debug("Adding gadget id = {} at the end of the column.",
          gadgetToMove.getId());
      gadgetsInColumn.add(gadgetToMove);
    }

    // Renumbers the gadgets.
    int newOrder = 0;
    for (GadgetInstance gadget : gadgetsInColumn) {
      log.debug("Moving gadget with id {} to {}.", gadget.getId(), newOrder);
      gadget.move(column, newOrder);
      ++newOrder;
    }
    log.trace("Leaving move");
  }

  /** Finds if there is a gadget in the group for the application.
   *
   * @param application The application to look for in the gadget group. It
   * cannot be null.
   *
   * @return true if the application was found in the group, false otherwise.
   */
  public boolean contains(final Application application) {
    Validate.notNull(application, "The application cannot be null.");
    for (GadgetInstance instance: getGadgets()) {
      if (application.getUrl().equals(instance.getApplication().getUrl())) {
        // The application is already in the group.
        return true;
      }
    }
    return false;
  }
}

