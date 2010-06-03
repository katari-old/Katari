#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.globant.${clientName}.${projectName}.web.user.domain.filter;

/** Holds the information to filter by the content of a column.
 *
 * For an entity to match this filter, the column specified must contain
 * at least one occurence of the specified value.
 */
public class ContainsFilter {

  /** The name of the contains column.
   */
  private String columnName = "";

  /** The contains value.
   */
  private String value = "";

  /** Set the contains value.
   *
   * @param theValue The sequence to search for. It cannot be null.
   */
  public final void setValue(final String theValue) {
    value = theValue;
  }

  /** Get the contains value.
   *
   * @return Returns the contains value.
   */
  public final String getValue() {
    return value;
  }

  /** Set the column name.
   *
   * Indicates the column that the contain value will be aplied.
   *
   * @param theColumnName The column name. It cannot be null. If the column
   * name is blank ("" or " "), the filter is not specified.
   */
  public final void setColumnName(final String theColumnName) {
    columnName = theColumnName;
  }

  /** Get the column name.
   *
   * Indicates the column that the contain value will be aplied.
   *
   * @return Returns the column name. If the column name is blank ("" or " "),
   * the filter is not specified.
   */
  public final String getColumnName() {
    return columnName;
  }
}
