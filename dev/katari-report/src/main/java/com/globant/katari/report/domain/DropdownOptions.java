package com.globant.katari.report.domain;

import org.apache.commons.lang.Validate;

/** Stores the value and corresponding label of an option to display in a drop
 * down.
 *
 * @author sebastian.ovide
 */
public class DropdownOptions {

  /** The value of the option in the drop down. It is never null.
   */
  private String value;

  /** The label of the option shown in the drop down. It is never null.
   */
  private String label;

  /** Constructor.
   *
   * @param theValue The value of the option in the drop down. It cannot be
   * null.
   *
   * @param theLabel The label of the option shown in the drop down.It cannot
   * be null.
   */
  public DropdownOptions(final String theValue, final String theLabel) {
    Validate.notNull(theValue, "The value cannot be null.");
    Validate.notNull(theLabel, "The label cannot be null.");
    value = theValue;
    label = theLabel;
  }

  /** Returns the value of the option.
   *
   * @return the value, never returns null.
   */
  public String getValue() {
    return value;
  }

  /** Returns the label of the option.
   *
   * @return the label, never returns null.
   */
  public String getLabel() {
    return label;
  }
}
