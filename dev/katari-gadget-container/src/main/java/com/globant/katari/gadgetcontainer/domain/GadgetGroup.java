/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;

import com.globant.katari.shindig.domain.Application;

/** Represents a group of gadgets that can be displayed on a web page.
 *
 * Gadgets are intended to be shown in a page in a column layout. Each gadget
 * defines the column and the position (order) in that column.
 *
 * This is an abstract base class. Subclasses implements specific behaviour for
 * gadget groups, like templates and customization.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "group_type",
    discriminatorType = DiscriminatorType.STRING)
@Table(name = "gadget_groups")
public abstract class GadgetGroup {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(GadgetGroup.class);

  /** The id of the gadget group, 0 for a newly created gadget group.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  /** The name of the group.
   *
   * This is never null.
   */
  @Column(nullable = false)
  private String name;

  /** The view of this gadget group.
   *
   * This will only hold gadgtes that support this view or the default view. It
   * is never null.
   */
  @Column(name = "view_name", nullable = false)
  private String view;

  /** The gadgets in this group.
   *
   * It is never null.
   */
  @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
  @JoinColumn(name = "gadget_group_id")
  private Set<GadgetInstance> gadgets = new HashSet<GadgetInstance>();

  /** The number of columns of the gadget group.
   */
  @Column(name = "number_of_columns", nullable = false)
  private int numberOfColumns = 1;

  /** Hibernate constructor.
   */
  GadgetGroup() {
  }

  /** Constructor.
   *
   * @param groupName name of the group. It cannot be null
   *
   * @param viewName name of the view. This gadget will only contain gadgets
   * that support this view or the default view. It cannot be null
   *
   * @param columns the number of columns in the group. It must be 1 or
   * greater.
   */
  public GadgetGroup(final String groupName, final String viewName,
      final int columns) {
    Validate.notEmpty(groupName, "Group name can not be null nor empty.");
    Validate.notEmpty(viewName, "View name can not be null nor empty.");
    Validate.isTrue(columns >= 1, "Number of columns must be 1 or greater.");

    name = groupName;
    view = viewName;
    numberOfColumns = columns;
  }

  /** Obtains the group id.
   *
   * @return the id, 0 for a newly created object.
   */
  public long getId() {
    return id;
  }

  /** Obtains the group name.
   *
   * @return the group name, never null.
   */
  public String getName() {
    return name;
  }

  /** Returns the name of the view.
   *
   * This gadget will only contain gadgets that support this view or the
   * default view.
   *
   * @return the view name, never null.
   */
  public String getView() {
    return view;
  }

  /** Returns an unmodifiable view of the gadget instances of this group.
   *
   * @return the gadgets. It never returns null.
   */
  public Set<GadgetInstance> getGadgets() {
    return Collections.unmodifiableSet(gadgets);
  }

  /** Returns the number of columns.
   *
   * @return the number of columns, 1 or greater.
   */
  public int getNumberOfColumns() {
    return numberOfColumns;
  }

  /** Finds if there is a gadget in the group for the application.
   *
   * @param application The application to look for in the gadget group. It
   * cannot be null.
   *
   * @return true if the application was found in the group, false otherwise.
   */
  public boolean contains(final Application application) {
    log.trace("Entering contains");
    Validate.notNull(application, "The application cannot be null.");
    for (GadgetInstance instance: getGadgets()) {
      if (application.getUrl().equals(instance.getApplication().getUrl())) {
        // The application is already in the group.
        log.trace("Leaving contains with true");
        return true;
      }
    }
    log.trace("Leaving contains with false");
    return false;
  }

  /** Adds a gadget to the group.
   *
   * @param instance the gadget instance to add to the group. It cannot be
   * null. The gadget must support the groups view.
   */
  protected void add(final GadgetInstance instance) {
    Validate.notNull(instance, "The instance cannot be null");
    Validate.isTrue(instance.getApplication().isViewSupported(view),
        "The gadget " + instance.getApplication().getUrl()
        + " does not support group " + name + "'s view.");
    gadgets.add(instance);
  }

  /** Removes a gadget from the group.
   *
   * @param instance the gadget instance to remove from the group. It cannot be
   * null.
   */
  protected void remove(final GadgetInstance instance) {
    Validate.notNull(instance, "The instance cannot be null");
    gadgets.remove(instance);
  }

  /** Tells if this gadget group is customizable.
   *
   * A customizable gadget group allows the user to move, add and remove
   * gadgets.
   *
   * @return true if the gadget group is customizable, false otherwise.
   */
  public abstract boolean isCustomizable();
}

