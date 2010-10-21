package com.globant.katari.gadgetcontainer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/** General misc utilities.
 *
 * It has an utf-8 encoding operation.
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 */
public final class Utils {

  /** The private constructor for utility classes.
   */
  private Utils() {
  }

  /** Encodes the provided string in utf-8.
   *
   * @param valueToEncode the string to encode, it cannot be null.
   *
   * @return The string encoded in utf-8, never null.
   */
  public static String urlEncode(final String valueToEncode) {
    try {
      return URLEncoder.encode(valueToEncode, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
