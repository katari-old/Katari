/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.Before;

import static org.easymock.classextension.EasyMock.*;

public class OptionalFilterTest {

  private FilterConfig filterConfig;

  private HttpServletRequest request;

  private HttpServletResponse response;

  private FilterChain filterChain;

  @Before
  public final void setUp() throws Exception {

    // Mocks the filter config.
    filterConfig = createMock(FilterConfig.class);
    replay(filterConfig);

    // Mocks the servlet request.
    request = createNiceMock(HttpServletRequest.class);
    replay(request);

    // Mocks the servlet response.
    response = createNiceMock(HttpServletResponse.class);
    replay(response);

    // Mocks the filter chain.
    filterChain = createNiceMock(FilterChain.class);
    replay(filterChain);
  }

  @Test
  public final void testInit_enabled() throws Exception {

    Filter target = createMock(Filter.class);
    target.init(filterConfig);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, true);
    optionalFilter.init(filterConfig);

    verify(target);
  }

  @Test
  public final void testInit_disabled() throws Exception {

    Filter target = createMock(Filter.class);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, false);
    optionalFilter.init(filterConfig);

    verify(target);
  }

  @Test
  public final void testDoFilter_enabled() throws Exception {

    Filter target = createMock(Filter.class);
    target.doFilter(request, response, filterChain);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, true);
    optionalFilter.doFilter(request, response, filterChain);

    verify(target);
  }

  @Test
  public final void testDoFilter_disabled() throws Exception {

    Filter target = createMock(Filter.class);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, false);
    optionalFilter.doFilter(request, response, filterChain);

    verify(target);
  }

  @Test
  public final void testDestroy_enabled() throws Exception {

    Filter target = createMock(Filter.class);
    target.destroy();
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, true);
    optionalFilter.destroy();

    verify(target);
  }

  @Test
  public final void testDestroy_disabled() throws Exception {

    Filter target = createMock(Filter.class);
    replay(target);

    OptionalFilter optionalFilter = new OptionalFilter(target, false);
    optionalFilter.destroy();

    verify(target);
  }
}

