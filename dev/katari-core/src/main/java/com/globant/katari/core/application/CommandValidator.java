/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.application;

import org.apache.commons.lang.Validate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/** A validator for commands thats implements ValidatableCommand interface, or
 * objects that implements Validatable.
 *
 * ValidatableCommand will soon be deprecated.
 *
 * @author nicolas.frontini
 */
public class CommandValidator implements Validator {

  /** Determines if the Validator can validate the supplied class.
   *
   * It returns <code>true</code> if theClass is an instance of
   * ValidatableCommand; otherwise it returns <code>false</code>.
   *
   * @param theClass The Class that this Validator is being asked if it can
   * validate. It cannot be null.
   *
   * @return <code>true</code> if the Validator can validate the supplied
   * class, <code>false</code> otherwise.
   */
  @SuppressWarnings("unchecked")
  public boolean supports(final Class theClass) {
    Validate.notNull(theClass, "The class cannotk be null");
    boolean supports = ValidatableCommand.class.isAssignableFrom(theClass)
      || Validatable.class.isAssignableFrom(theClass);
    return supports;
  }

  /** Validates the supplied <code>target</code> object, which must be of type
   * ValidatableCommand.
   *
   * @param target The object that is to be validated. It cannot be null.
   *
   * @param errors Contextual state about the validation process. It can not
   * be null.
   */
  public void validate(final Object target, final Errors errors) {
    Validate.notNull(target, "The target object cannot be null");
    Validate.notNull(errors, "The errors cannot be null");

    if (target instanceof ValidatableCommand<?>) {
      ValidatableCommand<?> command = (ValidatableCommand<?>) target;
      command.validate(errors);
    } else if (target instanceof Validatable) {
      Validatable validatable = (Validatable) target;
      validatable.validate(errors);
    }
  }
}

