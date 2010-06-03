package com.globant.katari.trails;

import org.apache.commons.lang.Validate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Bean provider class that gives access to spring beans.
 *
 * When access to application context beans is needed, an instance of this
 * provider can be injected to the client bean. <br>
 * This class was created to provide to OgnlAnnotationDecorator with a context
 * that includes a SpringBeanProvider, allowing PossibleValues and InitialValue
 * extensions to use expressions like
 * <strong>#spring.bean['persistenceService'].getInstances(..)</strong>, and
 * to make calls to any service bean.<br>
 *
 * @see {@link org.trails.descriptor.annotation.OgnlAnnotationsDecorator}
 * @see {@link com.globant.katari.sample.time.crud.Activity}
 * @see {@link org.trails.descriptor.annotation.PossibleValues}
 * @see {@link org.trails.descriptor.annotation.InitialValue}
 * @author jimena.garbarino
 */
public class SpringBeanProvider implements ApplicationContextAware {

  /**
   * Spring application context object.
   */
  private ApplicationContext context;

  /**
   * Sets the application context.
   * @param theContext
   *          The application context to set, not null
   */
  public void setApplicationContext(final ApplicationContext theContext) {
    Validate.notNull(theContext, "theContext cannot be null.");
    context = theContext;
  }

  /**
   * Returns the spring bean with that beanName.
   * This method throws an exception if the requested bean does not exist in
   * the application context.
   * @param beanName
   *          The name of the bean to look up, not null
   * @return the spring bean, never returns null
   */
  public Object getBean(final String beanName) {
    Validate.notNull(beanName, "beanName cannot be null.");
    return context.getBean(beanName);
  }

  /**
   * Dummy setter, needed to support OGNL bean property access.
   * @param beanName
   *          The name of the bean, ignored in this implementation
   * @param bean
   *          The bean, ignored in this implementation
   */
  public void setBean(final String beanName, final Object bean) {
    // this method should never be called!
    throw new RuntimeException("setBean method should never be called.");
  }
}
