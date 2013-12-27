package com.globant.katari.core.web;

import java.util.List;

import org.springframework.beans.factory.BeanFactoryAware;

/** Custom list factory bean that inject the bean factory into those lists
 * that implements the bean factory aware interface.
 */
public class ListFactoryBean
  extends org.springframework.beans.factory.config.ListFactoryBean {

  /** {@inheritDoc}.*/
  protected List<?> createInstance() {
    List<?> outputList = super.createInstance();
    if (outputList instanceof BeanFactoryAware) {
      ((BeanFactoryAware) outputList).setBeanFactory(getBeanFactory());
    }
    return outputList;
  }

}
