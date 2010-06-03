package com.globant.katari.core.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

/**
 * Tests the Filter Chain Selector.
 *
 * @author rcunci
 */
public class SecurityEntryPointFilterTest extends TestCase {

  /**
   * Tests the filter selector in the case that the conditioned filter is not
   * executed.
   *
   * @throws Exception
   */
  public void testDoFilter_false() throws Exception {
    ConditionedFilter conditionedFilter = EasyMock.createMock(
        ConditionedFilter.class);
    Filter defaultFilterChain = EasyMock.createMock(Filter.class);
    FilterConfig filterConfig = EasyMock.createMock(FilterConfig.class);
    ServletRequest request = EasyMock.createMock(ServletRequest.class);
    ServletResponse response = EasyMock.createMock(ServletResponse.class);
    FilterChain filterChain = EasyMock.createMock(FilterChain.class);

    defaultFilterChain.init(filterConfig);
    EasyMock.expectLastCall().once();
    defaultFilterChain.doFilter(request, response, filterChain);
    EasyMock.expectLastCall().once();
    defaultFilterChain.destroy();
    EasyMock.expectLastCall().once();

    conditionedFilter.init(filterConfig);
    EasyMock.expectLastCall().once();
    EasyMock.expect(conditionedFilter.doFilter(request, response, filterChain))
      .andReturn(false).once();
    conditionedFilter.destroy();
    EasyMock.expectLastCall().once();

    EasyMock.replay(conditionedFilter);
    EasyMock.replay(defaultFilterChain);
    EasyMock.replay(request);
    EasyMock.replay(response);
    EasyMock.replay(filterChain);

    SecurityEntryPointFilter entryPointFilter =
      new SecurityEntryPointFilter(defaultFilterChain, conditionedFilter);

    entryPointFilter.init(filterConfig);
    entryPointFilter.doFilter(request, response, filterChain);
    entryPointFilter.destroy();

    EasyMock.verify(conditionedFilter);
    EasyMock.verify(defaultFilterChain);
    EasyMock.verify(request);
    EasyMock.verify(response);
    EasyMock.verify(filterChain);
  }

  /**
   * Tests the filter selector in the case that the conditioned filter is
   * executed.
   *
   * @throws Exception
   */
  public void testDoFilter_true() throws Exception {
    ConditionedFilter conditionedFilter = EasyMock.createMock(
        ConditionedFilter.class);
    Filter defaultFilterChain = EasyMock.createMock(Filter.class);
    FilterConfig filterConfig = EasyMock.createMock(FilterConfig.class);
    ServletRequest request = EasyMock.createMock(ServletRequest.class);
    ServletResponse response = EasyMock.createMock(ServletResponse.class);
    FilterChain filterChain = EasyMock.createMock(FilterChain.class);

    defaultFilterChain.init(filterConfig);
    EasyMock.expectLastCall().once();
    defaultFilterChain.destroy();
    EasyMock.expectLastCall().once();

    conditionedFilter.init(filterConfig);
    EasyMock.expectLastCall().once();
    EasyMock.expect(conditionedFilter.doFilter(request, response, filterChain))
      .andReturn(true).once();
    conditionedFilter.destroy();
    EasyMock.expectLastCall().once();

    EasyMock.replay(conditionedFilter);
    EasyMock.replay(defaultFilterChain);
    EasyMock.replay(request);
    EasyMock.replay(response);
    EasyMock.replay(filterChain);

    SecurityEntryPointFilter entryPointFilter =
      new SecurityEntryPointFilter(defaultFilterChain, conditionedFilter);

    entryPointFilter.init(filterConfig);
    entryPointFilter.doFilter(request, response, filterChain);
    entryPointFilter.destroy();

    EasyMock.verify(conditionedFilter);
    EasyMock.verify(defaultFilterChain);
    EasyMock.verify(request);
    EasyMock.verify(response);
    EasyMock.verify(filterChain);
  }
}
