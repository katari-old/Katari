/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.view;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.easymock.EasyMock.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import com.globant.katari.jsmodule.domain.BundleCache;

public class ContentModuleServletTest {

  private BundleCache cache;

  @Before
  public void setUp() {
    cache = createMock(BundleCache.class);
  }

  /* Tests if service correctly dispatches the request.
   */
  @Test
  public final void testService() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockServletConfig config = new MockServletConfig();

    ContentModuleServlet servlet = new ContentModuleServlet(cache);
    servlet.init(config);

    request.setServletPath(
        "/com/globant/katari/jsmodule/testfile/image/a.png");
    request.setMethod("GET");
    servlet.service(request, response);

    assertThat(response.getStatus(), is(200));
    assertThat(response.getContentType(), is("image/png"));
  }

  /* Tests if service errors if the path should not be exposed.
   */
  @Test
  public final void testService_pathNotServed() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockServletConfig config = new MockServletConfig();

    ContentModuleServlet servlet = new ContentModuleServlet(cache);
    servlet.init(config);

    request.setServletPath(
        "/com/globant/katari/jsmodule/testfile/notserved/a.png");
    request.setMethod("GET");
    servlet.service(request, response);

    assertThat(response.getStatus(), is(404));
  }

  /* Tests if service correctly dispatches the request for a bundled
   * file which was cached.
   */
  @Test
  public final void testService_bundlePathFile() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockServletConfig config = new MockServletConfig();

    String content = "var testFunction;";
    expect(cache.findContent("md5_hash_key.js")).andReturn(content);
    replay(cache);

    ContentModuleServlet servlet = new ContentModuleServlet(cache);
    servlet.init(config);

    request.setServletPath(
        "/com/globant/katari/jsmodule/bundle/md5_hash_key.js");
    request.setMethod("GET");
    servlet.service(request, response);

    assertThat(response.getStatus(), is(200));
    assertThat(response.getContentType(), is("text/javascript"));
    assertThat(response.getContentAsString(), is(content));

    verify(cache);
  }

  @Test (expected = RuntimeException.class)
  public void new_nullCache() {
    new ContentModuleServlet(null);
  }
}

