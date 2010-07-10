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
   * If null, this is a shared group that can be used by everybody.
   */
  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  private CoreUser owner;

  /** The gadgets in this group.
   *
   * It is never null.
   */
  @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
  @JoinColumn(name = "gadget_group_id")
  private Set<GadgetInstance> gadgets = new HashSet<GadgetInstance>();

  @Column(name = "number_of_columns", nullable = false)
  private int numberOfColumns = 1;

  /** Hibernate constructor.
   */
  GadgetGroup() {
  }

  /** Constructor.
   *
   * @param user the user that owns the gadget group. If null, it is a shared
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

  /** @return the group owner, null for a shared gadget group.
   */
  public CoreUser getOwner() {
    return owner;
  }

  /** @return the gadgets. It never returns null.
   */
  public Set<GadgetInstance> getGadgets() {
    return gadgets;
  }

  /** @param instance the gadget to add to this group. It cannot be null.
   */
  public void addGadget(final GadgetInstance instance) {
    gadgets.add(instance);
  }

  /** Returns the number of columns.
   *
   * @return the number of columns, 1 or greater.
   */
  public int getNumberOfColumns() {
    return numberOfColumns;
  }
}

