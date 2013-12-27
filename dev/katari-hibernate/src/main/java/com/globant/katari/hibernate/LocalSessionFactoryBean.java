package com.globant.katari.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

/** Hibernate session factory that supports injecting dependencies of entities.
*
* Use this class instead of spring's AnnotationSessionFactoryBean if you want
* to inject dependencies to your entities.
*
* This class is configured with a map of fully qualified class names to spring
* bean names. Whenever hibernate needs to create a new entity, it looks for
* an entry by class name in this map. If it is not in the map, hibernate uses
* the default constructor. Otherwise, we use the bean in the map to
* instantiate the entity. This bean must have one method with signature
* [entity] create();
*
* With this mechanism, you configure your factories in spring injecting the
* dependencies you need.
*
* <p>
* NOTE: This factory perform lot of stuff, so please: <b>README</b>
* Below you will find information about each interface implementation and
* their use:
* </p>
*
* Implementation notes:
*
* <p>
* <ul>
*  <li>
*    InitializingBean: in the afterPropertiesSet we generate the tuplizer
*    factory, and also the hibernate configuration.
*  </li>
*  <li>
*    ApplicationListener: well, we use that spring phase in order to get the
*    moment when spring finish the session factory initialization, and
*    once we are in that moment, we propagate the tuplizer initialization.
*  </li>
*  <li>
*    ApplicationContextAware: we need that because: in order to discover the
*    bean instances related to each tuplizer within the tuplizer factory.
*  </li>
* </ul>
* </p>
*/
public class LocalSessionFactoryBean
  extends org.springframework.orm.hibernate4.LocalSessionFactoryBean
  implements ApplicationListener<ContextRefreshedEvent>,
    ApplicationContextAware {

  /** This map contains for each mapped entity the declared factory bean name.*/
  private Map<String, String> entityFactories = new HashMap<String, String>();

  /** The entity tuplizer factory.
   * This is initialized on the afterPropertiesSet spring callback.
   */
  private EntityTuplizerFactory entityTuplizerFactory;

  /** The spring application context, used by the tuplizer factory in order to
   * finish the Tuplizer initialization.
   */
  private ApplicationContext context;

  /** {@inheritDoc}.
   * Note: Here we override the entityTuplizerFactory field within the given
   * configuration instance.
   */
  @Override
  protected SessionFactory buildSessionFactory(
      final LocalSessionFactoryBuilder sfb) {
    entityTuplizerFactory = new EntityTuplizerFactory(entityFactories);

    DirectFieldAccessor reflection;
    reflection = new DirectFieldAccessor(sfb);
    reflection.setPropertyValue("entityTuplizerFactory", entityTuplizerFactory);

    return sfb.buildSessionFactory();
  }

  /** Sets the entity factories to this instance.
   * @param factories the entityFactories to set, cannot be null.
   */
  public void setEntityFactories(final Map<String, String> factories) {
    Validate.notNull(factories, "The factories cannot be null");
    entityFactories = factories;
  }

  /** {@inheritDoc} (from ApplicationListener).
  *
  * Initialize the tuplizers. We use an application event to guarantee that
  * all beans have already been initialized and wired.
  */
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    if (event.getApplicationContext().equals(context)) {
      // We skip the initialization when the session factory is not in the
      // context being refreshed.
      entityTuplizerFactory.initialize(context);
    }
  }

  /** {@inheritDoc}. */
  public void setApplicationContext(
      final ApplicationContext applicationContext) {
    context = applicationContext;
  }

}