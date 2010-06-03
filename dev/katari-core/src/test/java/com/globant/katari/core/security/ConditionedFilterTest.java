package com.globant.katari.core.security;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;

import org.acegisecurity.util.FilterChainProxy;
import org.easymock.classextension.EasyMock;

/**
 * Tests the Conditioned Filter.
 *
 * @author rcunci
 */
public class ConditionedFilterTest extends TestCase {

  /**
   * Tests the doFilter with a true predicate
   *
   * @throws Exception
   */
  public void testDoFilter_true() throws Exception {
    FilterChainProxy filterChainProxy =
      EasyMock.createMock(FilterChainProxy.class);
    RequestPredicate predicate = EasyMock.createMock(RequestPredicate.class);
    FilterConfig filterConfig = EasyMock.createMock(FilterConfig.class);
    ServletRequest request = EasyMock.createMock(ServletRequest.class);
    ServletResponse response = EasyMock.createMock(ServletResponse.class);
    FilterChain filterChain = EasyMock.createMock(FilterChain.class);

    EasyMock.expect(predicate.evaluate(request)).andReturn(true).once();
    filterChainProxy.init(filterConfig);
    EasyMock.expectLastCall().once();
    filterChainProxy.doFilter(request, response, filterChain);
    EasyMock.expectLastCall().once();
    filterChainProxy.destroy();
    EasyMock.expectLastCall().once();

    EasyMock.replay(filterChainProxy);
    EasyMock.replay(predicate);
    EasyMock.replay(filterConfig);
    EasyMock.replay(request);
    EasyMock.replay(response);
    EasyMock.replay(filterChain);

    ConditionedFilter filter =
      new ConditionedFilter(predicate, filterChainProxy);

    filter.init(filterConfig);
    assertTrue(filter.doFilter(request, response, filterChain));
    filter.destroy();

    EasyMock.verify(filterChainProxy);
    EasyMock.verify(predicate);
    EasyMock.verify(filterConfig);
    EasyMock.verify(request);
    EasyMock.verify(response);
    EasyMock.verify(filterChain);
  }

  /**
   * Tests the doFilter with a false predicate
   *
   * @throws Exception
   */
  public void testDoFilter_false() throws Exception {
    FilterChainProxy filterChainProxy =
      EasyMock.createMock(FilterChainProxy.class);
    RequestPredicate predicate = EasyMock.createMock(RequestPredicate.class);
    FilterConfig filterConfig = EasyMock.createMock(FilterConfig.class);
    ServletRequest request = EasyMock.createMock(ServletRequest.class);
    ServletResponse response = EasyMock.createMock(ServletResponse.class);
    FilterChain filterChain = EasyMock.createMock(FilterChain.class);

    filterChainProxy.init(filterConfig);
    EasyMock.expectLastCall().once();
    EasyMock.expect(predicate.evaluate(request)).andReturn(false).once();
    filterChainProxy.destroy();
    EasyMock.expectLastCall().once();

    EasyMock.replay(filterChainProxy);
    EasyMock.replay(predicate);
    EasyMock.replay(filterConfig);
    EasyMock.replay(request);
    EasyMock.replay(response);
    EasyMock.replay(filterChain);

    ConditionedFilter filter =
      new ConditionedFilter(predicate, filterChainProxy);

    filter.init(filterConfig);
    assertFalse(filter.doFilter(request, response, filterChain));
    filter.destroy();

    EasyMock.verify(filterChainProxy);
    EasyMock.verify(predicate);
    EasyMock.verify(filterConfig);
    EasyMock.verify(request);
    EasyMock.verify(response);
    EasyMock.verify(filterChain);
  }

  /**
   * Tests the doFilter with no predicate
   *
   * @throws Exception
   */
  public void testDoFilter_noPredicate() throws Exception {
    FilterConfig filterConfig = EasyMock.createMock(FilterConfig.class);
    ServletRequest request = EasyMock.createMock(ServletRequest.class);
    ServletResponse response = EasyMock.createMock(ServletResponse.class);
    FilterChain filterChain = EasyMock.createMock(FilterChain.class);

    EasyMock.replay(filterConfig);
    EasyMock.replay(request);
    EasyMock.replay(response);
    EasyMock.replay(filterChain);

    ConditionedFilter filter = new ConditionedFilter();

    filter.init(filterConfig);
    assertFalse(filter.doFilter(request, response, filterChain));
    filter.destroy();

    EasyMock.verify(filterConfig);
    EasyMock.verify(request);
    EasyMock.verify(response);
    EasyMock.verify(filterChain);
  }

}
