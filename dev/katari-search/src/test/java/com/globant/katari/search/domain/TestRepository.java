/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain;

import java.util.List;

import org.apache.commons.lang.Validate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TestRepository extends HibernateDaoSupport {

  /** Removes all the object of the specified type from the database.
   *
   * @param type The class of the objects to remove. It cannot be null.
   */
  @SuppressWarnings("unchecked")
  public void removeAll(final Class type) {
    Validate.notNull(type, "The type cannot be null");

    List<Object> objects = findAll(type);
    for (Object o: objects) {
      getHibernateTemplate().delete(o);
    }
  }

  /** Finds all instance of a type.
   *
   * @param type The class of the object to search for. It cannot be null.
   *
   * @return Returns all the instance of the provided type. Never returns null.
   */
  @SuppressWarnings("unchecked")
  public List findAll(final Class type) {
    return getSession().createCriteria(type).list();
  }

  /** Saves a new object or updates an existing one to the database.
   *
   * @param o The object to save. It cannot be null.
   */
  public void save(final Object o) {
    Validate.notNull(o, "The object cannot be null");
    getHibernateTemplate().saveOrUpdate(o);
  }
}

