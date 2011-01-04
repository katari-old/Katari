/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.monitoring;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;

import javax.servlet.http.HttpServletRequest;

/** Tests the module.xml.
 */
public class NoGzipMonitoringFilterTest {

  @Test
  public void testDoFilter() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addHeader("Accept-Encoding", "gzip");

    FilterChain chain = new FilterChain() {
      @SuppressWarnings("unchecked")
      public void doFilter(final ServletRequest request,
          final ServletResponse response) {
        Enumeration headers = ((HttpServletRequest) request).getHeaders(
            "Accept-Encoding");
        assertThat(headers.hasMoreElements(), is(false));
      }
    };

    Filter filter = new NoGzipMonitoringFilter();
    filter.init(new MockFilterConfig());

    filter.doFilter(request, response, chain);
  }
}

