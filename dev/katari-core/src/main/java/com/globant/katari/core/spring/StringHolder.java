/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.FactoryBean;

/** A spring bean that holds a string.
 *
 * This is used to create a string that can be referenced from another bean and
 * can be configured by a post-processor like PropertyOverrideConfigurer.
 */
public class StringHolder implements FactoryBean {

  /** The string value..
   *
   * It is initially the empty string. It is never null.
   */
  private String value = "";

  /** Set string value.
   *
   * @param theValue the string value. It cannot be null.
   */
  public void setValue(final String theValue) {
    Validate.notNull(theValue, "The value cannot be null");
    value = theValue;
  }

  /** Returns the string value, "" if setValue was never called.
   *
   * @return The value, never null.
   */
  public Object getObject() {
    return value;
  }

  /** The object type.
   *
   * @return String.class.
   */
  @SuppressWarnings("unchecked")
  public Class getObjectType() {
    return String.class;
  }

  /** If this is a singleton.
   *
   * @return always true.
   */
  public boolean isSingleton() {
    return true;
  }
}

