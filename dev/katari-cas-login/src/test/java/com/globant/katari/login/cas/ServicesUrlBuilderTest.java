/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/* Tests the cas service builder.
 */
public class ServicesUrlBuilderTest {

  /* Tests the services url when the cas server is deployed in the same
   * container as the aplication.
   */
  @Test
  public final void testBuildServiceUrl() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getScheme()).andStubReturn("http");
    expect(request.getServerName()).andStubReturn("localhost");
    expect(request.getServerPort()).andStubReturn(80);
    expect(request.getContextPath()).andStubReturn("/katari-web");
    replay(request);

    ServicesUrlBuilder builder;
    builder = new ServicesUrlBuilder("http://casurl", "service");

    assertThat(builder.buildServiceUrl(request),
        is("http://localhost:80/katari-web/service"));
  }

  @Test
  public final void testBuildCasLoginUrl() {
    ServicesUrlBuilder builder;
    builder = new ServicesUrlBuilder("http://casurl", "service");

    assertThat(builder.buildCasLoginUrl(), is("http://casurl/login"));
  }

  @Test
  public final void testBuildCasLogoutUrl() {
    ServicesUrlBuilder builder;
    builder = new ServicesUrlBuilder("http://casurl/", "service");

    assertThat(builder.buildCasLogoutUrl(), is("http://casurl/logout"));
  }

  /* Tests the services url when the cas server is deployed in a container
   * listening in the previous port as the aplication as specified in a system
   * property.
   */
  @Test
  public final void testBuildCasValidatorUrl() {
    ServicesUrlBuilder builder;
    builder = new ServicesUrlBuilder("http://casurl/", "service");

    assertThat(builder.buildCasValidatorUrl(),
        is("http://casurl/serviceValidate"));
  }

  /* Tests the services url when the cas server is deployed in a container
   * listening in a different host name.
   */
  @Test
  public final void testTransformServerName() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getScheme()).andStubReturn("http");
    expect(request.getServerName()).andStubReturn("localhost");
    expect(request.getServerPort()).andStubReturn(82);
    expect(request.getContextPath()).andStubReturn("/katari-source");
    replay(request);

    ServicesUrlBuilder builder;
    builder = new ServicesUrlBuilder("http://casurl", "service");

    assertThat(builder.buildCasLoginUrl(), is("http://casurl/login"));
    assertThat(builder.buildServiceUrl(request),
        is("http://localhost:82/katari-source/service"));
    assertThat(builder.buildCasValidatorUrl(),
        is("http://casurl/serviceValidate"));
  }
}

