#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.testsupport;

import java.util.List;

/** Help to verify common verifications in the test cases.
 *
 * Define verifiers that several test can be use.
 */
public class VerifyHelper {

  /** Verify the ascending order of the list.
   *
   * @param list A list of <code>String</code>.
   *
   * @return Returns true if the list is ascending order.
   */
  public final boolean ascendingOrder(final List<String> list) {
    return verifyOrder(list, true);
  }

  /** Verify the desscending order of the list.
   *
   * @param list A list of <code>String</code>.
   *
   * @return Returns true if the list is descending order.
   */
  public final boolean descendingOrder(final List<String> list) {
    return verifyOrder(list, false);
  }

  /** Verify the ascending or desscending order of the list.
   *
   * @param list A list of <code>String</code>.
   *
   * @param ascending A <code>boolean</code> that determines ascending
   *        or desceining.
   *
   * @return Returns true if the order is the correct.
   */
  private boolean verifyOrder(final List<String> list,
      final boolean ascending) {
    String previusElement = list.get(0);
    for (String element : list) {
      int comparison = previusElement.compareToIgnoreCase(element);
      if (ascending && comparison > 0) {
          return false;
      } else {
        if (!ascending && comparison < 0) {
          return false;
        }
      }
      previusElement = element;
    }
    return true;
  }

  /** Verify that all elements of the list contains the <code>String</code>
   * value.
   *
   * @param list The list of <code>String</code> elements.
   *
   * @param value The value.
   *
   * @return Returns true if all the elements in the list contains the value,
   * false otherwise.
   */
  public boolean containsAll(final List<String> list, final String value) {
    for (String element : list) {
      if (!element.contains(value)) {
        return false;
      }
    }
    return true;
  }
}
