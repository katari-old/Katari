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
 * routes are specified as routeContext elements.
 */
public class CamelModuleRoutes implements BeanPostProcessor {

  List<RouteDefinition> routeDefinitions;

  /** Creates a new event endpoint.
   *
   * @param theAggregationStrategy aggregates the response of each listener.
   * It cannot be null.
   *
   * @param theNullUri an endpoint where the event goes when there are no
   * listeners. This is necessary when the output and the input of the event
   * are of different types. It cannot be null.
   *
   * @param sourceUri the source endpoint name. It cannot be null.
   *
   * @param listeners the list of listeners of the events raised in the
   * provided source endpoint.
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

