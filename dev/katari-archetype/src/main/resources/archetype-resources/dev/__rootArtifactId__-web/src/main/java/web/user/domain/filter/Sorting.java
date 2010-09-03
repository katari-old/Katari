#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.user.domain.filter;

/** Holds the information to specify the order.
 *
 * For an entity to match this filter, the result set must to contain
 * sorting values.
 */
public class Sorting {

  /** The ascending order.
   */
  private boolean ascendingOrder = true;

  /** The name of the order column.
   */
  private String columnName = "";

  /** The ascending order.
   *
   * @return Returns <code>true</code> if the order is ascending,
   *         <code>false</code> if the order is descending.
   */
  public final boolean isAscendingOrder() {
    return ascendingOrder;
  }

  /** Set the ascending order.
   *
   * @param theAscendingOrder The ascending order.
   */
  public final void setAscendingOrder(final boolean theAscendingOrder) {
    ascendingOrder = theAscendingOrder;
  }

  /** Get the column name order.
   *
   * If the <code>String</code> is empty, the order is not specified.
   *
   * @return Retruns the column name order. If the column name is blank
   * ("" or " "), the sorting is not specified.
   */
  public final String getColumnName() {
    return columnName;
  }

  /** Set the column name order.
   *
   * @param theColumnName The column name order. It cannot be null. If the
   * column name is blank ("" or " "), the sorting is not specified.
   */
  public final void setColumnName(final String theColumnName) {
    columnName = theColumnName;
  }
}
