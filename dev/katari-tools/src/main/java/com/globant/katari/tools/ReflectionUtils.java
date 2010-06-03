/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.lang.reflect.Field;

/** Convenience methods to deal with reflection.
 */
public final class ReflectionUtils {

  /** A private constructor for this utility class.
   */
  private ReflectionUtils() {
  }

  /** Sets a value of a possible inaccesible field.
   *
   * @param object The object to set the attribute to. It cannot be null.
   *
   * @param attributeName The name of the attribute to change. The attribute
   * must exist in the object class. It cannot be null.
   *
   * @param value The new value for the attribute.
   *
   * TODO This does not search for attributes in the object hierarchy, to be
   * implemented in the future.
   */
  public static void setAttribute(final Object object, final String
      attributeName, final Object value) {

    try {
      Field field = object.getClass().getDeclaredField(attributeName);
      field.setAccessible(true);
      field.set(object, value);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error setting field " + attributeName, e);
    }
  }

  /** Obtains the value of a possible inaccesible field.
   *
   * @param object The object to set the attribute to. It cannot be null.
   *
   * @param attributeName The name of the attribute to change. The attribute
   * must exist in the object class. It cannot be null.
   *
   * @return The value for the attribute.
   */
  public static Object getAttribute(final Object object, final String
      attributeName) {

    try {
      Field field = object.getClass().getDeclaredField(attributeName);
      field.setAccessible(true);
      return field.get(object);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error setting field " + attributeName, e);
    }
  }
}

