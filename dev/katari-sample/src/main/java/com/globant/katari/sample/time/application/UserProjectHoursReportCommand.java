package com.globant.katari.sample.time.application;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.globant.katari.core.application.Command;

/**
 * This command gets a list of hours reported to a project between two dates.
 *
 * @author roman.cunci
 */
public class UserProjectHoursReportCommand
  implements Command<List<UserProjectHoursReportDTO>> {

  /** fromDate. Can be null. */
  private String fromDate = null;

  /** toDate. Can be null. */
  private String toDate = null;

  /** format. Cannot be null. */
  private String format = "pdf";

  /** timeReportService. Cannot be null. */
  private TimeReportService timeReportService;

  /**
   * Constructor.
   * @param theTimeReportService the time report service. Cannot be null.
   */
  public UserProjectHoursReportCommand(
      final TimeReportService theTimeReportService) {
    super();

    Validate.notNull(theTimeReportService, "The time report service cannot"
        + " be null");
    timeReportService = theTimeReportService;
  }

  /**
   * Parses the dates and executes the query for the report through
   * the repository. Before calling the repository, the command parses
   * the start and end date selected by the user.
   * Precondition: fromDate and toDate cannot be null.
   * @return Returns a parametrized type.
   */
  public List<UserProjectHoursReportDTO> execute() {
    Validate.notNull(fromDate, "The starting date cannot be null");
    Validate.notNull(toDate, "The ending date cannot be null");

    Calendar tmpConverter = Calendar.getInstance();
    String [] date = fromDate.split("/");
    final int lastHour = 23;
    final int lastMin = 59;
    final int lastSec = 59;

    tmpConverter.set(
        Integer.parseInt(date[2]),
        Integer.parseInt(date[0]),
        Integer.parseInt(date[1]),
        0, 0, 0);
    Date from = tmpConverter.getTime();

    date = toDate.split("/");
    tmpConverter.set(
        Integer.parseInt(date[2]),
        Integer.parseInt(date[0]),
        Integer.parseInt(date[1]),
        lastHour, lastMin, lastSec);
    Date to = tmpConverter.getTime();

    return timeReportService.getUserProjectHours(from, to);
  }

  /** Returns the start date. Can be null.
   * @return start date
   */
  public String getFromDate() {
    return fromDate;
  }

  /** Sets the start date. Can set null.
   * @param theFromDate start date
   */
  public void setFromDate(final String theFromDate) {
    fromDate = theFromDate;
  }

  /** Returns the end date. Can be null.
   * @return end date
   */
  public String getToDate() {
    return toDate;
  }

  /** Sets the end date. Can set null.
   * @param theToDate end date
   */
  public void setToDate(final String theToDate) {
    toDate = theToDate;
  }

  /** Returns the report format. Cannot be null.
   * @return report format
   */
  public String getFormat() {
    return format;
  }

  /** Sets the report format. Cannot set null.
   * @param theFormat report output format
   */
  public void setFormat(final String theFormat) {
    Validate.notNull(theFormat);
    format = theFormat;
  }

}
