package com.globant.katari.sample.time.domain;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.globant.katari.sample.user.domain.User;

/** This class is responsible for managing the persistence of time entries.
 *
 * @author nicolas.frontini
 */
public class TimeRepository extends HibernateDaoSupport {

  /** Finds a time entry by id.
   *
   * @param id The id of the time entry to search for.
   *
   * @return Returns the time entry with the specified id or null if no such
   * time entry exists.
   */
  public TimeEntry findTimeEntry(final long id) {
    TimeEntry timeEntry = (TimeEntry) getHibernateTemplate().get(
        TimeEntry.class, id);
    return timeEntry;
  }

  /** Finds a activity by id.
   *
   * @param id The id of the activity to search for.
   *
   * @return Returns the activity with the specified id or null if no such
   * activity exists.
   */
  public Activity findActivity(final long id) {
    Activity activity = (Activity) getHibernateTemplate().get(
        Activity.class, id);
    return activity;
  }

  /** Finds a project by id.
   *
   * @param id The id of the project to search for.
   *
   * @return Returns the project with the specified id or null if no such
   * project exists.
   */
  public Project findProject(final long id) {
    Project project = (Project) getHibernateTemplate().get(
        Project.class, id);
    return project;
  }

  /** Gets all the time entries of a user in the specified date.
   *
   * @param user Contain filter information. This parameter is passed by
   * reference to add aditional.
   *
   * @param date The date of the time entries. It cannot be null.
   *
   * @return Returns a list with the time entries. If there are no time
   * entries, it returns the empty list. Never returns null.
   */
  @SuppressWarnings("unchecked")
  public List<TimeEntry> getTimeEntries(final User user, final Date date) {
    Validate.notNull(user, "The user cannot be null");
    Validate.notNull(date, "The date cannot be null");

    Criteria criteria = getSession().createCriteria(TimeEntry.class);

    criteria.add(Restrictions.eq("entryDate", date));
    criteria.add(Restrictions.eq("user.id", user.getId()));
    return criteria.list();
  }

  /** Gets all the time entries.
   *
   * @return Returns a list with the time entries. If there are no time
   * entries, it returns the empty list. Never returns null.
   */
  @SuppressWarnings("unchecked")
  public List<TimeEntry> getTimeEntries() {
    return getHibernateTemplate().find("from TimeEntry");
  }

  /** Gets all the projects.
   *
   * @return Returns a list with the projects. If there are no projects, it
   * returns the empty list.
   */
  @SuppressWarnings("unchecked")
  public List<Project> getProjects() {
    return getHibernateTemplate().find("from Project");
  }

  /** Gets all the activities.
   *
   * @return Returns a list with the activities. If there are no activities, it
   * returns the empty list.
   */
  @SuppressWarnings("unchecked")
  public List<Activity> getActivities() {
    return getHibernateTemplate().find("from Activity");
  }

  /** Saves a new time entry or updates an existing time entry to the database.
   *
   * @param timeEntry The time entry to save. It cannot be null.
   */
  public void save(final TimeEntry timeEntry) {
    Validate.notNull(timeEntry, "The time entry cannot be null");
    getHibernateTemplate().saveOrUpdate(timeEntry);
  }

  /** Removes the specified time entry from the database.
   *
   * @param timeEntry The time entry to remove. It cannot be null.
   */
  public void remove(final TimeEntry timeEntry) {
    Validate.notNull(timeEntry, "The time entry cannot be null");
    getHibernateTemplate().delete(timeEntry);
  }
}
