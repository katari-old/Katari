package com.globant.katari.sample.time.application;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.globant.katari.user.integration.SecurityUtils;
import com.globant.katari.core.application.ValidatableCommand;
import com.globant.katari.sample.time.domain.Activity;
import com.globant.katari.sample.time.domain.Project;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimePeriod;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.user.domain.User;

/** Save user command.
 *
 * The execution of this command saves a user into the user repository.
 *
 * @author nicolas.frontini
 */
public class SaveTimeEntryCommand implements ValidatableCommand<Void> {

  /** The time repository.
   */
  private TimeRepository timeRepository;

  /** The id of the time entry.
   */
  private long timeEntryId = 0;

  /** The project name.
   */
  private long projectId;

  /** The activity name.
   */
  private long activityId;

  /** The start time.
   */
  private String start = null;

  /** The duration time.
   */
  private int duration;

  /** The comment.
   */
  private String comment;

  /** The date.
   */
  private Date date = new Date();

  /** The contructor with the time repository.
   *
   * @param theTimeRepository The time repository. It cannot be null.
   */
  public SaveTimeEntryCommand(final TimeRepository theTimeRepository) {
    Validate.notNull(theTimeRepository, "The time repository cannot be null");
    timeRepository = theTimeRepository;
  }

  /** Returns the id of the time entry.
   *
   * @return Returns the time entry id.
   */
  public long getTimeEntryId() {
    return timeEntryId;
  }

  /** Sets the id of the time entry.
   *
   * @param theTimeEntryId The id of the time entry.
   */
  public void setTimeEntryId(final long theTimeEntryId) {
    timeEntryId = theTimeEntryId;
  }

  /** Returns the project id.
   *
   * @return the project id.
   */
  public long getProjectId() {
    return projectId;
  }

  /** Sets the project id.
   *
   * @param theProjectId The project id.
   */
  public void setProjectId(final long theProjectId) {
    projectId = theProjectId;
  }

  /** Returns the activity id.
   *
   * @return the activity id.
   */
  public long getActivityId() {
    return activityId;
  }

  /** Sets the project id.
   *
   * @param theActivityId The activity id.
   */
  public void setActivityId(final long theActivityId) {
    activityId = theActivityId;
  }

  /** Returns the start time.
   *
   * @return the start time.
   */
  public String getStart() {
    return start;
  }

  /** Sets the start time.
   *
   * @param theStart The start time. It cannot be null.
   */
  public void setStart(final String theStart) {
    Validate.notNull(theStart, "The start time cannot be null.");
    start = theStart;
  }

  /** Returns the duration of the period in minutes.
   *
   * @return The duration of the period in minutes, greater than 0.
   */
  public int getDuration() {
    return duration;
  }

  /** Sets the duration time.
   *
   * @param theDuration The duration of the period in minutes, greater than 0.
   */
  public void setDuration(final int theDuration) {
    duration = theDuration;
  }

  /** Returns the comment of the time entry.
   *
   * @return the comment.
   */
  public String getComment() {
    return comment;
  }

  /** Sets the comment of the time entry.
   *
   * @param theComment The comment. It cannot be null.
   */
  public void setComment(final String theComment) {
    Validate.notNull(theComment, "The comment cannot be null.");
    comment = theComment;
  }

  /** Returns the date of the time entry. The default date is the current date.
   *
   * @return The date of the time entry.
   */
  public Date getDate() {
    return new Date(date.getTime());
  }

  /** Sets the date of the time entry.
   *
   * @param theDate The date. It cannot be null.
   */
  public void setDate(final Date theDate) {
    Validate.notNull(theDate, "The date cannot be null.");
    date = new Date(theDate.getTime());
  }

  /** Saves the domain time entry into the repository.
   *
   * @return It returns nothing.
   */
  public Void execute() {
    Activity activity = timeRepository.findActivity(getActivityId());
    Project project = timeRepository.findProject(getProjectId());
    if ((null == activity) || (null == project)) {
      throw new RuntimeException("Project or Activity cannot be null.");
    }
    TimePeriod period = new TimePeriod(getStart(), getDuration());
    TimeEntry timeEntry;
    if (getTimeEntryId() == 0) {
      User user = SecurityUtils.getCurrentUser();
      timeEntry = new TimeEntry(activity, user, project,
        getDate(), period, getComment());
    } else {
      timeEntry = timeRepository.findTimeEntry(getTimeEntryId());
      timeEntry.modify(activity, project, period, getComment());
    }

    timeRepository.save(timeEntry);
    return null;
  }

  /** Validates this command.
   *
   * @param errors Contextual state about the validation process. It can not be
   * null.
   */
  public void validate(final Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectId", "required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "activityId", "required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "duration", "required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "date", "required");

    if (getDuration() <= 0) {
      errors.rejectValue("duration", "positive");
    }
    validateTimePeriod(errors);
  }

  /** Validates the time period.
   *
   * @param errors Contextual state about the validation process. It can not be
   * null.
   */
  private void validateTimePeriod(final Errors errors) {
    if (start.indexOf(':') == -1) {
      errors.rejectValue("start", "invalid.format");
    } else {
      String[] hoursAndMinutes = start.split(":");
      if (2 != hoursAndMinutes.length) {
        errors.rejectValue("start", "invalid.format");
      } else {
        try {
          int hours = Integer.parseInt(hoursAndMinutes[0]);
          int minutes = Integer.parseInt(hoursAndMinutes[1]);

          if ((hours < 0) || (hours > TimePeriod.MAX_HOURS)) {
            errors.rejectValue("start", "invalid.hour",
                new Object[] {hours}, "");
          }
          if ((minutes < 0) || (minutes > TimePeriod.MAX_MINUTES)) {
            errors.rejectValue("start", "invalid.minutes",
                new Object[] {minutes}, "");
          }
          int endingMinutes = getEndingMinutes(hours, minutes, getDuration());
          if (endingMinutes
              > TimePeriod.DAY_DURATION * TimePeriod.HOUR_DURATION) {
            errors.rejectValue("start", "invalid.period");
          }

        } catch (NumberFormatException e) {
          errors.rejectValue("start", "invalid.format");
        }
      }
    }
  }

  /** Utility method for calculating the ending minutes of an entry
   * given its components.
   *
   * @param theStartHour The starting hour.
   *
   * @param theStartMinutes The starting minutes.
   *
   * @param theDuration The duration.
   *
   * @return The ending minutes (total) of this period.
   */
  private static int getEndingMinutes(final int theStartHour,
      final int theStartMinutes, final int theDuration) {
    return theStartHour * TimePeriod.HOUR_DURATION
        + theStartMinutes + theDuration;
  }
}
