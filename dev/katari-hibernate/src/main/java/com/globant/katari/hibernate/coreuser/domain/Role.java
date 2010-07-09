/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

import org.apache.commons.lang.Validate;

/** A role determines what action a user can perform on the system.
 */
@Entity
@Table(name = "roles")
@Searchable(root = false)
public class Role {

  /** The length in characters of the role name.
   */
  private static final int ROLE_NAME_LENGTH = 50;

  /** The id of the role.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @SearchableId
  private long id = 0;

  /** The name of the role.
   */
  @Column(name = "name", nullable = false, length = ROLE_NAME_LENGTH)
  @SearchableProperty
  private String name;

  /** The default constructor.
   *
   * Builds an empty role.
   */
  protected Role() {
    // Nothing to see here, move along.
  }

  /** Creates an instance of Role.
   *
   * @param theName The name of the role. It cannot be null.
   */
  public Role(final String theName) {
    Validate.notNull(theName, "The role name cannot be null.");
    name = theName;
  }

  /** Modifies the values of the role.
   *
   * @param theName The name of the role. It cannot be null.
   */
  public void modify(final String theName) {
    Validate.notNull(theName, "The new role name cannot be null.");
    name = theName;
  }

  /** Returns the name of the role.
   *
   * @return the name, never returns null.
   */
  public String getName() {
    return name;
  }

  /** Returns the id of the role.
   *
   * @return Returns the role id.
   */
  public long getId() {
    return id;
  }

  /** Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   *
   * @return <code>true</code> if this object is the same as the obj
   */
  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Role)) {
      return false;
    }
    Role other = (Role) obj;
    return (other.name.equals(name) && (other.id == id));
  }

  /** Returns a hash code value for the object.
   *
   * @return a hash code value for the object.
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /** Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  @Override
  public String toString() {
    return "Role: " + name;
  }
}

