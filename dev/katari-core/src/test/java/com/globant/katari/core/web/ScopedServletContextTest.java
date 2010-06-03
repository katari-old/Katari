package com.globant.katari.core.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.util.WebUtils;

/**
 * Tests the behaviour of the {@link ScopedServletContext}
 * @author pablo.saavedra
 */
public class ScopedServletContextTest {

  /**
   * Checks the behaviour of the scoped servlet context.
   */
  @Test
  public void checkBehaviour() {
    MockServletContext parent = new MockServletContext();
    parent.setAttribute("parent", "me");
    parent.setAttribute("override", "you won't see this");
    parent.addInitParameter("hidden", "true");
    parent.removeAttribute(WebUtils.TEMP_DIR_CONTEXT_ATTRIBUTE);

    ScopedServletContext context = new ScopedServletContext(parent);
    context.setAttribute("override", "yes!");
    context.addInitParameter("visible", "true");

    Enumeration<String> attributes = context.getAttributeNames();
    checkEnumeration(attributes, 2, "parent", "override");

    Assert.assertEquals("me", context.getAttribute("parent"));
    Assert.assertEquals("yes!", context.getAttribute("override"));

    Enumeration<String> params = context.getInitParameterNames();
    Assert.assertTrue(params.hasMoreElements());
    Assert.assertEquals("visible", params.nextElement());
    Assert.assertEquals("true", context.getInitParameter("visible"));

    context.removeAttribute("override");
    checkEnumeration(context.getAttributeNames(), 2, "parent", "override");
    Assert.assertEquals("me", context.getAttribute("parent"));
    Assert.assertEquals("you won't see this", context.getAttribute("override"));

    context.removeAttribute("override");
    checkEnumeration(context.getAttributeNames(), 1, "parent");
    Assert.assertEquals("me", context.getAttribute("parent"));
  }

  /**
   * Checks that the enumeration contains the given elements.
   * @param en
   *          The enumeration to check.
   * @param size
   *          The expected size.
   * @param elements
   *          Elements that are expected to be in the enumeration.
   */
  private <T> void checkEnumeration(final Enumeration<T> en, final int size,
      final T... elements) {
    ArrayList<T> attrs = Collections.list(en);
    Assert.assertEquals(size, attrs.size());
    for (T elem : elements) {
      Assert.assertTrue(attrs.contains(elem));
    }
  }
}
