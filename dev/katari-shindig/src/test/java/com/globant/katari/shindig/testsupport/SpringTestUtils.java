/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.testsupport;

import com.globant.katari.tools.SpringTestUtilsBase;

/** Utility class to give support to test cases.
 */
public final class SpringTestUtils extends SpringTestUtilsBase {

  /** The static instance for the singleton.*/
  private static SpringTestUtils instance;

  /** A private constructor so no instances are created.*/
  private SpringTestUtils() {
    super(new String[] {
        "classpath:/com/globant/katari/shindig/applicationContext.xml"
    }, null);
  }

  /** Retrieves the intance.
   * @return the instance, never null.
   */
  public static synchronized SpringTestUtils get() {
    if (instance == null) {
      instance = new SpringTestUtils();
    }
    return instance;
  }

}

