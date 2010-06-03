/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/** Bean post processor that adds additional elements to a target list.
 *
 * It is used to compose a list (for example, the list of persistent classes)
 * with fragments provided by different modules. It is implemented as a bean
 * factory post processor to make sure that the list is fully created before
 * any other bean. The target list must be a {@link ListFactoryBean} usually
 * defined as &lt;util:list&gt; bean on the spring application context.
 * Multiple {@link ListFactoryAdder} can target to the same list. There is no
 * guarantee on the order in which fragments are added to the target list.<br>
 * Sample use:
 * <pre>
 *    &lt;bean class='com.globant.katari.core.web.ListFactoryAdder'&gt;
 *      &lt;constructor-arg value='targetList'/&gt;
 *      &lt;constructor-arg&gt;
 *        &lt;list&gt;
 *          ....
 *        &lt;/list&gt;
 *      &lt;/constructor-arg&gt;
 *    &lt;/bean&gt;
 * </pre>
 * @author gerardo.bercovich
 */
public class ListFactoryAdder implements BeanFactoryPostProcessor {

  /** The id of the target list to add the elements, it is never null.
   */
  private String targetList;

  /** The list of elements to add to the target list, it is never null. */
  private List<?> elements;

  /** Indicates if the targetList must exist or not.
   *
   * If this parameter is false and the target list does not exist, we throw an
   * exception. otherwise, it is ignored. This is used to optionally modify an
   * existing list. The default is false.
   */
  private boolean isOptional = false;

  /** ListFactoryAdder constructor.
   *
   * @param theTargetList the bean name of the target list to add the elements,
   * it cannot be null.
   *
   * @param theElements the list of elements to add to the target list, it
   * cannot be null.
   */
  public ListFactoryAdder(final String theTargetList,
      final List<?> theElements) {
    Validate.notNull(theElements, "the elements cannnot be null.");
    Validate.notNull(theTargetList, "the target list cannnot be null.");
    targetList = theTargetList;
    elements = theElements;
  }

  /** ListFactoryAdder constructor.
   *
   * @param theTargetList the bean name of the target list to add the elements,
   * it cannot be null.
   *
   * @param optional indicates if the target list must exist. If true, nothing
   * happens if the target list does not exist. If false,
   * postProcessBeanFactory throws an exception if the target list does not
   * exist.
   *
   * @param theElements the list of elements to add to the target list, it
   * cannot be null.
   */
  public ListFactoryAdder(final String theTargetList, final boolean optional,
      final List<?> theElements) {
    Validate.notNull(theElements, "the elements cannnot be null.");
    Validate.notNull(theTargetList, "the target list cannnot be null.");
    targetList = theTargetList;
    isOptional = optional;
    elements = theElements;
  }

  /** Adds the elements to the target list definition.
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void postProcessBeanFactory(
      final ConfigurableListableBeanFactory beanFactory) {
    if (isOptional && !beanFactory.containsBeanDefinition(targetList)) {
      // We ignore the case that the target list does not exist.
      return;
    }
    BeanDefinition tagetBeanDefinition;
    tagetBeanDefinition = beanFactory.getBeanDefinition(targetList);
    MutablePropertyValues propertyValues;
    propertyValues = tagetBeanDefinition.getPropertyValues();
    PropertyValue propertyValue;
    propertyValue = propertyValues.getPropertyValue("sourceList");
    // Warning: we are not explicitly checking that the target list is
    // effectively a ListFactoryBean, only that it has a sourceList
    // property.
    Validate.notNull(propertyValue, "The target list is not a "
        + "ListFactoryBean definition.");
    List targetListDefinition = (List) propertyValue.getValue();
    targetListDefinition.addAll(elements);
  }
}

