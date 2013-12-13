package com.globant.katari.hibernate;

import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.PojoInstantiator;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.PojoEntityTuplizer;

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
public class Tuplizer extends PojoEntityTuplizer
  implements PlatformTuplizer {

  /** The factory of hibernate entities, never null.*/
  private Object entityFactory;

  /** The cached factory method for this tuplizer and this object factory,
   * never null. */
  private Method factoryMethod;

  /** Creates a new instance of this tuplizer.
   * @param model the entity meta model.
   * @param entity the mapped entity.
   */
  public Tuplizer(final EntityMetamodel model, final PersistentClass entity) {
    super(model, entity);
  }

  /** Finish the tuplizer initialization.
   * @param method the method of the factory object to execute, cannot be null.
   * @param factory the factory object to use, cannot be null.
   */
  public void initialize(final Object factory, final Method method) {
    Validate.notNull(method, "The method cannot be null");
    Validate.notNull(factory, "The factory cannot be null");
    factoryMethod = method;
    entityFactory = factory;
  }

  /** Builds a {@link BeanPojoInstantiator} for the given mapped entity.
   *
   * @param clazz The mapping information regarding the mapped entity.
   * @return a {@link BeanPojoInstantiator} instance.
   */
  @Override
  protected Instantiator buildInstantiator(final PersistentClass clazz) {

    PojoInstantiator instantiator;

    /** The Hibernate's Pojo Instantiator.
     * We had to create this object here because 'Hibernate', the funny boy,
     * perform the buildInstantiator within the super constructor, so: it's
     * impossible for us to handle that without reflection, so this is
     * the best "non hacking" approach to tackle that.
     */
    instantiator = new PojoInstantiator(clazz, null) {

      /** The serial Id.*/
      private static final long serialVersionUID = 1L;

      /** Performs the requested instantiation.
       *
       * Looks for a bean named as the mapped class name in the spring context
       * and instantiates it, if no such bean exists
       * {@link PojoInstantiator#instantiate()} method will be called.
       *
       * @return the instantiated mapped class, never null.
       */
     @Override
     public Object instantiate() {
       Validate.notNull(factoryMethod, "The factory method cannot be null");
       Validate.notNull(entityFactory, "The factory object cannot be null");
       try {
         return factoryMethod.invoke(entityFactory);
       } catch (Exception e) {
         throw new RuntimeException(e);
       }
     }
    };

    return instantiator;
  }
}

