#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.web.user.application;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import ${package}.web.user.domain.User;

/** The passwword handling part of the user command.
 */
public class Password {

  /** The password minimum length.
   */
  private static final int MINIMUM_PASSWORD_LENGTH = 6;

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(Password.class);

  /** The new value for the password.
   *
   * It is an empty string by default. This is never null.
   */
  private String newPassword = "";

  /** The confirmation password.
   *
   * It is an empty string by default. This is never null.
   */
  private String confirmedPassword = "";

  /** The old password.
   *
   * It is an empty string by default. This is never null.
   */
  private String oldPassword = "";

  /** Returns the password of the user.
   *
   * @return Returns the password. Never returns null.
   */
  public String getNewPassword() {
    return newPassword;
  }

  /** Sets the new password of the user.
   *
   * @param password The new password. It cannot be null.
   */
  public void setNewPassword(final String password) {
    Validate.notNull(password, "The password cannot be null");
    newPassword = password;
  }

  /** Returns the confirmation password of the user.
   *
   * @return Returns the confirmation password. Never returns null.
   */
  public String getConfirmedPassword() {
    return confirmedPassword;
  }

  /** Sets the confirmation password of the user.
   *
   * @param theConfirmedPassword The confirmation password. It cannot be null.
   */
  public void setConfirmedPassword(final String theConfirmedPassword) {
    Validate.notNull(theConfirmedPassword,
        "The confirmation password cannot be null");
    confirmedPassword = theConfirmedPassword;
  }

  /** Returns the old password of the user.
   *
   * @return Returns the old password. Never returns null.
   */
  public String getOldPassword() {
    return oldPassword;
  }

  /** Sets the old password of the user.
   *
   * @param theOldPassword The old password. It cannot be null.
   */
  public void setOldPassword(final String theOldPassword) {
    Validate.notNull(theOldPassword, "The old password cannot be null");
    oldPassword = theOldPassword;
  }

  /** Applies the password changes to the previously loaded user.
   *
   * @param user The user whose password is being changed. It cannot be null.
   *
   * @return the modified user.
   */
  public User apply(final User user) {
    log.trace("Entering applying change password");
    Validate.notNull(user, "The user cannot be null");
    user.changePassword(getNewPassword());
    log.trace("Leaving applying change password");
    return user;
  }

  /** Validate the password and confirmation password.
   *  If the user preexists, validates the old password.
   *
   * @param user The user loaded from the database when editing a user, null
   * for a new user.
   *
   * @param errors Contextual state about the validation process. It can not be
   * null.
   */
  public void validate(final User user, final Errors errors) {
    log.trace("Entering validate");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "password.newPassword", "required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "password.confirmedPassword", "required");

    if (getNewPassword().trim().length() < MINIMUM_PASSWORD_LENGTH) {
      errors.rejectValue("password.newPassword", "field.min.length");
    }
    if (!getNewPassword().equals(getConfirmedPassword())) {
      errors.rejectValue("password.confirmedPassword", "field.not.equal");
    }

    // The user already exists and changes the password.
    boolean oldPasswordMatches = (user == null || "".equals(getOldPassword())
      && user.getPassword().equals(oldPassword));
    if (!oldPasswordMatches) {
      errors.rejectValue("password.oldPassword", "field.not.equal");
    }
    log.trace("Leaving validate");
  }
}

