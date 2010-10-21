/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.List;

/** This class is now deprecated, use ListFactoryAppender instead.
 */
@Deprecated
public class ListFactoryAdder extends ListFactoryAppender {

  /** Constructor.
   *
   * @param target the bean name of the target list to add the elements, it
   * cannot be null.
   *
   * @param elements the list of elements to add to the target list, it cannot
   * be null.
   */
  public ListFactoryAdder(final String target, final List<?> elements) {
    super(target, elements);
  }

  /** Constructor.
   *
   * @param target the bean name of the target list to add the elements, it
   * cannot be null.
   *
   * @param optional indicates if the target list must exist. If true, nothing
   * happens if the target list does not exist. If false,
   * postProcessBeanFactory throws an exception if the target list does not
   * exist.
   *
   * @param elements the list of elements to add to the target list, it cannot
   * be null.
   */
  public ListFactoryAdder(final String target, final boolean optional,
      final List<?> elements) {
    super(target, optional, elements);
  }
}

