/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.application;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Initializable;
import com.globant.katari.core.application.Validatable;
import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.hibernate.coreuser.SecurityUtils;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** Save user command.
 *
 * The execution of this command saves a user into the user repository.
 *
 * @author nicolas.frontini
 */
public class SaveUserCommand implements Command<Void>, Validatable,
    Initializable {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(SaveUserCommand.class);

  /** The user repository.
   */
  private UserRepository userRepository;

  /** The role repository.
   */
  private RoleRepository roleRepository;

  /** The id of the user.
   */
  private long userId = 0;

  /** The user loaded from the userRepository or null if new.
  */
  private User user = null;

  /**
   * The list of all roles available in the system.
   * It is null .
   */
  private List<Role> availableRoles = null;

  /** The user profile.
   *
   * It is null when changing the password of an existing user.
   */
  private Profile profile;

  /** The user password.
   *
   * It is null when changing the profile of an existing user.
   */
  private Password password;

  /** Constructor to dynamically proxy this type of command.
   */
  protected SaveUserCommand() {
  }

  /** Constructor, builds a SaveUserCommand suitable for creating new users.
   *
   * @param theUserRepository The user repository. It cannot be null.
   *
   * @param theRoleRepository The role repository. It cannot be null.
   *
   * @param theProfile The user profile. It cannot be null.
   *
   * @param thePassword The password. It cannot be null.
   */
  public SaveUserCommand(final UserRepository theUserRepository, final
      RoleRepository theRoleRepository, final Profile theProfile, final
      Password thePassword) {
    Validate.notNull(theUserRepository, "The user repository cannot be null");
    Validate.notNull(theRoleRepository, "The role repository cannot be null");
    Validate.notNull(theProfile, "The user profile helper cannot be null");
    Validate.notNull(thePassword, "The password helper cannot be null");
    userRepository = theUserRepository;
    roleRepository = theRoleRepository;
    profile = theProfile;
    password = thePassword;
  }

  /** Constructor, builds a SaveUserCommand suitable for modifying the user
   * profile.
   *
   * @param theUserRepository The user repository. It cannot be null.
   *
   * @param theRoleRepository The role repository. It cannot be null.
   *
   * @param theProfile The user profile. It cannot be null.
   */
  public SaveUserCommand(final UserRepository theUserRepository, final
      RoleRepository theRoleRepository, final Profile theProfile) {
    Validate.notNull(theUserRepository, "The user repository cannot be null");
    Validate.notNull(theRoleRepository, "The role repository cannot be null");
    Validate.notNull(theProfile, "The user profile helper cannot be null");
    userRepository = theUserRepository;
    roleRepository = theRoleRepository;
    profile = theProfile;
  }

  /** Constructor, builds a SaveUserCommand suitable for changing the user
   * password.
   *
   * @param theUserRepository The user repository. It cannot be null.
   *
   * @param theRoleRepository The role repository. It cannot be null.
   *
   * @param thePassword The password. It cannot be null.
   */
  public SaveUserCommand(final UserRepository theUserRepository, final
      RoleRepository theRoleRepository, final Password thePassword) {
    Validate.notNull(theUserRepository, "The user repository cannot be null");
    Validate.notNull(theRoleRepository, "The role repository cannot be null");
    Validate.notNull(thePassword, "The password helper cannot be null");
    userRepository = theUserRepository;
    roleRepository = theRoleRepository;
    password = thePassword;
  }

  /** Returns the id of the user.
   *
   * @return Returns the user id, 0 for a new user.
   */
  public long getUserId() {
    return userId;
  }

  /** Sets the id of the user.
   *
   * @param theUserId The id of the user. It cannot be 0.
   */
  public void setUserId(final long theUserId) {
    Validate.notNull(theUserId, "The user id cannot be 0.");
    userId = theUserId;
  }

  /** Returns the profile.
   *
   * @return Returns the profile. It returns null only when modifying an
   * existing user password.
   */
  public Profile getProfile() {
    return profile;
  }

  /** Returns the password.
   *
   * @return Returns the password. It returns null only when modifying the user
   * profile.
   */
  public Password getPassword() {
    return password;
  }

  /** Returns all the available roles.
   *
   * @return Returns all the available roles, as a map of id to role name.  It
   * Never returns null.
   */
  public Map<String, String> getAvailableRoles() {
    // User Roles.
    Map<String, String> rolesMap = new LinkedHashMap<String, String>();
    for (Role role : availableRoles) {
      rolesMap.put(String.valueOf(role.getId()), role.getName());
    }
    return rolesMap;
  }

  /**
   * Returns the list of available roles.
   * @return The list of roles, never null.
   */
  public List<Role> getRoles() {
    return Collections.unmodifiableList(availableRoles);
  }

  /** Initializes this command form the specified userId.
   *
   * This loads the user specified in the user id. If it is not found, it throws
   * an IllegalArgumentException.
   */
  public void init() {
    log.trace("Entering init");
    this.availableRoles = roleRepository.getRoles();
    if (userId != 0) {
      user = userRepository.findUser(Long.valueOf(getUserId()));
      if (user == null) {
        throw new IllegalArgumentException("The user id was not found");
      }
      if (profile != null) {
        profile.init(user);
      }
    }
    log.trace("Leaving init");
  }

  /** Validates the user profile and the password data if it is a new user.
   *
   * Validates the user profile if it is a preexistent user being edited.
   *
   * Validates the password data if it is a change password action on a
   * preexistent user.
   *
   * @param errors Contextual state about the validation process. It can not be
   * null.
   */
  public void validate(final Errors errors) {
    log.trace("Entering validate");
    if (profile != null) {
      profile.validate(userRepository, getUserId(), errors);
    }
    if (password != null) {
      password.validate(user, errors);
    }
    log.trace("Leaving validate");
  }

  /** Modifies the user profile or password.
   *
   * Saves the user into the repository.
   *
   * The user must be logged in.
   *
   * The user is changing his own data or is an administrator.
   *
   * @return It returns nothing.
   */
  public Void execute() {
    log.trace("Entering execute");

    // TODO: Is this cast fine here? 
    User me = (User) SecurityUtils.getCurrentUser();
    if (me == null) {
      throw new RuntimeException("Not enough privileges");
    }

    user = userRepository.findUser(Long.valueOf(getUserId()));

    boolean editingMyself = (user != null && user.getId() == me.getId());
    if (!(editingMyself || me.isAdministrator())) {
      // Non administrators can only edit theirselves.
      throw new RuntimeException("Not enough privileges");
    }

    if (profile != null) {
      user = profile.apply(roleRepository, user);
    }
    if (password != null) {
      user = password.apply(user);
    }
    userRepository.save(user);

    log.trace("Leaving execute");
    return null;
  }
}

