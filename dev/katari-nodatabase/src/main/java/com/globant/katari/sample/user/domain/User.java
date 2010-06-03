/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.user.domain;

import org.apache.commons.lang.Validate;

/** Defines the user entity.
 */
public class User {

  /** The id of the user.
   *
   * This is 0 for a newly created user.
   */
  private long id = 0;

  /** The name of the user.
   */
  private String name;

  /** The email of the user.
   */
  private String email;

  /** The password that the user must know to log in.
   *
   * It is an empty string by default. This is never null.
   */
  private String password = "";

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

  /** Validates the password of the user.
   *
   * @param thePassowrd The password to validate.
   *
   * @return Returns true if the provided password matches the user password,
   * false otherwise.
   */
  public boolean validatePassword(final String thePassowrd) {
    return password.equals(thePassowrd);
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

  /** Sets the user name.
   *
   * @param theName The user name.  It cannot be null.
   */
  public void setName(final String theName) {
    name = theName;
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

  /** Set the email.
   *
   * @param theEmail The email. It cannot be null.
   */
  public void setEmail(final String theEmail) {
    email = theEmail;
  }
}

