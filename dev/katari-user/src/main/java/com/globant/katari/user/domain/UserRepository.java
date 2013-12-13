/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.domain;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.globant.katari.hibernate.BaseRepository;
import com.globant.katari.user.domain.filter.ContainsFilter;
import com.globant.katari.user.domain.filter.Paging;
import com.globant.katari.user.domain.filter.Sorting;

/** This class is responsible for managing the persistence of users.
 */
public class UserRepository extends BaseRepository {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(UserRepository.class);

  /** Removes the specified user from the database.
   *
   * @param user The user to remove. It cannot be null.
   */
  public void remove(final User user) {
    Validate.notNull(user, "The user cannot be null");
    getSession().delete(user);
  }

  /** Finds a user by email.
   *
   * @param email The email of the user to search for.
   *
   * @return Returns the user with the specified email or null if no such user
   * exists.
   */
  @SuppressWarnings("unchecked")
  public User findUserByEmail(final String email) {
    List<User> users = (List<User>) find("from User where email = ?",
        email);
    if (users.isEmpty()) {
      return null;
    } else {
      return users.get(0);
    }
  }

  /** Finds the user with the specified user name.
   *
   * @param username The user name of the user to search for. It cannot be
   * null.
   *
   * @return Returns the user with the specified user name, or null if no such
   * user exists
   */
  @SuppressWarnings("unchecked")
  public User findUserByName(final String username) {
    Validate.notNull(username, "The username cannot be null");
    List<User> users = (List<User>) find(
        "from User user where user.name = ?", username);
    if (users.isEmpty()) {
      return null;
    } else {
      return users.get(0);
    }
  }

  /** Saves a new user or updates an existing user to the database.
   *
   * @param user The user to save. It cannot be null.
   */
  public void save(final User user) {
    Validate.notNull(user, "The user cannot be null");
    getSession().saveOrUpdate(user);
  }

  /** Gets all the users using a filter.
   *
   * @param userFilter Contain filter information. This parameter is passed by
   * reference to add aditional. It cannot be null.
   *
   * @return Returns a list with the user. If there are no users, it returns
   * the empty list. Never returns null.
   */
  @SuppressWarnings(value = { "unchecked" })
  public List<User> getUsers(final UserFilter userFilter) {
    log.trace("Entering getUsers");
    Validate.notNull(userFilter, "The user filter cannot be null");

    Criteria criteria = getSession().createCriteria(User.class);
    criteria.setCacheable(true);

    // Add the order criteria.
    Sorting sorting = userFilter.getSorting();
    if (!StringUtils.isBlank(sorting.getColumnName())) {
      if (sorting.isAscendingOrder()) {
        criteria.addOrder(org.hibernate.criterion.Order.asc(sorting
            .getColumnName()));
      } else {
        criteria.addOrder(org.hibernate.criterion.Order.desc(sorting
            .getColumnName()));
      }
    }

    // Add the restriction like.
    ContainsFilter contains = userFilter.getContainsFilter();
    if (!StringUtils.isBlank(contains.getColumnName())) {
      criteria.add(Restrictions.like(contains.getColumnName(), "%"
          + contains.getValue() + "%"));
    }

    // Set the paging configuration.
    Paging paging = userFilter.getPaging();
    if (paging.getPageSize() > 0) {
      int pageSize = paging.getPageSize();
      int totalResult = criteria.list().size();
      int totalPageNumber = totalResult / pageSize;
      if (totalResult % pageSize != 0) {
        totalPageNumber++;
      }
      paging.setTotalPageNumber(totalPageNumber);
      criteria.setFirstResult(paging.getPageNumber() * pageSize);
      criteria.setMaxResults(pageSize);
    }

    // Obtains the ids of the users matching the conditions.
    criteria.setProjection(Projections.projectionList().add(Projections.id()));
    List<Long> ids = criteria.list();

    List<User> result = new LinkedList<User>();
    if (!ids.isEmpty()) {
      criteria.add(Restrictions.in("id", ids));

      // Clean up from the last critiera run
      criteria.setProjection(null);
      criteria.setFirstResult(0);
      criteria.setMaxResults(Integer.MAX_VALUE);
      criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
      result = criteria.list();
    }
    log.trace("Leaving getUsers");
    return result;
  }

  /** Finds the user with the specified id.
   *
   * @param id The id of the user to search for.
   *
   * @return Returns the user with the specified id, or null if no such user
   * exists.
   */
  public User findUser(final long id) {
    return (User) getSession().get(User.class, id);
  }
}

