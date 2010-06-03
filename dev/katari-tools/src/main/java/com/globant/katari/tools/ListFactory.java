package com.globant.katari.tools;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * Easy to use List factory for test cases support.
 * This list factory simplify the list building on test cases allowing create
 * and fill a list inline.
 * <pre>
 * Code comparison:
 * {@code
 * List&lt;Object&gt; list = new LinkedList&lt;Object&gt;();
 * list.add(object1);
 * list.add(object2);
 * list.add(object3);
 *        Vs.
 * List&lt;Object&gt; list = ListFactory.create(object1, object2, object3);
 * }
 * </pre>
 * @author gerardo.bercovich
 */
public final class ListFactory {

  /**
   * Private constructor.
   */
  private ListFactory() {
  }

  /**
   * Creates a new list with the given elements.
   * @param <T> type of the list elements.
   * @param elements the list elements. cannot be null.
   * @return a new List with the given content. It never returns null.
   */
  public static <T> List<T> create(final T... elements) {
    Validate.notNull(elements);
    final LinkedList<T> list = new LinkedList<T>();
    for (T t : elements) {
      list.add(t);
    }
    return list;
  }
}
