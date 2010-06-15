/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.trails;

import junit.framework.TestCase;

import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.ISpecificationSource;
import org.apache.tapestry.services.Infrastructure;
import org.apache.tapestry.spec.IComponentSpecification;

import static org.easymock.EasyMock.*;

public class ModuleTemplateSourceDelegateTest extends TestCase {

  public void testFindPageSpecificationDefaultLocation() {
    IRequestCycle cycle = createNiceMock(IRequestCycle.class);
    Infrastructure infrastructure = createMock(Infrastructure.class);
    ISpecificationSource specSource = createMock(ISpecificationSource.class);
    INamespace namespace = createMock(INamespace.class);
    IComponentSpecification ics = createMock(IComponentSpecification.class);
    expect(
        namespace.getPropertyValue("org.apache.tapestry.page-class-packages"))
        .andReturn("com.globant.katari.trails.test.pages");
    expect(cycle.getInfrastructure()).andReturn(infrastructure);
    expect(infrastructure.getSpecificationSource()).andReturn(specSource);
    Resource r = new ClasspathResource(new DefaultClassResolver(),
        "com/globant/katari/trails/test/pages/DefaultException.page");
    expect(specSource.getPageSpecification(r)).andReturn(ics);;
    replay(cycle);
    replay(infrastructure);
    replay(specSource);
    replay(namespace);

    ModuleTemplateSourceDelegate msd = new ModuleTemplateSourceDelegate();
    assertNotNull(msd.findPageSpecification(cycle, namespace,
        "DefaultException"));

    verify(cycle);
    verify(namespace);
    verify(specSource);
    verify(infrastructure);
  }

  public void testFindPageSpecificationAdditional() {
    IRequestCycle cycle = createMock(IRequestCycle.class);
    Infrastructure infrastructure = createMock(Infrastructure.class);
    ISpecificationSource specSource = createMock(ISpecificationSource.class);
    INamespace namespace = createMock(INamespace.class);
    IComponentSpecification ics = createMock(IComponentSpecification.class);
    expect(
        namespace.getPropertyValue("org.apache.tapestry.page-class-packages"))
        .andReturn("com.globant.katari.trails.pages");
    expect(cycle.getInfrastructure()).andReturn(infrastructure);
    expect(infrastructure.getSpecificationSource()).andReturn(
        specSource);
    Resource r = new ClasspathResource(new DefaultClassResolver(),
        "com/globant/katari/trails/test/pages/DefaultException.page");
    expect(specSource.getPageSpecification(r)).andReturn(ics);
    replay(cycle);
    replay(infrastructure);
    replay(specSource);
    replay(namespace);

    ModuleTemplateSourceDelegate msd = new ModuleTemplateSourceDelegate();
    msd.setCustomPagesLocation("com/globant/katari/trails/test/pages");
    assertNotNull(msd.findPageSpecification(cycle, namespace,
        "DefaultException"));

    verify(cycle);
    verify(namespace);
    verify(specSource);
    verify(infrastructure);
  }

  public void testFindComponentSpecificationDefaultLocation() {
    IRequestCycle cycle = createMock(IRequestCycle.class);
    Infrastructure infrastructure = createMock(Infrastructure.class);
    ISpecificationSource specSource = createMock(ISpecificationSource.class);
    INamespace namespace = createMock(INamespace.class);
    IComponentSpecification ics = createMock(IComponentSpecification.class);
    expect(namespace
          .getPropertyValue("org.apache.tapestry.component-class-packages"))
        .andReturn("com.globant.katari.trails.test.components");
    expect(cycle.getInfrastructure()).andReturn(infrastructure);
    expect(infrastructure.getSpecificationSource()).andReturn(
        specSource);
    Resource r = new ClasspathResource(new DefaultClassResolver(),
        "com/globant/katari/trails/test/components/Border.jwc");
    expect(specSource.getComponentSpecification(r)).andReturn(ics);;
    replay(cycle);
    replay(infrastructure);
    replay(specSource);
    replay(namespace);

    ModuleTemplateSourceDelegate msd = new ModuleTemplateSourceDelegate();
    assertNotNull(msd.findComponentSpecification(cycle, namespace, "Border"));

    verify(cycle);
    verify(namespace);
    verify(specSource);
    verify(infrastructure);
  }

  public void testFindComponentSpecificationOverride() {
    IRequestCycle cycle = createMock(IRequestCycle.class);
    Infrastructure infrastructure = createMock(Infrastructure.class);

    ISpecificationSource specSource;
    specSource = createMock(ISpecificationSource.class);

    INamespace namespace = createMock(INamespace.class);

    IComponentSpecification ics;
    ics = createMock(IComponentSpecification.class);

    expect(namespace
            .getPropertyValue("org.apache.tapestry.component-class-packages"))
        .andReturn("com.globant.katari.trails..components");
    expect(cycle.getInfrastructure()).andReturn(infrastructure);
    expect(infrastructure.getSpecificationSource()).andReturn(
        specSource);
    Resource r = new ClasspathResource(new DefaultClassResolver(),
        "com/globant/katari/trails/test/components/Border.jwc");
    expect(specSource.getComponentSpecification(r)).andReturn(ics);
    replay(cycle);
    replay(infrastructure);
    replay(specSource);
    replay(namespace);

    ModuleTemplateSourceDelegate msd = new ModuleTemplateSourceDelegate();
    msd.setCustomComponentsLocation(
        "com/globant/katari/trails/test/components");
    assertNotNull(msd.findComponentSpecification(cycle, namespace, "Border"));

    verify(cycle);
    verify(namespace);
    verify(specSource);
    verify(infrastructure);
  }
}

