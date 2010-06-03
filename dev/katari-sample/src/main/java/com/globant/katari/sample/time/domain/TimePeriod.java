package com.globant.katari.sample.time.domain;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/** Class that represents a period of time in the form of start time (hh:mm)
 * and duration (minutes). It's used to improve the readability and consistency
 * of a time entry (instead of using a double for the duration).
 *
 * @author pablo.saavedra
 */
@Embeddable
public class TimePeriod {

  /** The maximum starting hours.
   */
  public static final int MAX_HOURS = 23;

  /** The maximum starting hours.
   */
  public static final int MAX_MINUTES = 59;

  /** Duration of the day, in hours.
   */
  public static final int DAY_DURATION = 24;

  /** Duration of the hour, in minutes.
   */
  public static final int HOUR_DURATION = 60;

  /** The starting hour of the period, from 0 to 23.
   */
  @Basic
  private int startHour;

  /** The starting minutes of the period, from 0 to 59.
   */
  @Basic
  private int startMinutes;

  /** The duration (in minutes) of the period, should not extend to the
   * following day.
   */
  @Basic
  private int duration;

  /** Protected default constructor, for hibernate's sake.
   */
  protected TimePeriod() {

  }

  /** Constructs a TimePeriod instance with the given parameters. Instances
   * shouldn't be constructed directly, use the factory methods provided by the
   * class instead.
   *
   * @param theStartingHour The starting hour for the period, from 0 to 23.
   *
   * @param theStartingMins The starting minutes for the period, from 0 to 59.
   *
   * @param theDuration The duration of the period, should be greater than 0.
   */
  public TimePeriod(final int theStartingHour, final int theStartingMins,
      final int theDuration) {
    validatePeriod(theStartingHour, theStartingMins, theDuration);
    startHour = theStartingHour;
    startMinutes = theStartingMins;
    duration = theDuration;
  }

  /** Constructs a time period with the start time given as a String.
   *
   * @param startingTime The starting time of the period, in hh:mm or h:mm
   * format. Cannot be null.
   *
   * @param theDuration The duration of the period in minutes, greater than 0.
   */
  public TimePeriod(final String startingTime, final int theDuration) {
    Validate.notEmpty(startingTime, "The starting time cannot be empty");
    if (startingTime.indexOf(':') == -1) {
      throw new IllegalArgumentException(
          "The starting time is not in hh:mm format");
    }
    String[] hoursAndMinutes = startingTime.split(":");
    int hours = Integer.parseInt(hoursAndMinutes[0]);
    int minutes = Integer.parseInt(hoursAndMinutes[1]);
    validatePeriod(hours, minutes, theDuration);
    startHour = hours;
    startMinutes = minutes;
    duration = theDuration;
  }

  /** Validation logic for the period elements.
   *
   * @param theStartHour The starting hour.
   *
   * @param theStartMinute The starting minutes.
   *
   * @param theDuration The duration of the period.
   */
  private void validatePeriod(final int theStartHour, final int theStartMinute,
      final int theDuration) {
    if ((theStartHour < 0) || (theStartHour > MAX_HOURS)) {
      throw new IllegalArgumentException("The starting hour " + theStartHour
          + " is invalid, it should be between 0 and 23");
    }
    if ((theStartMinute < 0) || (theStartMinute > MAX_MINUTES)) {
      throw new IllegalArgumentException("The starting minutes "
          + theStartMinute + " are invalid, it should be between 0 and 59");
    }
    if (theDuration <= 0) {
      throw new IllegalArgumentException("Duration should be greater than 0");
    }
    int endingMinutes = getEndingMinutes(
        theStartHour, theStartMinute, theDuration);

    if (endingMinutes > DAY_DURATION * HOUR_DURATION) {
      throw new IllegalArgumentException(
          "The period expands to the following day");
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
    return theStartHour * HOUR_DURATION + theStartMinutes + theDuration;
  }

  /** The starting hour for the period.
   *
   * @return the start hour, from 0 to 23.
   */
  public int getStartHour() {
    return startHour;
  }

  /** The starting minutes for the period.
   *
   * @return the start minutes, from 0 to 59.
   */
  public int getStartMinutes() {
    return startMinutes;
  }

  /** The duration of the period.
   *
   * @return the duration, in minutes.
   */
  public int getDuration() {
    return duration;
  }

  /** Returns the finishing hour of this period, based on the starting hour and
   * the duration.
   *
   * @return The finishing hour.
   */
  public int getFinishHour() {
    int finishMinutes = getEndingMinutes(startHour, startMinutes, duration);
    return finishMinutes / HOUR_DURATION;
  }

  /** Returns the finishing minutes of this period, based on the starting hour
   * and the duration.
   *
   * @return The finishing hour.
   */
  public int getFinishMinutes() {
    int finishMinutes = getEndingMinutes(startHour, startMinutes, duration);
    return finishMinutes % HOUR_DURATION;
  }

  /** Returns a string representation of this time period.
   *
   * @return A human readable string.
   */
  @Override
  public String toString() {
    return "Time period, from " + startHour + ":" + padWithZeros(startMinutes)
        + " to " + padWithZeros(getFinishHour()) + ":"
        + padWithZeros(getFinishMinutes());
  }

  /** Completes an integer with a leading 0 if it's only one digit long.
   *
   * @param value The integer to pad.
   *
   * @return The padded integer.
   */
  private static String padWithZeros(final int value) {
    return StringUtils.leftPad(Integer.toString(value), 2, '0');
  }
}
