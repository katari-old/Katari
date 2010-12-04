/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServletRequest;

import com.globant.katari.cas.CasServicesUrlBuilder;
import com.globant.katari.cas.UrlTransformer;

import junit.framework.TestCase;

/* Tests the cas service builder.
 */
public class ServicesUrlBuilderTest extends TestCase {

  /* Tests the services url when the cas server is deployed in the same
   * container as the aplication.
   */
  public final void testGetLocalCasServices() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getScheme()).andStubReturn("http");
    expect(request.getServerName()).andStubReturn("localhost");
    expect(request.getServerPort()).andStubReturn(80);
    expect(request.getContextPath()).andStubReturn("/katari-web");
    replay(request);

    CasServicesUrlBuilder builder;
    UrlTransformer webToCas = new UrlTransformer(null, null, "^(.*)web$",
        "$1cas");
    builder = new CasServicesUrlBuilder(webToCas, webToCas, "service", "login",
        "validator");

    assertEquals("http://localhost:80/katari-web/service",
        builder.buildServiceUrl(request));
    assertEquals("http://localhost:80/katari-cas/login",
        builder.buildLoginUrl(request));
    assertEquals("http://localhost:80/katari-cas/validator",
        builder.buildCasValidatorUrl(request));
  }

  /* Tests the services url when the cas server is deployed in a container
   * listening in the previous port as the aplication.
   */
  public final void testGetPreviousPortServices() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getScheme()).andStubReturn("http");
    expect(request.getServerName()).andStubReturn("localhost");
    expect(request.getServerPort()).andStubReturn(81);
    expect(request.getContextPath()).andStubReturn("/katari-source");
    replay(request);

    CasServicesUrlBuilder builder;
    UrlTransformer portMinus1 = new UrlTransformer(null, "-1", "^(.*)source$",
        "$1web");
    builder = new CasServicesUrlBuilder(portMinus1, portMinus1, "service",
        "login", "validator");

    assertEquals("http://localhost:81/katari-source/service",
        builder.buildServiceUrl(request));
    assertEquals("http://localhost:80/katari-web/login",
        builder.buildLoginUrl(request));
    assertEquals("http://localhost:80/katari-web/validator",
        builder.buildCasValidatorUrl(request));
  }

  /* Tests the services url when the cas server is deployed in a container
   * listening in the previous port as the aplication as specified in a system
   * property.
   */
  public final void testGetPreviousPortFromPropertyServices() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getScheme()).andStubReturn("http");
    expect(request.getServerName()).andStubReturn("localhost");
    expect(request.getServerPort()).andStubReturn(82);
    expect(request.getContextPath()).andStubReturn("/katari-source");
    replay(request);

    CasServicesUrlBuilder builder;
    String previous = System.setProperty("cas.test.prop", "-2");
    UrlTransformer portProperty = new UrlTransformer(null, "cas.test.prop",
        "^(.*)source$", "$1web");
    builder = new CasServicesUrlBuilder(portProperty, portProperty, "service",
        "login", "validator");

    assertEquals("http://localhost:82/katari-source/service",
        builder.buildServiceUrl(request));
    assertEquals("http://localhost:80/katari-web/login",
        builder.buildLoginUrl(request));
    assertEquals("http://localhost:80/katari-web/validator",
        builder.buildCasValidatorUrl(request));

    if (previous != null) {
      System.setProperty("cas.test.prop", previous);
    }
  }

  /* Tests the services url when the cas server is deployed in a container
   * listening in a different host name.
   */
  public final void testTransformServerName() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getScheme()).andStubReturn("http");
    expect(request.getServerName()).andStubReturn("localhost");
    expect(request.getServerPort()).andStubReturn(82);
    expect(request.getContextPath()).andStubReturn("/katari-source");
    replay(request);

    CasServicesUrlBuilder builder;
    UrlTransformer newServer = new UrlTransformer("127.0.0.1", null,
        "^(.*)source$", "$1web");
    builder = new CasServicesUrlBuilder(newServer, newServer, "service",
        "login", "validator");

    assertEquals("http://localhost:82/katari-source/service",
        builder.buildServiceUrl(request));
    assertEquals("http://127.0.0.1:82/katari-web/login",
        builder.buildLoginUrl(request));
    assertEquals("http://127.0.0.1:82/katari-web/validator",
        builder.buildCasValidatorUrl(request));
  }
}

