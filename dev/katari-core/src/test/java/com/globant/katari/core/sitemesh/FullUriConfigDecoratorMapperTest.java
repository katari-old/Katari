/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.mapper.ConfigLoader;

import static org.easymock.classextension.EasyMock.*;

/* Tests the custom sitemesh decorator mapper.
 */
public class FullUriConfigDecoratorMapperTest  extends TestCase {

  /* Tests the getNamedDecorator method.
   */
  public final void testGetNamedDecorator() throws Exception {

    Decorator decorator = createMock(Decorator.class);
    expect(decorator.getRole()).andReturn(null);
    replay(decorator);

    ConfigLoader configLoader = createMock(ConfigLoader.class);
    expect(configLoader.getDecoratorByName("sampleDecorator"))
      .andReturn(decorator);
    replay(configLoader);

    FullUriConfigDecoratorMapper mapper = new FullUriConfigDecoratorMapper();
    mapper.setConfigLoader(configLoader);
    assertEquals(decorator, mapper.getNamedDecorator(null, "sampleDecorator"));
  }

  /* Tests the getDecorator method.
   */
  public final void testGetDecorator() throws Exception {

    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getServletPath()).andReturn("/servletPath");
    expect(request.getPathInfo()).andReturn("/pathInfo");
    replay(request);

    Decorator decorator = createMock(Decorator.class);
    expect(decorator.getRole()).andReturn(null);
    replay(decorator);

    ConfigLoader configLoader = createMock(ConfigLoader.class);
    expect(configLoader.getMappedName("/servletPath/pathInfo"))
      .andReturn("sampleDecorator");
    expect(configLoader.getDecoratorByName("sampleDecorator"))
      .andReturn(decorator);
    replay(configLoader);

    FullUriConfigDecoratorMapper mapper = new FullUriConfigDecoratorMapper();
    mapper.setConfigLoader(configLoader);
    assertSame(decorator, mapper.getDecorator(request, null));
  }
}

