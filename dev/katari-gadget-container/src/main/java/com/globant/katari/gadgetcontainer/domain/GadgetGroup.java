/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.Validate.notEmpty;

import java.util.HashSet;
import java.util.Set;

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

/** Represents a group of gadgets that can be displayed on a web page.
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

  /** {@link Set<GadgetInstance>} gadgets of the group.
   * It's never null.
   */
  @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
  @JoinColumn(name = "gadget_group_id")
  private Set<GadgetInstance> gadgets = new HashSet<GadgetInstance>();

  /** {@link String} the owner of this gadget group.
   *
   * If null, this is a shared group that can be used by everybody.
   */
  @Column(name = "owner")
  private String owner;

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
   */
  public GadgetGroup(final String user, final String groupName) {
    notEmpty(groupName, "group name can not be null nor empty");
    name = groupName;
    owner = user;
  }

  /** @return the id.
   */
  public long getId() {
    return id;
  }

  /** @return the group name, never null.
   */
  public String getName() {
    return name;
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

  /** @return the group owner, null for a shared gadget group.
   */
  public String getOwner() {
    return owner;
  }
}

