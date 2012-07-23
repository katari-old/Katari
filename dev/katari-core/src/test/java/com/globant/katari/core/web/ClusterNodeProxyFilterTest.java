/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.EasyMock.*;

import com.globant.katari.tools.NanoHTTPD;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;

public class ClusterNodeProxyFilterTest {

  private NanoHTTPD server = null;

  private ClusterNodeProxyFilter filter;

  private Map<String, String> emptyNodeToUrl = new HashMap<String, String>();

  private Map<String, String> nodeToUrl;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  // A filter chain mock that expects a call to doFilter with request and
  // response.
  private FilterChain expectsDoFilterChain;

  private FilterChain notCalledChain;

  @Before public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    expectsDoFilterChain = createMock(FilterChain.class);
    expectsDoFilterChain.doFilter(request, response);
    replay(expectsDoFilterChain);

    notCalledChain = createMock(FilterChain.class);
    replay(notCalledChain);

    server = new NanoHTTPD(0) {
      public Response serve(final String uri, final String method, final
          Properties headers, final Properties parameters) {
        String result = new String(method + ": " + uri);
        for (Object parameterName : parameters.keySet()) {
          result = result + " p:" + parameterName + ": "
            + parameters.get(parameterName);
        }
        for (Object headerName : headers.keySet()) {
          result = result + " h:" + headerName + ": "
            + headers.get(headerName);
        }
        return new NanoHTTPD.Response(NanoHTTPD.MIME_PLAINTEXT, result);
      }
    };
    nodeToUrl = new LinkedHashMap<String, String>();
    nodeToUrl.put("node-1", "http://localhost:" + server.getPort());
    nodeToUrl.put("node-2", "http://host-2:8080");
  }

  @After public void tearDown() {
    // We stop the server.
    if (server != null) {
      server.stop();
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void constructor_localForbidden() {
    Map<String, String> nodeToUrl = new HashMap<String, String>();
    nodeToUrl.put("local", "something");
    filter = new ClusterNodeProxyFilter(nodeToUrl);
  }

  @Test (expected = IllegalArgumentException.class)
  public void constructor_specialCharsForbidden() {
    Map<String, String> nodeToUrl = new HashMap<String, String>();
    nodeToUrl.put("l ocal", "something");
    filter = new ClusterNodeProxyFilter(nodeToUrl);
  }

  @Test
  public void doFilter_noNodes() throws Exception {
    filter = new ClusterNodeProxyFilter(emptyNodeToUrl);

    // No additional parameters.
    filter.doFilter(request, response, expectsDoFilterChain);
    verify(expectsDoFilterChain);
  }

  @Test
  public void doFilter_noNodesWithParameter() throws Exception {
    filter = new ClusterNodeProxyFilter(emptyNodeToUrl);

    // The node parameter.
    request.setParameter("katari-node", "some-node");
    filter.doFilter(request, response, expectsDoFilterChain);
    verify(expectsDoFilterChain);
  }

  @Test
  public void doFilter_noNodesWithCookie() throws Exception {
    filter = new ClusterNodeProxyFilter(emptyNodeToUrl);

    // The node cookie.
    request.setCookies(new Cookie("katari-node", "some-node"),
        new Cookie("some-other-cookie", "value"));
    filter.doFilter(request, response, expectsDoFilterChain);
    verify(expectsDoFilterChain);
  }

  @Test
  public void doFilter_listNodes() throws Exception {
    filter = new ClusterNodeProxyFilter(nodeToUrl);

    filter.doFilter(request, response, expectsDoFilterChain);

    // It must not call chain.doFilter, just send a response body and a cookie.
    Cookie katariNode = response.getCookie("katari-node");
    assertThat(katariNode, is(not(nullValue())));
    assertThat(katariNode.getMaxAge(), is(0));
    assertThat(response.getContentAsString().matches(".*node-1.*node-2.*"),
        is(true));
  }

  @Test
  public void doFilter_toLocal() throws Exception {
    filter = new ClusterNodeProxyFilter(nodeToUrl);

    request.setParameter("katari-node", "local");
    filter.doFilter(request, response, expectsDoFilterChain);
    verify(expectsDoFilterChain);
  }

  @Test
  public void doFilter_toProxy() throws Exception {
    filter = new ClusterNodeProxyFilter(nodeToUrl);

    request.setParameter("katari-node", "node-1");
    request.setContent(new byte[0]);
    request.setMethod("GET");
    request.setRequestURI("/katari/module/a.do");
    filter.doFilter(request, response, notCalledChain);
    assertThat(response.getContentAsString().matches(
          "^GET: /katari/module/a.do p:katari-node: local.*"), is(true));
    verify(notCalledChain);
  }

  @Test
  public void doFilter_toProxyWithParameters() throws Exception {
    filter = new ClusterNodeProxyFilter(nodeToUrl);

    request.setParameter("katari-node", "node-1");
    request.setParameter("p1", "v1");
    request.setContent(new byte[0]);
    request.setMethod("POST");
    request.setRequestURI("/katari/module/a.do");
    request.addHeader("test", "test");
    filter.doFilter(request, response, notCalledChain);

    String content = response.getContentAsString();
    assertThat(content.matches(
          "^POST: /katari/module/a.do p:katari-node: local p:p1: v1.*"),
        is(true));
    assertThat(content.matches(".*h:test: test.*"), is(true));
    verify(notCalledChain);
  }

  @Test
  public void doFilter_toProxyWithPayload() throws Exception {
    filter = new ClusterNodeProxyFilter(nodeToUrl);

    request.setParameter("katari-node", "node-1");
    request.setContent("test=value".getBytes("UTF-8"));
    request.setMethod("POST");
    request.setRequestURI("/katari/module/a.do");
    filter.doFilter(request, response, notCalledChain);

    String content = response.getContentAsString();
    assertThat(content.matches(".*p:test: value.*"), is(true));
    verify(notCalledChain);
  }
}

