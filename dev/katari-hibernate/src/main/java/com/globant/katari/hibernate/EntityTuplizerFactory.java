package com.globant.katari.hibernate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.hibernate.EntityMode;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;

/** Factory that creates Tuplizers for the Hibernate's mapped entities.
 *
 * <p>
 * Example: Let's supposed that we have a hibernate entity mapped, and that
 * class needs a repository to interact.
 * </p>
 *
 * <code>
 * @Entity
 * @Table(name = "example")
 * public class Example {
 *   @Transient
 *   private ExampleRepository exampleRepository;
 *
 *   Example(final ExampleRepository repository) {
 *     exampleRepository = repository;
 *   }
 *  ...
 * }
 * </code>
 *
 * In order to bring hibernate the ability to inject that dependency, we need
 * to provide 2 things:
 *
 * The first one, will be a bean that act as a Factory for that entity with
 * a <b>MAGIC METHOD</b> called <b>create</b>
 *
 * Below, an example of that factory.
 *
 * <code>
 *
 * public class ExampleBeanFactory {
 *
 *   private final ExampleRepository exampleRepository;
 *
 *   public ExampleBeanFactory(final ExampleRepository repository) {
 *     exampleRepository = repository;
 *   }
 *
 *  public Example create() {
 *    return new Example(exampleRepository);
 *  }
 *
 * }
 * </code>
 *
 * Once we have that bean, we need to map in and provide it to the Hibernate
 * session factory.
 *
 * In katari, there are a bean called 'infrastructure.objectFactoryMap'
 * that you can append you own implementation with the MapFactoryAppender.
 *
 * Example, within your module.xml descriptor you can add factories:
 *
 * the key should be the fully class name, and the value the bean name, NOT
 * the bean 'per se'.
 *
 * <pre>
 *   <bean
 *     class='com.globant.igexpansion.smp.infrastructure.MapFactoryAppender'>
 *     <constructor-arg index='0' value='infrastructure.objectFactoryMap' />
 *     <constructor-arg index='1'>
 *       <util:map>
 *         <entry key='Example' value='exampleBeanFactory' />
 *       ...
 * </pre>
 */
@SuppressWarnings("serial")
public class EntityTuplizerFactory
    extends org.hibernate.tuple.entity.EntityTuplizerFactory {

  /** The class logger.*/
  private final Logger log = LoggerFactory.getLogger(
      EntityTuplizerFactory.class);

  /** This map holds the full class name as a key and the method
   * from the factory as value.
   * Each factory method implementation should have a method called "create"
   * with an empty constructor, and of course should return a new instance
   * of the same type of object defined within the key of this map.
   */
  private final Map<String, String> factories;

  /** This map holds the Tuplizer for each entity.*/
  private final Map<String, PlatformTuplizer> tuplizers;

  /** Creates a new instance of the tuplizer factory.
   *
   * @param factoryMap the map with the tuplizers factories,
   * cannot be null.
   */
  public EntityTuplizerFactory(final Map<String, String> factoryMap) {
    Validate.notNull(factoryMap, "The factory map cannot be null");
    factories = factoryMap;
    tuplizers = new HashMap<String, PlatformTuplizer>();
  }

  /** Construct an instance of the default tuplizer for the given entity-mode.
   *
   * For {@link EntityMode#POJO} mode {@link EntityTuplizer} will
   * be used.
   *
   * @param mode The entity mode for which to build a default tuplizer, cannot
   * be null.
   * @param metamodel The entity metadata, cannot be null.
   * @param clazz The entity mapping info, cannot be null.
   * @return The instantiated Tuplizer, never null.
   */
  public EntityTuplizer constructDefaultTuplizer(final EntityMode mode,
      final EntityMetamodel metamodel, final PersistentClass clazz) {

    registerComponentTuplizers(metamodel.getPropertyTypes());

    String entityName = clazz.getEntityName();
    Object currentFactory = factories.get(entityName);
    if (EntityMode.POJO == mode && currentFactory != null) {
      try {
        PlatformTuplizer tuplizer = new Tuplizer(metamodel, clazz);
        tuplizers.put(entityName, tuplizer);
        return (EntityTuplizer) tuplizer;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return super.constructDefaultTuplizer(mode, metamodel, clazz);
  }

  /** Creates the Tuplizers for the components within an entity.
   * @param types types of the entity.
   */
  private void registerComponentTuplizers(final Type[] types) {
    for (Type type : types) {
      if (type instanceof ComponentType) {
        ComponentType currentType = (ComponentType) type;
        if (currentType.getSubtypes().length > 0) {
          /* If we have a bidirectional relationship this method could
           * raise an stack overflow, so, we need to add a validation, for
           * now, we do not need to do it.
           */
          registerComponentTuplizers(currentType.getSubtypes());
        }
        String componentName = currentType.getReturnedClass().getName();
        Object theFactory = factories.get(componentName);
        if (theFactory != null) {
         ComponentTuplizer componentTuplizer;
         org.hibernate.tuple.component.ComponentTuplizer currentTuplizer;
         currentTuplizer = ((ComponentType)type).getComponentTuplizer();
         componentTuplizer = new ComponentTuplizer(currentTuplizer);
         DirectFieldAccessor reflection = new DirectFieldAccessor(type);
         reflection.setPropertyValue("componentTuplizer", componentTuplizer);

         tuplizers.put(componentName, componentTuplizer);

        }
      }
    }
  }

  /** Performs the initialization for each tuplizer factory.
   * @param applicationContext the spring application context, cannot be null.
   */
  protected void initialize(final ApplicationContext applicationContext) {
    Validate.notNull(applicationContext,
        "The application context cannot be null");

    Set<Map.Entry<String, String>> definedTuplizers = factories.entrySet();
    for (Entry<String, String> tuplizerTuple : definedTuplizers) {
      String entityName = tuplizerTuple.getKey();
      String factoryBeanName = tuplizerTuple.getValue();

      Object factory = applicationContext.getBean(factoryBeanName);
      Method method = extractCreateMethod(factory);

      Validate.notNull(method, "Method create NOT FOUND for the given factory:"
              + entityName);

      log.debug("Initlizing the Tuplizer for:" + entityName);
      PlatformTuplizer aTuplizer = tuplizers.get(entityName);

      if (aTuplizer == null) {
        throw new RuntimeException(
            "Tuplizer for entity: " + entityName + " not found!");
      }

      aTuplizer.initialize(factory, method);
    }
  }

  /** Extract the method from the given factory.
   * @param factory the factory.
   * @return the 'create' method.
   */
  private Method extractCreateMethod(final Object factory) {
    Method method;
    try {
      method = factory.getClass().getDeclaredMethod("create");
      method.setAccessible(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return method;
  }
}
