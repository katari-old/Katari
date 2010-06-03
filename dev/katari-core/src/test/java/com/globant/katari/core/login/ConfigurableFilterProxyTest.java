/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.login;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;

public class ConfigurableFilterProxyTest extends TestCase {

  private ConfigurableFilterProxy filter;
  private ServletRequest request;
  private ServletResponse response;
  private FilterChain chain;
  private Filter delegateFilter;

  @Override
  protected void setUp(){
    filter = new ConfigurableFilterProxy();
    request = createMock(ServletRequest.class);
    response = createMock(ServletResponse.class);
    chain = createMock(FilterChain.class);
  }

  public void testDoFilter_noDelegate() throws Exception {
    try {
      filter.doFilter(request, response, chain);
      fail("doFilter did not validate for a null delegate.");
    } catch (IllegalStateException e) {
      // Test passed.
    }
  }

  public void testDoFilter_withDelegate() throws Exception {
    delegateFilter = createMock(Filter.class);
    delegateFilter.doFilter(request, response, chain);
    expectLastCall().once();
    filter.setDelegate(delegateFilter);
    replay(delegateFilter);
    filter.doFilter(request, response, chain);
    verify(delegateFilter);
  }
}

