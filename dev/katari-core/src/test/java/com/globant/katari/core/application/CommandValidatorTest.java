/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.application;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import junit.framework.TestCase;

/** Tests the command validator.
 *
 * @author nicolas.frontini
 */
public class CommandValidatorTest extends TestCase {

  /** The command validator.
   */
  private CommandValidator commandValidator;

  /** The sample validatable command.
   */
  private SampleValidatableCommand sampleValidatableCommand;

  /** The sample command.
   */
  private SampleCommand sampleCommand;

  /** The sample validatable.
   */
  private SampleValidatable sampleValidatable;

  /** Initialize the commands.
   */
  public void setUp() {
    commandValidator = new CommandValidator();
    sampleCommand = new SampleCommand();
    sampleValidatable = new SampleValidatable();
    sampleValidatableCommand = new SampleValidatableCommand();
  }

  /** Test the supports method.
   */
  public final void testSupports() {
    assertFalse(commandValidator.supports(sampleCommand.getClass()));
    assertTrue(commandValidator.supports(sampleValidatableCommand.getClass()));
    assertFalse(commandValidator.supports(this.getClass()));
    assertTrue(commandValidator.supports(sampleValidatable.getClass()));
  }

  /** Test the validate method.
   */
  public final void testValidate() {

    Errors errors;

    // Validate a command.  Validate with errors.
    errors = new BindException(sampleValidatableCommand,
        "sampleValidatableCommand");
    commandValidator.validate(sampleValidatableCommand, errors);
    assertEquals(1, errors.getErrorCount());

    // Validate without errors.
    errors = new BindException(
        sampleValidatableCommand, "sampleValidatableCommand");
    sampleValidatableCommand.setField("Not empty field");
    commandValidator.validate(sampleValidatableCommand, errors);
    assertEquals(0, errors.getErrorCount());

    // Validate a validatable. Validate with errors.
    errors = new BindException(sampleValidatable, "sampleValidatable");
    commandValidator.validate(sampleValidatable, errors);
    assertEquals(1, errors.getErrorCount());

    // Validate without errors.
    errors = new BindException(sampleValidatable, "sampleValidatable");
    sampleValidatable.setField("Not empty field");
    commandValidator.validate(sampleValidatable, errors);
    assertEquals(0, errors.getErrorCount());
  }

  /** A sample command.
   */
  public class SampleCommand implements Command<Void> {

    /** Execute the command.
     *
     * @return It returns nothing.
     */
    public Void execute() {
      return null;
    }
  }

  /** A sample validatable command.
   */
  public class SampleValidatableCommand implements ValidatableCommand<Void> {

    /** A field to validate.
     */
    private String field;

    /** Gets the field.
     *
     * @return Returns the field.
     */
    public String getField() {
      return field;
    }

    /** Sets the field.
     *
     * @param theField the field
     */
    public void setField(final String theField) {
      field = theField;
    }

    /** Execute the command.
     *
     * @return It returns nothing.
     */
    public Void execute() {
      return null;
    }

    /** Validate the command.
     *
     * @param errors The errors.
     */
    public void validate(final Errors errors) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "field", "required");
    }
  }

  public class SampleValidatable implements Validatable {

    /** A field to validate.
     */
    private String field;

    /** Gets the field.
     *
     * @return Returns the field.
     */
    public String getField() {
      return field;
    }

    /** Sets the field.
     *
     * @param theField the field
     */
    public void setField(final String theField) {
      field = theField;
    }

    /** Validate the object.
     *
     * @param errors The errors.
     */
    public void validate(final Errors errors) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "field", "required");
    }
  }
}

