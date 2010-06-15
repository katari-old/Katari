/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.trails;

import org.apache.commons.lang.Validate;

import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.annotation.AbstractAnnotationHandler;
import org.trails.descriptor.annotation.DescriptorAnnotationHandler;

/**
 * Creates a new {@link ViewGroupDivisionDescriptorExtension} with the value
 * from the {@link ViewGroupDivision} annotation.
 *
 * @author juan.pereyra
 */
public class ViewGroupDivisionAnnotationHandler extends
    AbstractAnnotationHandler implements
    DescriptorAnnotationHandler<ViewGroupDivision, IPropertyDescriptor> {

  /** Creates a {@link ViewGroupDivisionDescriptorExtension} and adds it to the
   * property descriptor.
   *
   * @param annotation Annotation added to the property. It cannot be null.
   * @param descriptor The property descriptor. It cannot be null.
   *
   * @return Returns descriptor, with the possible values extension.
   */
  public IPropertyDescriptor decorateFromAnnotation(final ViewGroupDivision
      annotation, final IPropertyDescriptor descriptor) {
    Validate.notNull(annotation, "The annotation cannot be null");
    Validate.notNull(descriptor, "The descriptor cannot be null");

    ViewGroupDivisionDescriptorExtension extension
        = new ViewGroupDivisionDescriptorExtension(annotation.cssClass());

    descriptor.addExtension(
        ViewGroupDivisionDescriptorExtension.class.getName(), extension);
    return descriptor;
  }
}

