/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.role.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * This class is responsible for managing the persistence of roles.
 */
public final class RoleRepository extends HibernateDaoSupport {

  /**
   * Removes the specified role from the database.
   *
   * @param role The role to remove. It cannot be null.
   */
  public void remove(final Role role) {
    Validate.notNull(role, "The role cannot be null");
    getHibernateTemplate().delete(role);
  }

  /**
   * Saves a new role or updates an existing role to the database.
   *
   * @param role The role to save. It cannot be null.
   */
  public void save(final Role role) {
    Validate.notNull(role, "The role cannot be null");
    getHibernateTemplate().saveOrUpdate(role);
  }

  /**
   * Finds the role with the specified id.
   *
   * @param id The id of the role to search for.
   *
   * @return Returns the role with the specified id, or null if no such role
   * exists.
   */
  public Role findRole(final long id) {
    Role role = (Role) getHibernateTemplate().get(Role.class, id);
    return role;
  }

  /**
   * Finds the role with the specified name.
   *
   * @param name The name of the role to search for. It cannot be null.
   *
   * @return Returns the role with the specified name, or null if no such role
   * exists.
   */
  @SuppressWarnings("unchecked")
  public Role findRoleByName(final String name) {

    Validate.notNull(name, "The role name cannot be null");

    List<Role> roles = getHibernateTemplate().find(
        "from Role role where role.name = ?", name);
    if (roles.isEmpty()) {
      return null;
    } else {
      return roles.get(0);
    }
  }

  /**
   * Gets all the roles.
   *
   * @return Returns a list with the reles. If there are no roles, it returns
   * the empty list.
   */
  @SuppressWarnings("unchecked")
  public List<Role> getRoles() {
    return getHibernateTemplate().find("from Role");
  }

  /**
   * Gets roles by ids.
   *
   * @param ids The ids of the roles. It cannot be null.
   *
   * @return Returns a list with the roles specified in the array of ids. If
   * there are no roles, it returns the empty list.
   */
  public List<Role> getRoles(final List<String> ids) {
    Validate.notNull(ids, "The list of ids cannot be null.");
    List<Role> roles = new ArrayList<Role>();
    for (Role role : getRoles()) {
      if (ids.contains(String.valueOf(role.getId()))) {
        roles.add(role);
      }
    }
    return roles;
  }
}
