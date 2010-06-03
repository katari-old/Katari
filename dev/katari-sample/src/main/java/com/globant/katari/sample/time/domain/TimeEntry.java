/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.time.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.Validate;

import com.globant.katari.sample.user.domain.User;

/** A time entry stores the amount of work performed by a user on a given
 * project.
 *
 * <p>
 * Unless otherwise stated, the getters of this class do not return
 * <code>null</code>.
 * </p>
 */
@Entity
@Table(name = "time_entries")
public class TimeEntry {

  /** Time entry id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = 0;

  /** Activity done by the user on the specified project.
   *
   * This is never null.
   */
  @ManyToOne(targetEntity = Activity.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  /** User who have done the activity.
   *
   * This is never null.
   */
  @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /** Project to be charged the time entry.
   *
   * This is never null.
   */
  @ManyToOne(targetEntity = Project.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  /** The date for the time entry.
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "entry_date", nullable = false, unique = false)
  private Date entryDate;

  /** The time period this time entry covers. It cannot be null.
   */
  @Embedded
  private TimePeriod period;

  /** A brief explanation of what was done in the time period represented by
   * this entry. It cannot be null.
   */
  @Column(name = "comment", unique = false, nullable = false)
  private String comment;

  /** Creates an instance of TimeEntry.
   *
   * @param theActivity Activity done by the user. Activity cannot be null.
   *
   * @param theUser User who have done the activity. User cannot be null.
   *
   * @param theProject Project to be charged the time entry. Project cannot be
   * null.
   *
   * @param theEntryDate The date of the time entry, cannot be
   * <code>null</code>.
   *
   * @param thePeriod The period the time entry covers, cannot be
   * <code>null</code>.
   *
   * @param theComment A comment describing briefly what was done during the
   * entry. Cannot be null.
   */
  public TimeEntry(final Activity theActivity, final User theUser,
      final Project theProject, final Date theEntryDate,
      final TimePeriod thePeriod, final String theComment) {
    Validate.notNull(theActivity, "the activity cannot be null");
    Validate.notNull(theUser, "the user cannot be null");
    Validate.notNull(theProject, "the project cannot be null");
    Validate.notNull(theEntryDate, "the entry date cannot be null");
    Validate.notNull(thePeriod, "the time period cannot be null");
    Validate.notNull(theComment, "the comment cannot be null");
    activity = theActivity;
    user = theUser;
    project = theProject;
    entryDate = new Date(theEntryDate.getTime());
    period = thePeriod;
    comment = theComment;
  }

  /** The default constructor.
   *
   * Builds an empty time entry. This constuctor is used to make hibernate
   * happy.
   */
  protected TimeEntry() {
  }

  /** Obtain the time entry id.
   *
   * @return time entry id, 0 if the user was not persisted yet.
   */
  public long getId() {
    return id;
  }

  /** Returns the activity that was performed in this time entry.
   *
   * @return the activity. Never returns null.
   */
  public Activity getActivity() {
    return activity;
  }

  /** The user that entered the entry.
   *
   * @return the user. Never returns null.
   */
  public User getUser() {
    return user;
  }

  /** The project the user was working on during this entry.
   *
   * @return the project. Never returns null.
   */
  public Project getProject() {
    return project;
  }

  /** The date this entry was made.
   *
   * @return the entryDate. Never returns null.
   */
  public Date getEntryDate() {
    return new Date(entryDate.getTime());
  }

  /** The period of time worked.
   *
   * @return the period. Never returns null.
   */
  public TimePeriod getPeriod() {
    return period;
  }

  /** A comment stating what was done during the entry.
   *
   * @return the comment. Never returns null.
   */
  public String getComment() {
    return comment;
  }

  /** Modify an instance of TimeEntry.
   *
   * @param theActivity Activity done by the user. Activity cannot be null.
   *
   * @param theProject Project to be charged the time entry. Project cannot be
   * null.
   *
   * @param thePeriod The period the time entry covers, cannot be
   * <code>null</code>.
   *
   * @param theComment A comment describing briefly what was done during the
   * entry. Cannot be null.
   */
  public void modify(final Activity theActivity, final Project theProject,
      final TimePeriod thePeriod, final String theComment) {
    Validate.notNull(theActivity, "the activity cannot be null");
    Validate.notNull(theProject, "the project cannot be null");
    Validate.notNull(thePeriod, "the time period cannot be null");
    Validate.notNull(theComment, "the comment cannot be null");
    activity = theActivity;
    project = theProject;
    period = thePeriod;
    comment = theComment;
  }
}
