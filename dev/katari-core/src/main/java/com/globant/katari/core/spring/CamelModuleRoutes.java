/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.util.List;

import org.apache.commons.lang.Validate;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.CamelContextFactoryBean;

import org.springframework.beans.factory.config.BeanPostProcessor;

/** Defines routes to add to the camel context.
 * 
 * This class registers routes in the camel context named katari.eventBus. The
 * routes are specified as RouteDefinition elements.
 */
public class CamelModuleRoutes implements BeanPostProcessor {

  /** The definitions of the routes to add to camel.
   *
   * This is never null.
   */
  private List<RouteDefinition> routeDefinitions;

  /** Creates a new event endpoint.
   *
   * @param routes The routes to add to cammel. It cannot be null.
   */
  public CamelModuleRoutes(final List<RouteDefinition> routes) {
    Validate.notNull(routes, "The routes cannot be null.");
    routeDefinitions = routes;
  }

  /** {@inheritDoc}
   *
   * This implementation does nothing.
   */
  public Object postProcessAfterInitialization(final Object bean,
      final String beanName) {
    return bean;
  }

  /** {@inheritDoc}
   *
   * Attaches the routes to the camel context named katari.eventBus.
   */
  public Object postProcessBeforeInitialization(final Object bean,
      final String beanName) {
    if (beanName.equals("katari.eventBus")) {
      // Adds it to the context.
      CamelContextFactoryBean contextFactory = (CamelContextFactoryBean) bean;
      contextFactory.getRoutes().addAll(routeDefinitions);
      return contextFactory;
    } else {
      return bean;
    }
  }
}

