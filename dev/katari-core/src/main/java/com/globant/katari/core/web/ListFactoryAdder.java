/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.List;

/** This class is now deprecated, use ListFactoryAppender instead.
 */
@Deprecated
public class ListFactoryAdder extends ListFactoryAppender {

  /** {@inheritDoc}
   */
  public ListFactoryAdder(final String target, final List<?> elements) {
    super(target, elements);
  }

  /** {@inheritDoc}
   */
  public ListFactoryAdder(final String target, final boolean optional,
      final List<?> elements) {
    super(target, optional, elements);
  }
}

