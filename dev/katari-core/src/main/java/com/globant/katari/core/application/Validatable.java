package com.globant.katari.core.application;

import org.springframework.validation.Errors;

/** Validatable interface for objects that can be validated.
 */
public interface Validatable {

  /** The validate method.
   *
   * @param errors The errors. It cannot be null.
   */
  void validate(Errors errors);
}

