/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import static org.junit.Assert.assertThat;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.mapper.ConfigLoader;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;

/* Tests the custom sitemesh decorator mapper.
 */
public class FullUriConfigDecoratorMapperTest  {

  @Test
  public void getNamedDecorator() throws Exception {

    Decorator decorator = createMock(Decorator.class);
    expect(decorator.getRole()).andReturn(null);
    replay(decorator);

    ConfigLoader configLoader = createMock(ConfigLoader.class);
    expect(configLoader.getDecoratorByName("sampleDecorator"))
      .andReturn(decorator);
    replay(configLoader);

    FullUriConfigDecoratorMapper mapper = new FullUriConfigDecoratorMapper();
    mapper.setConfigLoader(configLoader);
    Decorator result = mapper.getNamedDecorator(null, "sampleDecorator");
    assertThat(result, is(decorator));
  }

  @Test
  public final void getDecorator() throws Exception {

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
    Decorator result = mapper.getDecorator(request, null);
    assertThat(result, is(decorator));
  }

  @Test
  public final void getDecorator_skip() throws Exception {

    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getAttribute("katari-skip-decoration")).andReturn("");
    replay(request);

    FullUriConfigDecoratorMapper mapper = new FullUriConfigDecoratorMapper();
    Decorator result = mapper.getDecorator(request, null);
    assertThat(result, is(nullValue()));
  }
}

