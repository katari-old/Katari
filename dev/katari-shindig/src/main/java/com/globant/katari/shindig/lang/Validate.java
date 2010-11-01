/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.lang;

import static javax.servlet.http.HttpServletResponse.*;

import java.util.Collection;

import  org.apache.shindig.protocol.ProtocolException;

/** Replacement for apache commons lang validate, but instead of throwing
 * IllegalArgumentException this method throws ProtocolException, which as a
 * checked exception.
 */
public final class Validate {

  /** We assume that the validation errors correspond to a 'Receiving an
   * unsupported, nonstandard parameter', so we return a 400 BAD REQUEST http
   * statuc code.
   */
  private static final int DEFAULT_CODE = SC_BAD_REQUEST;

  /** A private constructor for a utility class.
   */
  private Validate() {
  }

  /**
   * Validate that the specified argument is not null; otherwise throwing an
   * exception with the specified message.
   *
   * Validate.notNull(myObject, "The object must not be null");
   *
   * @param object the object to check.
   *
   * @param message the exception message if invalid. It cannot be null.
   */
  public static void notNull(final Object object, final String message) {
    if (object == null) {
      throw new ProtocolException(DEFAULT_CODE, message);
    }
  }

  /** Validate that the argument condition is true; otherwise throwing an
   * exception with the specified message.
   *
   * This method is useful when validating according to an arbitrary boolean
   * expression, such as validating a primitive number or using your own custom
   * validation expression.
   *
   * Validate.isTrue(myObject.isOk(), "The object is not OK");
   *
   * @param expression the boolean expression to check.
   *
   * @param message the exception message if invalid. It cannot be null.
   */
  public static void isTrue(final boolean expression,
      final String message) {
    if (!expression) {
      throw new ProtocolException(DEFAULT_CODE, message);
    }
  }

  /** Validate that the specified argument collection is neither null nor a
   * size of zero (no elements); otherwise throwing an exception with the
   * specified message.
   *
   * Validate.notEmpty(myCollection, "The collection must not be empty");
   *
   * @param collection the collection to check.
   *
   * @param message the exception message if invalid. It cannot be null.
   */
  public static void notEmpty(final Collection<?> collection,
      final String message) {
    if (collection == null || collection.size() == 0) {
      throw new ProtocolException(DEFAULT_CODE, message);
    }
  }

  /** Validate that the specified argument string is neither null nor a length
   * of zero (no characters); otherwise throwing an exception with the
   * specified message.
   *
   * Validate.notEmpty(myString, "The string must not be empty");
   *
   * @param string the string to check.
   *
   * @param message the exception message if invalid. It cannot be null.
   */
  public static void notEmpty(final String string,
      final String message) {
    if (string == null || string.length() == 0) {
      throw new ProtocolException(DEFAULT_CODE, message);
    }
  }
}

