package com.globant.katari.sample.time.application;

import java.util.Date;

import org.apache.commons.lang.Validate;

import com.globant.katari.core.application.Command;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimeRepository;

/** Command to delete a time entry.
 *
 * @author nicolas.frontini
 */
public class DeleteTimeEntryCommand implements Command<Void> {

  /** The time repository.
   */
  private TimeRepository timeRepository;

  /** The id of the time entry.
   */
  private long timeEntryId = 0;

  /** The date of the deleted time entry.
   */
  private Date date = new Date();

  /** The contructor with the time repository.
   *
   * @param theTimeRepository The time repository. It cannot be null.
   */
  public DeleteTimeEntryCommand(final TimeRepository theTimeRepository) {
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

  /** Returns the date of the deleted time entry. The default date is the
   * current date.
   *
   * @return The date of the deleted time entry.
   */
  public Date getDate() {
    return new Date(date.getTime());
  }

  /** Sets the date of the deleted time entry.
   *
   * @param theDate The date. It cannot be null.
   */
  public void setDate(final Date theDate) {
    Validate.notNull(theDate, "The date cannot be null.");
    date = new Date(theDate.getTime());
  }

  /** Remove a time entry with the midified data.
   *
   * @return Always returns null.
   */
  public Void execute() {
    TimeEntry timeEntry = timeRepository.findTimeEntry(timeEntryId);
    date = timeEntry.getEntryDate();
    timeRepository.remove(timeEntry);
    return null;
  }
}
