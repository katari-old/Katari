package com.globant.katari.core.application;

/** Command interface for classes that can be executed.
 *
 * @param <T> The return type of the execute method.
 *
 * @author nicolas.frontini
 */
public interface Command<T> {

  /** The execute method.
   *
   * @return Returns a parametrized type.
   */
  T execute();
}

