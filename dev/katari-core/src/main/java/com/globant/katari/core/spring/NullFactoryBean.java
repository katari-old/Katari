/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.springframework.beans.factory.FactoryBean;

/** A spring factory bean that always returns null as the constructed instance.
 *
 * This is useful to define a bean that is optionally intended to be overriden
 * somewhere else.
 */
public class NullFactoryBean implements FactoryBean<Void> {

  /** {@inheritDoc}
   *
   * This always returns null.
   */
  public Void getObject() {
    return null;
  }

  /** {@inheritDoc}
   *
   * This always returns null.
   */
  public Class<? extends Void> getObjectType() {
    return null;
  }

  /** {@inheritDoc}
   *
   * This always returns true.
   */
  public boolean isSingleton() {
    return true;
  }
}

