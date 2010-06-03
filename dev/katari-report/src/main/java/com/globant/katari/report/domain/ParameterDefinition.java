/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.domain;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

/** Class that represents a custom parameter defined in the report template.
 *
 * @author sergio.sobek
 */
public class ParameterDefinition {

  /** The parameter name.
   *
   * It is never null.
   */
  private String name;

  /** The parameter type.
   *
   * It is never null.
   */
  private String type;

  /** Describe if the parameter is optional or not. */
  private boolean optional;

  /** The query used to populate the dropdown options.
   * If null, no dropdown options are available, it is null if the parameter is
   * not a dropdown.
   */
  private String dropdownQuery = null;

  /** A set with all the accepted types of parameters.
   *
   * This is used to check if the parameter is created correctly.
   */
  private static final Set<String> ACCEPTED_TYPES = new HashSet<String>();

  static {
    String[] values = {"java.lang.String", "java.lang.Double",
      "java.lang.Integer", "java.lang.Long", "java.lang.Float",
      "java.util.Date", "java.lang.Boolean", "java.sql.Time"};
    ACCEPTED_TYPES.addAll(Arrays.asList(values));
  }

  /** ParameterDefinition constructor.
   *
   * @param theName the name of the parameter. It cannot be null.
   * @param theType the type of the parameter, represented as a fully qualified
   * class name.
   * @param isOptional define if the parameter value is optional.
   * @param theDropdownQuery Query used to retrieve the drop down options. If
   * null, the parameter is not a drop down.
   *
   * The valid accepted values for this parameter are: 'java.lang.String',
   * 'java.lang.Double', 'java.lang.Integer', 'java.lang.Float',
   * 'java.util.Date' and 'java.lang.Boolean'. It cannot be null.
   */
  public ParameterDefinition(final String theName, final String theType,
      final boolean isOptional,
      final String theDropdownQuery) {

    Validate.notNull(theName, "The name of the parameter cannot be null");
    Validate.notNull(theType, "The type of the parameter cannot be null");
    Validate.isTrue(ACCEPTED_TYPES.contains(theType), "The type " + theType
        + " is not supported.");
    name = theName;
    type = theType;
    optional = isOptional;
    dropdownQuery = theDropdownQuery;
  }

  /** Gets the name of the parameter.
   *
   * @return the parameter name. Never returns null.
   */
  public String getName() {
    return name;
  }

  /** Gets the type of parameter, as a fully qualified class name.
   *
   * @return the parameter description type. It never returns null.
   */
  public String getType() {
    return type;
  }

  /** It says if it is a drop down.
   *
   * @return true if the parameters definition describes a dropdown. It is not a
   * dropdown if the query is null.
   */
  public boolean isDropdown() {
    return dropdownQuery != null;
  }

  /** Converts the given String value to the correct type.
   *
   * The valid accepted types are: String, Integer, Long, Float, Double,
   * Boolean and date.
   *
   * @param value the String value to be converted. It cannot be null.
   *
   * @return the value converted to the correct type. If it cannot convert the
   * value it returns null. If the type to be converted to is a Date and there
   * is an error while converting the value, it throws a RuntimeException.
   *
   * @throws ParseException in case a parameter could not be parsed according
   * to the expected type.
   *
   * TODO This method hardcodes the format to mm/MM/yyyy. Provide a way to
   * configure this.
   */
  public Object convertValue(final String value) throws ParseException {
    Validate.notNull(value, "The value cannot be null.");
    Class< ? > classType;
    Object result = null;

    try {
      classType = Class.forName(type);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Error obtaining class", e);
    }

    if (String.class.equals(classType)) {
      result = value;
    } else if (Integer.class.equals(classType)) {
      result = Integer.valueOf(value);
    } else if (Long.class.equals(classType)) {
      result = Long.valueOf(value);
    } else if (Float.class.equals(classType)) {
      result = Float.valueOf(value);
    } else if (Double.class.equals(classType)) {
      result = Double.valueOf(value);
    } else if (Boolean.class.equals(classType)) {
      result = Boolean.valueOf(value);
    } else if (Date.class.equals(classType)) {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      df.setLenient(false);
      result = df.parse(value);
    } else if (Time.class.equals(classType)) {
      DateFormat df = new SimpleDateFormat("HH:mm:ss");
      Date time = (Date) df.parseObject(value);
      result = new Time(time.getTime());
    } else {
      throw new RuntimeException("Type not supported: " + classType);
    }
    return result;
  }

  /**
   * The dropdown query.
   * @return the dropdown query, it is null if the parameter is not dropdown.
   */
  public String getDropdownQuery() {
    return dropdownQuery;
  }

  /** Says if it is optional parameter.
   * @return true if the parameter is optional.
   */
  public boolean isOptional() {
    return optional;
  }
}

