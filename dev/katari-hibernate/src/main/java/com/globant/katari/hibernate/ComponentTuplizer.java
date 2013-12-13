package com.globant.katari.hibernate;

import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;
import org.hibernate.property.Getter;

/** A Tuplizer defines the contract for things which know how to manage a
 * particular representation of a piece of data.
 *
 * This particular implementation works with an object factory that should hold
 * a method that ensures the proper creation for any kind of entity.
 *
 * The method and the object factory SHOULD be provided within the object
 * creation. This tuplizer does not perform any kind of method lookup in order
 * to find the proper method to execute.
 */
public class ComponentTuplizer implements PlatformTuplizer,
  org.hibernate.tuple.component.ComponentTuplizer {

  /** The serial version.*/
  private static final long serialVersionUID = 1L;

  /** Object delegatee, it's never null.*/
  private org.hibernate.tuple.component.ComponentTuplizer delegatee;

  /** The factory of hibernate entities, never null.*/
  private Object entityFactory;

  /** The cached factory method for this tuplizer and this object factory,
   * never null. */
  private Method factoryMethod;

  /** Creates a new instance of the ComponentTuplizer.
   * @param tuplizer the component tuplizer, cannot be null.
   */
  public ComponentTuplizer(
      final org.hibernate.tuple.component.ComponentTuplizer tuplizer) {
    Validate.notNull(tuplizer, "The tuplizer cannot be null");
    delegatee = tuplizer;
  }

  // Methods that we really care.

  /** {@inheritDoc}. */
  public void initialize(final Object factory, final Method method) {
    Validate.notNull(method, "The method cannot be null");
    Validate.notNull(factory, "The factory cannot be null");
    factoryMethod = method;
    entityFactory = factory;
  }

  /** {@inheritDoc}. */
  public boolean isInstance(final Object object) {
    return delegatee.isInstance(object);
  }

  /** {@inheritDoc}. */
  public Object instantiate() {
    Validate.notNull(factoryMethod, "The factory method cannot be null");
    Validate.notNull(entityFactory, "The factory object cannot be null");
    try {
      return factoryMethod.invoke(entityFactory);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  //End Methods that we really care.

  /** {@inheritDoc}. */
  public Object[] getPropertyValues(final Object entity) {
    return delegatee.getPropertyValues(entity);
  }

  /** {@inheritDoc}. */
  public Object getPropertyValue(final Object entity, final int i) {
    return delegatee.getPropertyValue(entity, i);
  }

  /** {@inheritDoc}. */
  @SuppressWarnings("rawtypes")
  public Class getMappedClass() {
    return delegatee.getMappedClass();
  }

  /** {@inheritDoc}. */
  public Getter getGetter(final int i) {
    return delegatee.getGetter(i);
  }

  /** {@inheritDoc}. */
  public void setPropertyValues(final Object component,
      final Object[] values) {
    delegatee.setPropertyValues(component, values);
  }

  /** {@inheritDoc}. */
  public Object getParent(final Object component) {
    return delegatee.getParent(component);
  }

  /** {@inheritDoc}. */
  public boolean hasParentProperty() {
    return delegatee.hasParentProperty();
  }

  /** {@inheritDoc}. */
  public boolean isMethodOf(final Method method) {
    return delegatee.isMethodOf(method);
  }

  /** {@inheritDoc}. */
  public void setParent(final Object component, final Object parent,
      final org.hibernate.engine.spi.SessionFactoryImplementor factory) {
    delegatee.setParent(component, parent, factory);
  }
}
