/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.application;

/** Interface for objects that can be initialized.
 *
 * This interface must be implemnted by commands that need to perform some
 * initialization after the client calls the command setters.
 *
 * Some spring mvc controlles make use of this interface, calling init after
 * binding the request parameters.
 */
public interface Initializable {

  /** The initialize method.
   */
  void init();
}

