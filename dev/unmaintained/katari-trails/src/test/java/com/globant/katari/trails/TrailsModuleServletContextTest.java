package com.globant.katari.trails;

import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;

import junit.framework.TestCase;

public class TrailsModuleServletContextTest extends TestCase {

  public void testSetAttributeOk() {
    BeanFactory bf = EasyMock.createMock(BeanFactory.class);
    ServletContext sc = EasyMock.createMock(ServletContext.class);
    sc.setAttribute("aName", "aValue");
    EasyMock.replay(sc);
    TrailsModuleServletContext tmsc = new TrailsModuleServletContext(bf, sc);
    // no exception
    tmsc.setAttribute("aName", "aValue");
    EasyMock.verify(sc);
  }

  public void testSetAttributeInvalid() {
    BeanFactory bf = EasyMock.createMock(BeanFactory.class);
    ServletContext sc = EasyMock.createMock(ServletContext.class);
    EasyMock.replay(sc);
    TrailsModuleServletContext tmsc = new TrailsModuleServletContext(bf, sc);
    try {
      tmsc.setAttribute(
          WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
          "aValue");
    } catch (IllegalArgumentException iae) {
      EasyMock.verify(sc);
      return;
    }
    fail();
  }

  public void testGetAttribute() {
    BeanFactory bf = EasyMock.createMock(BeanFactory.class);
    ServletContext sc = EasyMock.createMock(ServletContext.class);
    EasyMock.expect(sc.getAttribute("aName")).andReturn("aValue");
    EasyMock.replay(sc);
    TrailsModuleServletContext tmsc = new TrailsModuleServletContext(bf, sc);
    // no exception
    String result = (String) tmsc.getAttribute("aName");
    assertEquals("aValue", result);
    EasyMock.verify(sc);
  }

  public void testGetAttributeBeanFactory() {
    BeanFactory bf = EasyMock.createMock(BeanFactory.class);
    ServletContext sc = EasyMock.createMock(ServletContext.class);
    EasyMock.replay(sc);
    TrailsModuleServletContext tmsc = new TrailsModuleServletContext(bf, sc);
    // no exception
    BeanFactory bf2 = (BeanFactory) tmsc
        .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    assertEquals(bf, bf2);
    EasyMock.verify(sc);
  }


}
