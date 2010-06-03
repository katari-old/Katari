package com.globant.katari.core.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.Validate;

/**
 * This filter is the entry point to katari security.
 *
 * It selects whether to use the default filter chain or an alternative one
 * based on some condition. This allows to select between 2 filter chains, one
 * being the default chain and another the alternate one, for purposes of
 * security.
 *
 * @author rcunci
 */
public class SecurityEntryPointFilter implements Filter {

  /** The default filter chain.
   *
   * It is never null.
   */
  private Filter defaultFilterChain;

  /** The alternative filter chain, that includes a condition that must be true
   * to use this filter.
   *
   * It is never null.
  */
  private ConditionedFilter conditionedFilter;

  /**
   * Constructor.
   *
   * @param theDefaultFilterChain The default filter chain to use. It cannot be
   * null.
   *
   * @param theConditionedFilter The alternative filter chain to use if it's
   * condition evaluates to true. It cannot be null.
   */
  public SecurityEntryPointFilter(final Filter theDefaultFilterChain,
      final ConditionedFilter theConditionedFilter) {
    Validate.notNull(theDefaultFilterChain, "The default filter chain "
        + "cannot be null.");
    Validate.notNull(theConditionedFilter, "The conditioned filter "
        + "cannot be null.");

    defaultFilterChain = theDefaultFilterChain;
    conditionedFilter = theConditionedFilter;
  }

  /**
   * {@inheritDoc}
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    defaultFilterChain.init(filterConfig);
    conditionedFilter.init(filterConfig);
  }

  /** Calls the conditioned filter and if it does not handle the request pass
   * it to the default filter.
   *
   * {@inheritDoc}
   */
  public void doFilter(final ServletRequest request,
      final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    if (!conditionedFilter.doFilter(request, response, chain)) {
      defaultFilterChain.doFilter(request, response, chain);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void destroy() {
    defaultFilterChain.destroy();
    conditionedFilter.destroy();
  }
}

