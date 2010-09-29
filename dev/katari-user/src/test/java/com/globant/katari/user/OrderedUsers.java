/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import com.globant.katari.user.domain.User;

/** Hamcrest matcher to verify if a list of users is in descending or ascending
 * order.
 */
public class OrderedUsers<T extends List<User>>
  extends TypeSafeMatcher<List<User>> {

  /** True to check if the list is in ascending order.
   */ 
  boolean ascending = false;

  /** Constructor.
   *
   * @param isAscending true to check if the list is in ascending order.
   */
  public OrderedUsers(final boolean isAscending) {
    ascending = isAscending;
  }

  /** {@inheritDoc}.
   */
  @Override
  public boolean matchesSafely(List<User> list) {
    User previous = list.get(0);
    for (User current: list) {
      int result = previous.getName().compareToIgnoreCase(current.getName());
      if (ascending && result > 0) {
        return false;
      } else {
        if (!ascending && result < 0) {
          return false;
        }
      }
      previous = current;
    }
    return true;
  }

  /** {@inheritDoc}.
   */
  public void describeTo(Description description) {
    description.appendText("not in descendent order");
  }

  /** Creates an instance of this matcher to verify thit a list is in
   * descending order.
   */
  @Factory
  public static <T extends List<User>> Matcher<List<User>>
  inDescendingOrder() {
    return new OrderedUsers<T>(false);
  }

  /** Creates an instance of this matcher to verify that a list is in
   * ascescending order.
   */
  @Factory
  public static <T extends List<User>> Matcher<List<User>>
  inAscendingOrder() {
    return new OrderedUsers<T>(true);
  }
}

