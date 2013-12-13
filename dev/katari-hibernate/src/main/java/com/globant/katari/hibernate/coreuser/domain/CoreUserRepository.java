/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Query;

import com.globant.katari.hibernate.BaseRepository;

/** This class provides read only access to the user.
 *
 * User creation is left to a module. This repository gives you access to the
 * CoreUser only.
 */
public class CoreUserRepository extends BaseRepository {

  /** Finds the user with the specified id.
   *
   * @param id The id of the user to search for.
   *
   * @return Returns the user with the specified id, or null if no such user
   * exists.
   */
  public CoreUser findUser(final long id) {
    return (CoreUser) getSession().get(CoreUser.class, id);
  }

  /** Finds the user with the specified name.
   *
   * @param name The name of the user to search for. It cannot be null.
   *
   * @return Returns the user with the specified name, or null if no such user
   * exists.
   */
  @SuppressWarnings("unchecked")
  public CoreUser findUserByName(final String name) {

    Validate.notNull(name, "The user name cannot be null");
    Query query = getSession().createQuery(
        "from CoreUser user where user.name = ?");
    query.setParameter(0, name);
    List<CoreUser> users = query.list();
    if (users.isEmpty()) {
      return null;
    } else {
      return users.get(0);
    }
  }
}

