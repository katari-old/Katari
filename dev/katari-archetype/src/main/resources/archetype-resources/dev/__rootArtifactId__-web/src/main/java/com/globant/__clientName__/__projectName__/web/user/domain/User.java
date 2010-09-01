#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.user.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.DiscriminatorValue;

import org.apache.commons.lang.Validate;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.SearchableComponent;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/** Defines the user entity.
 */
@Entity
@DiscriminatorValue("user")
@Searchable
public class User extends CoreUser {

  /** The length in characters of the email address.
   */
  private static final int EMAIL_LENGTH = 50;

  /** The length in characters of the password.
   */
  private static final int PASSWORD_LENGTH = 20;

  /** The email of the user.
   */
  @Column(name = "email", nullable = false, length = EMAIL_LENGTH)
  @SearchableProperty
  private String email;

  /** The password that the user must know to log in.
   *
   * It is an empty string by default. This is never null.
   */
  @Column(name = "password", nullable = false, length = PASSWORD_LENGTH)
  private String password = "";

  /* Tried w/ SearchableReference, but it did not work: the roles set is empty
   * after being found from index.  */
  /** The roles of the user.
   */
  @ManyToMany(fetch = FetchType.EAGER)
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
    super(theName);
    Validate.notNull(theMail, "The user email cannot be null");
    email = theMail;
  }

  /** Modifies the values of the entity.
   *
   * @param newName The new name of the user. It cannot be null.
   *
   * @param newEmail The new email of the user. It cannot be null.
   */
  public void modify(final String newName, final String newEmail) {
    Validate.notNull(newEmail, "The user email cannot be null");
    setName(newName);
    email = newEmail;
  }

  /** Changes the user password.
   *
   * @param newPassword The new user password. It cannot be null.
   *
   * TODO Decide how to manage the password restrictions. Should this decision
   * be delegated to a strategy?
   */
  public void changePassword(final String newPassword) {
    Validate.notNull(newPassword, "The password cannot be null");
    password = newPassword;
  }

  /** Adds the specified role.
   *
   * Ignores duplicates.
   *
   * @param theRole The role to add. It cannot be null.
   *
   * @return true if the role was added
   */
  public boolean addRole(final Role theRole) {
    Validate.notNull(theRole, "The role cannot be null");
    return roles.add(theRole);
  }

  /** Removes the specified role.
   *
   * @param theRole The role to be deleted.
   *
   * @return true if the list contained the specified role
   */
  public boolean removeRole(final Role theRole) {
    return roles.remove(theRole);
  }

  /** Validates the password of the user.
   *
   * @param thePassword The password to validate.
   *
   * @return Returns true if the provided password matches the user password,
   * false otherwise.
   */
  public boolean validatePassword(final String thePassword) {
    return password.equals(thePassword);
  }

  /** Returns the password of the user.
   *
   * @return Returns the password. Never returns null.
   */
  public String getPassword() {
    return password;
  }

  /** Returns the email of the user.
   *
   * @return the email address.
   */
  public String getEmail() {
    return email;
  }

  /** Returns the roles of the user.
   *
   * @return the roles
   */
  public Set<Role> getRoles() {
    return roles;
  }

  /** Returns if the user has administrator privileges.
   *
   * Users with administrator privileges are in the role named ADMINISTRATOR.
   *
   * @return true if the user is in the ADMINISTRATOR role.
   */
  public boolean isAdministrator() {
    for (Role role : roles) {
      if (role.getName().equals("ADMINISTRATOR")) {
        return true;
      }
    }
    return false;
  }
}

