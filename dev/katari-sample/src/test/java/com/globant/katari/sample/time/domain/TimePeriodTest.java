package com.globant.katari.sample.time.domain;

import junit.framework.TestCase;

/** This class represents a TestCase of the time period.
 *
 * @author nicolas.frontini
 */
public class TimePeriodTest extends TestCase {

  /** Test the creation of a time period.
   */
  public void testCreateTimePeriod() {
    TimePeriod period = new TimePeriod(9, 00, 480);
    assertEquals(9, period.getStartHour());
    assertEquals(0, period.getStartMinutes());
    assertEquals(480, period.getDuration());
    assertEquals(17, period.getFinishHour());
    assertEquals(0, period.getFinishMinutes());

    assertEquals("Time period, from 9:00 to 17:00", period.toString());
  }

  /** Test time period creation from a String.
   */
  public void testCreateTimePeriodFromString() {
    TimePeriod period = new TimePeriod("9:00", 480);
    assertEquals(9, period.getStartHour());
    assertEquals(0, period.getStartMinutes());
    assertEquals(480, period.getDuration());
    assertEquals(17, period.getFinishHour());
    assertEquals(0, period.getFinishMinutes());

    TimePeriod otherPeriod = new TimePeriod("9:16", 480);
    assertEquals(9, otherPeriod.getStartHour());
    assertEquals(16, otherPeriod.getStartMinutes());
    assertEquals(480, otherPeriod.getDuration());
    assertEquals(17, otherPeriod.getFinishHour());
    assertEquals(16, otherPeriod.getFinishMinutes());

    TimePeriod yetAnotherPeriod = new TimePeriod("9:59", 2);
    assertEquals(9, yetAnotherPeriod.getStartHour());
    assertEquals(59, yetAnotherPeriod.getStartMinutes());
    assertEquals(2, yetAnotherPeriod.getDuration());
    assertEquals(10, yetAnotherPeriod.getFinishHour());
    assertEquals(01, yetAnotherPeriod.getFinishMinutes());
  }

  /** Test time period creation with worngs houers.
   */
  public void testCreateTimePeriodWrongHour() {
    try {
      TimePeriod period = new TimePeriod(24, 00, 480);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertTrue(e.getMessage().indexOf("should be between 0 and 23") != -1);
    }
    try {
      TimePeriod period = new TimePeriod("24:01", 480);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertTrue(e.getMessage().indexOf("should be between 0 and 23") != -1);
    }
  }

  /** Test time period creation with worngs minutes.
   */
  public void testCreateTimePeriodWrongMinutes() {
    try {
      TimePeriod period = new TimePeriod(9, 61, 480);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertTrue(e.getMessage().indexOf("should be between 0 and 59") != -1);
    }
    try {
      TimePeriod period = new TimePeriod("8:70", 480);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertTrue(e.getMessage().indexOf("should be between 0 and 59") != -1);
    }
  }

  /** Test time period creation with worngs duration.
   */
  public void testCreateTimePeriodWrongDuration() {
    try {
      TimePeriod period = new TimePeriod(9, 59, 0);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("Duration should be greater than 0", e.getMessage());
    }
    try {
      TimePeriod period = new TimePeriod("23:59", 3);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("The period expands to the following day", e.getMessage());
    }
  }

  /** Test time period creation with worngs format.
   */
  public void testCreateTimePeriodWrongFormat() {
    try {
      TimePeriod period = new TimePeriod("00-00", 480);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("The starting time is not in hh:mm format", e.getMessage());
    }
    try {
      TimePeriod period = new TimePeriod("", 480);
      fail();
      period.toString();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("The starting time cannot be empty", e.getMessage());
    }
  }
}
