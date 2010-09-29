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
public class NamesContain<T extends List<User>>
  extends TypeSafeMatcher<List<User>> {

  /** True to check if the list is in ascending order.
   */ 
  private String name;

  /** Constructor.
   *
   * @param isAscending true to check if the list is in ascending order.
   */
  public NamesContain(final String theName) {
    name = theName;
  }

  /** {@inheritDoc}.
   */
  @Override
  public boolean matchesSafely(List<User> list) {
    for (User current: list) {
      if (!current.getName().contains(name)) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc}.
   */
  public void describeTo(Description description) {
    description.appendText("all user names contain " + name);
  }

  /** Creates an instance of this matcher to verify thit a list is in
   * descending order.
   */
  @Factory
  public static <T extends List<User>> Matcher<List<User>>
      namesContain(final String name) {
    return new NamesContain<T>(name);
  }
}

