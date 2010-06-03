/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain.mock;

import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.CascadeType;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.SearchableComponent;

import com.globant.katari.hibernate.role.domain.Role;

/** Defines a user entity to be used for testing.
 */
@Entity
@Table(name = "users_mock")
@Searchable
public class User {

  /** The id of the user.
   *
   * This is 0 for a newly created user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @SearchableId
  private long id = 0;

  /** The name of the user.
   */
  @Column(name = "name", nullable = false, length = 50)
  @SearchableProperty
  private String name;

  /** The email of the user.
   */
  @Column(name = "email", nullable = false, length = 50)
  @SearchableProperty
  private String email;

  /** The roles of the user.
   */
  @ManyToMany(fetch = FetchType.EAGER, cascade={CascadeType.ALL})
  @JoinTable(name = "users_roles")
  @SearchableComponent
  private Set<Role> roles = new HashSet<Role>();

  /** The default constructor.
   *
   * Builds an empty user.
   */
  public User() {
  }

  /** A custom constructor.
   *
   * Builds a user with the most basic data it needs to have.
   *
   * @param theName The user name. It cannot be null.
   *
   * @param theMail The user email address.
   */
  public User(final String theName, final String theMail) {
    Validate.notNull(theName, "The user name cannot be null");
    Validate.notNull(theMail, "The user email cannot be null");
    name = theName;
    email = theMail;
  }

  /** Modifies the values of the entity.
   *
   * @param newName The new name of the user. It cannot be null.
   *
   * @param newEmail The new email of the user. It cannot be null.
   */
  public void modify(final String newName, final String newEmail) {
    Validate.notNull(newName, "The user name cannot be null");
    Validate.notNull(newEmail, "The user email cannot be null");
    name = newName;
    email = newEmail;
  }

  /** Returns the id of the user.
   *
   * @return Returns the user id, 0 if the user was not persisted yet.
   */
  public long getId() {
    return id;
  }

  /** Returns the name of the user.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /** Returns the email of the user.
   *
   * @return the email address.
   */
  public String getEmail() {
    return email;
  }

  public boolean addRole(final Role theRole) {
    Validate.notNull(theRole, "The role cannot be null");
    return roles.add(theRole);
  }

  public Set<Role> getRoles() {
    return roles;
  }
}

