/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.time.application;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

import org.springframework.validation.Errors;

import com.globant.katari.sample.integration.SecurityUtils;

import com.globant.katari.core.application.ValidatableCommand;

import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimeRepository;

/** Command to show the time entry of a user by id.
 *
 * @author nicolas.frontini
 */
public class ViewTimeEntriesCommand implements
    ValidatableCommand<List<TimeEntry>> {

  /** The time repository.
   */
  private TimeRepository timeRepository;

  /** The date.
   */
  private Date date = new Date();

  /** The contructor with the time repository.
   *
   * @param theTimeRepository The time repository. It cannot be null.
   */
  public ViewTimeEntriesCommand(final TimeRepository theTimeRepository) {
    Validate.notNull(theTimeRepository, "The time repository cannot be null");
    timeRepository = theTimeRepository;
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

  /** Exectue the command and returns a list of time entries.
   *
   * @return Returns a list of time entries.
   */
  public List<TimeEntry> execute() {
    List<TimeEntry> timeEntryList = timeRepository.getTimeEntries(
        SecurityUtils.getCurrentUser(), date);
    return timeEntryList;
  }

  /** Validates this command.
   *
   * This implementation does nothing.
   *
   * @param errors Contextual state about the validation process. It can not be
   * null.
   */
  public void validate(final Errors errors) {
  }
}

