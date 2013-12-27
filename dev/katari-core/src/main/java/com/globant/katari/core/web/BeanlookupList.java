package com.globant.katari.core.web;

import java.util.Iterator;
import java.util.LinkedList;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/** A list that is initialized with bean names, and returns bean instances.
 *
 * This list implementation works together with spring, basically the method
 * "add" expects spring bean names.
 * Once any kind of read method is called, this list will lookup each one of
 * those beans and store it within an internal list.
 *
 * Note: This class should be created within the spring context.
 * Warning: this is not intended as a general list, it does not implement the
 * full list contract, only what is necessary at the moment.
 * See the overriden operations javadoc.
 */
public class BeanlookupList extends LinkedList<Object>
  implements BeanFactoryAware {

  /** The serial version. */
  private static final long serialVersionUID = 1L;

  /** Checks if the list has been initialized, default: false. */
  private volatile boolean initialized = false;

  /** The list of elements, it's never null. */
  private final LinkedList<Object> initializedBeans = new LinkedList<Object>();

  /** The spring bean factory, it's never null.*/
  private BeanFactory beanFactory;

  /** {@inheritDoc}.*/
  @Override
  public Object get(final int index) {
    initialize();
    return initializedBeans.get(index);
  }

  /** {@inheritDoc}.*/
  @Override
  public Object element() {
    initialize();
    return initializedBeans.element();
  }

  /** {@inheritDoc}.*/
  @Override
  public Object getFirst() {
    initialize();
    return initializedBeans.getFirst();
  }

  /** {@inheritDoc}.*/
  @Override
  public Object getLast() {
    initialize();
    return initializedBeans.getLast();
  }

  /** {@inheritDoc}.*/
  @Override
  public Iterator<Object> iterator() {
    initialize();
    return initializedBeans.iterator();
  }

  /** Initializes the collection.*/
  private void initialize() {
    if (!initialized) {
      synchronized (this) {
        Iterator<Object> it = super.iterator();
        while (it.hasNext()) {
          String beanName = (String) it.next();
          initializedBeans.add(beanFactory.getBean(beanName));
        }
      }
      initialized = true;
    }
  }

  /** {@inheritDoc}.*/
  @Override
  public void setBeanFactory(final BeanFactory springBeanFactory) {
    beanFactory = springBeanFactory;
  }

}
