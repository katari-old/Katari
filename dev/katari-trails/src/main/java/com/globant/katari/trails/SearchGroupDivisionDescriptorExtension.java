package com.globant.katari.trails;

import org.apache.commons.lang.Validate;
import org.trails.descriptor.IDescriptorExtension;

/**
 * Extension to the Property Descriptor (following Erich Gamma's pattern) that
 * holds the value for the css class to be assigned to the newly created ul
 * element.
 *
 * @see {@link SearchGroupDivision}
 * @author juan.pereyra
 */
public class SearchGroupDivisionDescriptorExtension implements
    IDescriptorExtension {

  /**
   * The serial id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The CSS class name.
   */
  private String cssClass;

  /**
   * Constructor. It creates a new extension with a CSS class name.
   * @param aCssClass The CSS class name. It can't be null.
   */
  public SearchGroupDivisionDescriptorExtension(final String aCssClass) {
    super();
    Validate.notEmpty(aCssClass);
    cssClass = aCssClass;
  }

  /**
   * CSS Class getter.
   * @return The CSS class.
   */
  public String getCssClass() {
    return cssClass;
  }
}

