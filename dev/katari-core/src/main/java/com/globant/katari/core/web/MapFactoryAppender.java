/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/** Bean post processor that adds additional elements to a target map.
 *
 * It is used to add additional elements to an existing map. It is mainly used
 * to let different modules add keys to a map defined in another module.
 *
 * If the original map already contains a key, the existing value is replaced
 * by the new one.
 */
public class MapFactoryAppender implements BeanFactoryPostProcessor {

  /** The id of the target list to add the elements, never null. */
  private final String targetMap;

  /** The list of elements to add to the target list, never null. */
  private final Map<?, ?> elements;

  /** Map Factory appender constructor.
   *
   * @param theTargetMap the bean name of the target map to add the elements,
   *  it cannot be null.
   *
   * @param theElements the map of elements to add to the target map,
   *  it cannot be null.
   */
  public MapFactoryAppender(final String theTargetMap,
      final Map<?, ?> theElements) {
    Validate.notNull(theElements, "the elements cannnot be null.");
    Validate.notNull(theTargetMap, "the target map cannnot be null.");
    targetMap = theTargetMap;
    elements = theElements;
  }

  /** Adds the elements to the target list definition.
   *
   * {@inheritDoc}
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void postProcessBeanFactory(
      final ConfigurableListableBeanFactory beanFactory) {
    if (!beanFactory.containsBeanDefinition(targetMap)) {
      return;
    }
    BeanDefinition tagetBeanDefinition;
    tagetBeanDefinition = beanFactory.getBeanDefinition(targetMap);
    MutablePropertyValues propertyValues;
    propertyValues = tagetBeanDefinition.getPropertyValues();
    PropertyValue propertyValue;
    propertyValue = propertyValues.getPropertyValue("sourceMap");
    Validate.notNull(propertyValue, "The target map is not a MapFactoryBean");
    Map targetListDefinition = (Map) propertyValue.getValue();
    targetListDefinition.putAll(elements);
  }
}
