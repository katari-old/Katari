package com.globant.katari.trails;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.trails.descriptor.annotation.DescriptorAnnotation;

/**
 * Annotation that provides the possibility to create a visual property
 * group in the search form.
 *
 * It basically means that the ul element containing the previous
 * property will be closed and a new one will be opened, containing the
 * annotated property. You also have to define the css class that will be
 * used in the new ul.
 *
 * @author juan.pereyra
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@DescriptorAnnotation(SearchGroupDivisionAnnotationHandler.class)
public @interface SearchGroupDivision {

  /** The css class to be assigned to the newly created ul element.
   */
  String cssClass();
}

