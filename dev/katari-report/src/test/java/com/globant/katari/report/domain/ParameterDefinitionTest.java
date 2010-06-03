/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.domain;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

/** Tests Report Definition.
 */
public class ParameterDefinitionTest extends TestCase {
  private JasperReportRepository repository;

  @Override
  protected void setUp() throws Exception {
    repository = EasyMock.createMock(JasperReportRepository.class);
    EasyMock.replay(repository);
  }

  public void testConvertValue_string() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.lang.String", false, null);
    assertEquals("String", string.convertValue("String"));
  }

  public void testConvertValue_long() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.lang.Long", false, null);
    assertEquals(1000, ((Long) string.convertValue("1000")).longValue());
  }

  public void testConvertValue_int() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.lang.Integer", false, null);
    assertEquals(1000, string.convertValue("1000"));
  }

  public void testConvertValue_double() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.lang.Double", false, null);
    assertEquals(1000.0, (Double) string.convertValue("1000"), 0.0001);
  }

  public void testConvertValue_float() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.lang.Float", false, null);
    assertEquals(1000.0, (Float) string.convertValue("1000"), 0.0001);
  }

  public void testConvertValue_date() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.util.Date", false, null);
    Calendar date = Calendar.getInstance();
    date.clear();
    date.set(2008, 10, 10);
    assertEquals(date.getTime(), string.convertValue("10/11/2008"));
  }

  public void testConvertValue_time() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.sql.Time", false, null);
    String value = "10:11:12";
    DateFormat df = new SimpleDateFormat("HH:mm:ss");
    Date time = (Date) df.parseObject(value);
    Time timeConverted = (Time) string.convertValue(value);
    assertEquals(new Time(time.getTime()), timeConverted);
  }

  public void testConvertValue_boolean() throws Exception {
    ParameterDefinition string;
    string = new ParameterDefinition("Test", "java.lang.Boolean", false, null);
    assertTrue((Boolean) string.convertValue("true"));
    assertFalse((Boolean) string.convertValue("false"));
  }
}

