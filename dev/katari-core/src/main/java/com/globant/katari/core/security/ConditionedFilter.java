package com.globant.katari.core.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.acegisecurity.util.FilterChainProxy;
import org.apache.commons.lang.Validate;

/**
 * This filter links a filter chain proxy with a predicate.
 * If the predicate evaluates to true, then the filter chain proxy is used.
 * Otherwise, the filter chain proxy is ignored.
 *
 * @author rcunci
 */
public class ConditionedFilter {

  /** requestPredicate. Can be null, that only happens when this class is
   * created through the empty constructor, and it means that this filter
   * should be ignored by the filter selector. If this is null, then
   * also filterChainProxy is null.
   */
  private RequestPredicate requestPredicate;

  /** filterChainProxy. Can be null, that only happens when this class is
   * created through the empty constructor, and it means that this filter
   * should be ignored by the filter selector. If this is null, then
   * also requestPredicate is null.
   */
  private FilterChainProxy filterChainProxy;

  /**
   * Constructor. In this case, everything is left null and this conditioned
   * filter is ignored by the application context. Everything works as if
   * the only filter chain for security was the default filter.
   */
  public ConditionedFilter() {
  }

  /**
   * Constructor. The request predicate and the filter chain proxy are set, so
   * the predicate will be evaluated and if true, the filter proxy will be
   * executed on a request.
   *
   * @param theRequestPredicate The request predicate. Cannot be null.
   * @param theFilterChainProxy The filter chain proxy. Cannot be null.
   */
  public ConditionedFilter(final RequestPredicate theRequestPredicate,
      final FilterChainProxy theFilterChainProxy) {

    Validate.notNull(theRequestPredicate, "The request predicate cannot be "
        + "null in this constructor.");
    Validate.notNull(theFilterChainProxy, "The filter chain proxy cannot be "
        + "null in this constructor.");

    filterChainProxy = theFilterChainProxy;
    requestPredicate = theRequestPredicate;
  }

  /**
   * Called by the web container to indicate to a filter that it is being
   * placed into service. The servlet container calls the init method exactly
   * once after instantiating the filter. The init method must complete
   * successfully before the filter is asked to do any filtering work.
   *
   * @param filterConfig the filter config from web.xml.
   *
   * @throws ServletException in case of error.
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    if (filterChainProxy != null) {
      filterChainProxy.init(filterConfig);
    }
  }

  /**
   * The <code>doFilter</code> method of the Filter is called by the container
   * each time a request/response pair is passed through the chain due
   * to a client request for a resource at the end of the chain.
   * The FilterChain passed in to this method allows the Filter to pass on the
   * request and response to the next entity in the chain.<p>
   * If the predicate evaluates to true and the filter is executed,
   * then the result value is true, otherwise it's false.
   *
   * @param request The servlet request.
   * @param response The servlet response.
   * @param chain The chain to follow the filter chain.
   * @return true if the predicate is evaluated to true and the filter is
   * executed, false otherwise.
   *
   * @throws IOException in case of error
   * @throws ServletException in case of error
   */
  public boolean doFilter(final ServletRequest request,
      final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    if (requestPredicate  != null && requestPredicate.evaluate(request)) {
      filterChainProxy.doFilter(request, response, chain);
      return true;
    }

    return false;
  }

  /**
   * Called by the web container to indicate to a filter that it is being taken
   * out of service. This method is only called once all threads within the
   * filter's doFilter method have exited or after a timeout period has passed.
   * After the web container calls this method, it will not call the doFilter
   * method again on this instance of the filter.
   *
   * This method gives the filter an opportunity to clean up any resources that
   * are being held (for example, memory, file handles, threads) and make sure
   * that any persistent state is synchronized with the filter's current state
   * in memory.
   */
  public void destroy() {
    if (filterChainProxy != null) {
      filterChainProxy.destroy();
    }
  }
}
