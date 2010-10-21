/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class chains the filters provided by each module.
 *
 * Each module provides a regular expression that must match the request path,
 * a filter with its configuration and a priority. All filters are executed in
 * order of ascending priority (lowest numbers go first), for every matching
 * request path.
 *
 * The regular expression is checked against the servlet path and path info.,
 * including the fragment where each module is mapped. For example, if the
 * request is made to http://server:port/module/user/remove/id/1, the string
 * /module/user/remove/id/1 is matched against each regex.
 */
public final class ModuleFilterProxy implements Filter {

  /** The serialization version number.
   *
   * This number must change every time a new serialization incompatible change
   * is introduced in the class.
   */
  private static final long serialVersionUID = 20080226;

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ModuleFilterProxy.class);

  /** A list of module names to module configuration.
   *
   * A module configuration is simply another map that maps a url fragment to a
   * servlet plus its configuration. It is never null.
   */
  private List<FilterMapping> filterMaps = new LinkedList<FilterMapping>();

  /** Builds a ModuleFilterProxy with no registered filters.
   */
  public ModuleFilterProxy() {
  }

  /** Builds a ModuleFilterProxy with an initial list of registered filters.
   *
   * @param initialFilters The list of initial filters. It cannot be null.
   */
  public ModuleFilterProxy(final List<FilterMapping> initialFilters) {
    Validate.notNull(initialFilters, "The list of initial filters cannot be"
        + " null");
    filterMaps.addAll(initialFilters);
    sort();
  }

  /** Adds a list of filters to the chain.
   *
   * Each module can have a list of filters that must be called before each
   * request. This operation is inteded for modules to add a list of filters to
   * the chain of module filters.
   *
   * @param additionalFilterMappings The list of filter mappings provided by a
   * module. It cannot be null.
   */
  public void addFilters(final List<FilterMapping> additionalFilterMappings) {
    Validate.notNull(additionalFilterMappings, "The filter mappings cannot be"
        + " null");
    filterMaps.addAll(additionalFilterMappings);
    sort();
  }

  /** Comparator for filter priority, used in sort().
   */
  private static class PriorityComparator
      implements Comparator<FilterMapping> {

    /** {@inheritDoc}
     *
     * Lower priority comes first.
     */
    public int compare(final FilterMapping o1, final FilterMapping o2) {
      return o1.getPriority() - o2.getPriority();
    }
  }

  /** Sorts the list of filters according to their priority.
   */
  private void sort() {
    // Sort the list of filters.
    Collections.sort(filterMaps, new PriorityComparator());
  }

  /** Called by the servlet container to indicate to a servlet that it is being
   * placed into service.
   *
   * It calls init on all the registered filters.
   *
   * @param filterConfig The servlet's configuration and initialization
   * parameters.  This object is created by the container.
   *
   * @throws ServletException if an unexpected exception occurs.
   *
   * TODO Validate that when a filter has been already initialized, the
   * parameters has not changed. This is to avoid having two
   * FilterAndParameters instance with the same filter and different
   * parameters. In such case, the filter will be initialized only once with an
   * undefined set of parameters.
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.trace("Entering init");

    Set<Filter> alreadyInitialized = new HashSet<Filter>();
    for (final FilterMapping filterMapping : filterMaps) {
      if (!alreadyInitialized.contains(filterMapping.getFilter())) {

        // A servlet context wrapper that returns the init parameters of this
        // configured filter. This context is used in the filter config created
        // for the filter.
        final ServletContext context =
          new ServletContextWrapper(filterConfig.getServletContext()) {
            public String getInitParameter(final String name) {
              return filterMapping.getParameters().get(name);
            }
            @SuppressWarnings("unchecked")
            public Enumeration getInitParameterNames() {
              return IteratorUtils.asEnumeration(
                  filterMapping.getParameters().values().iterator());
            }
          };

        FilterConfig config = new FilterConfig() {
          public String getFilterName() {
            return filterConfig.getFilterName();
          }
          public ServletContext getServletContext() {
            return context;
          }
          public String getInitParameter(final String name) {
            return filterMapping.getParameters().get(name);
          }
          @SuppressWarnings("unchecked")
          public Enumeration getInitParameterNames() {
            return IteratorUtils.asEnumeration(
                filterMapping.getParameters().values().iterator());
          }
        };

        filterMapping.getFilter().init(config);
        alreadyInitialized.add(filterMapping.getFilter());
      }
    }
    log.trace("Leaving init");
  }

  /** Called by the servlet container to allow the servlet to respond to a
   * request.
   *
   * This gives a chance to every filter to filter the request. If a filter
   * does not call chain.doFilter, the request processing is stopped, including
   * further filter processing.
   *
   * @param request The ServletRequest object that contains the client's
   * request.
   *
   * @param response The ServletResponse object that contains the servlet's
   * response
   *
   * @param filterChain allows the Filter to pass on the request and response
   * to the next entity in the chain.
   *
   * @throws IOException if an input or output exception occurs.
   *
   * @throws ServletException if some other error occurs.
   */
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain filterChain) throws ServletException,
         IOException {

    log.trace("Entering doFilter");

    Chain chain = new Chain(filterMaps, filterChain);
    chain.doFilter(request, response);

    log.trace("Leaving doFilter");
  }

  /** Called by the web container to indicate to a filter that it is being
   * taken out of service.
   *
   * It calls destroy on all the registered filters.
   */
  public void destroy() {
    log.trace("Entering destroy");
    Set<Filter> alreadyDestroyed = new HashSet<Filter>();
    for (FilterMapping mapping : filterMaps) {
      if (!alreadyDestroyed.contains(mapping.getFilter())) {
        mapping.getFilter().destroy();
        alreadyDestroyed.add(mapping.getFilter());
      }
    }
    log.trace("Leaving destroy");
  }

  /** FilterChain implementation that keeps an iterator on the list of filters
   * and to call the next filter in the chain.
   */
  private static class Chain implements FilterChain {

    /** The class logger.
     */
    private static Logger log = LoggerFactory.getLogger(Chain.class);

    /** The list of filter that filters the requests.
     *
     * It is never null.
     */
    private List<FilterMapping> filterMappings;

    /** The tail of the chain.
     *
     * It is never null.
     */
    private FilterChain tailChain;

    /** The current filter in the chain.
     *
     * It is null until the iteration begins. The first call to doFilter
     * initializes this iterator. It iterates on elements of filters.
     */
    private Iterator<FilterMapping> current = null;

    /** Creates a new chain.
     *
     * @param theFilterMappings The list of filters to be ran over the
     * request/response.
     *
     * @param theTailChain The original servlet chain that is logically
     * appended at the end of the filter chain. It cannot be null.
     */
    public Chain(final List<FilterMapping> theFilterMappings, final FilterChain
        theTailChain) {
      log.trace("Entering Chain");
      Validate.notNull(theFilterMappings, "The filter mappings cannot be null");
      Validate.notNull(theTailChain, "The tail chain cannot be null");

      filterMappings = theFilterMappings;
      tailChain = theTailChain;
      log.trace("Leaving Chain");
    }

    /** Causes the next filter in the chain to be invoked, or if the calling
     * filter is the last filter in the chain, causes the resource at the end
     * of the chain to be invoked.
     *
     * @param request The ServletRequest object that contains the client's
     * request. It cannot be null.
     *
     * @param response The ServletResponse object that contains the servlet's
     * response
     *
     * @throws IOException if an input or output exception occurs.
     *
     * @throws ServletException if an error occurs.
     */
    public void doFilter(final ServletRequest request, final ServletResponse
        response) throws java.io.IOException, ServletException {
      log.trace("Entering doFilter");

      Validate.notNull(request, "The request cannot be null");

      if (!(request instanceof HttpServletRequest)) {
        throw new RuntimeException("Calling doFilter on an non http request");
      }

      HttpServletRequest httpRequest = (HttpServletRequest) request;

      if (current == null) {
        current = filterMappings.iterator();
      }
      if (current.hasNext()) {

        FilterMapping filterMapping = current.next();
        String url = httpRequest.getServletPath() + httpRequest.getPathInfo();
        if (url.matches(filterMapping.getPattern())) {
          // Call the filter.
          filterMapping.getFilter().doFilter(request, response, this);
        } else {
          // Skip current filter. The recursive implementation is easier to
          // understand.
          doFilter(request, response);
        }
      } else {
        tailChain.doFilter(request, response);
      }
      log.trace("Leaving doFilter");
    }
  }
}

