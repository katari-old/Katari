/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/** Represents a group of gadgets that can be displayed on a web page.
 *
 * Gadgets are intended to be shown in a page in a column layout. Each gadget
 * defines the column and the position in that column.
 *
 * Gadgets can be static or customizable. Static gadget groups do not have an
 * owner. If it is customizable, it always has an owner.
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 */
@Entity
@Table(name = "gadget_groups")
public class GadgetGroup {

  /** The id of the gadget group, 0 for a newly created gadget group.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  /** {@link String} name of the group.
   *
   * This is never null.
   */
  @Column(nullable = false)
  private String name;

  /** {@link String} the owner of this gadget group.
   *
   * If null, this is a static group.
   */
  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  private CoreUser owner = null;

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
   * @param user the user that owns the gadget group. If null, it is a static
   * group.
   *
   * @param groupName name of the group. It cannot be null
   *
   * @param columns the number of columns in the group. It must be 1 or
   * greater.
   */
  public GadgetGroup(final CoreUser user, final String groupName,
      final int columns) {
    Validate.notEmpty(groupName, "group name can not be null nor empty");
    Validate.isTrue(columns >= 1, "Number of columns must be 1 or greater.");
    name = groupName;
    owner = user;
    numberOfColumns = columns;
  }

  /** @return the id, 0 for a newly created object.
   */
  public long getId() {
    return id;
  }

  /** @return the group name, never null.
   */
  public String getName() {
    return name;
  }

  /** Returns the owner of a customizable group, or null if this is as static
   * group.
   *
   * If the group is customizable, this never returns null.
   *
   * @return the group owner, null for a static gadget group.
   */
  public CoreUser getOwner() {
    return owner;
  }

  /** @return the gadgets. It never returns null.
   */
  public Set<GadgetInstance> getGadgets() {
    return gadgets;
  }

  /**  Adds a gadget to the group.
   *
   * The gadget's column must be between 0 and the number of columns in this
   * group.
   *
   * @param instance the gadget to add to this group. It cannot be null.
   */
  public void addGadget(final GadgetInstance instance) {
    Validate.notNull(instance, "The gadget instance cannot be null.");
    Validate.isTrue(instance.getColumn() < numberOfColumns,
        "You cannot add a gadget for column greater than the gadget group's.");
    gadgets.add(instance);
  }

  /** Returns the number of columns.
   *
   * @return the number of columns, 1 or greater.
   */
  public int getNumberOfColumns() {
    return numberOfColumns;
  }

  /** Tells if this gadget group is customizable.
   *
   * A customizable gadget group allows the user to move, add and remove
   * gadgets.
   *
   * @return true if the gadget group is customizable, false otherwise.
   */
  public boolean isCustomizable() {
    return owner != null;
  }

  /** Moves a gadget in the group to a new column and position in the column.
   *
   * @param gadgetInstanceId The id of the gadget instance to move.
   *
   * @param column The column to move the gadget to, starting from 0.
   *
   * @param order The position of the gadget in the column, starting from 0.
   */
  public void move(final long gadgetInstanceId, final int column, final int
      order) {
    throw new RuntimeException("Not implemented yet");
  }
}

