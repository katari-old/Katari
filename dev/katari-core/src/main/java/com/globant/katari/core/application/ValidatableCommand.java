package com.globant.katari.core.application;

import org.springframework.validation.Errors;

/** ValidatableCommand interface for commands that can be validated.
 *
 * @param <T> The return type of the execute method from Command interface.
 *
 * @author nicolas.frontini
 */
public interface ValidatableCommand<T> extends Command<T> {

  /** The validate method.
   *
   * @param errors The errors. It cannot be null.
   */
  void validate(Errors errors);
}

