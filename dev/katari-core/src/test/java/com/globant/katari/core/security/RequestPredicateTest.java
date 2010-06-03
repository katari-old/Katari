package com.globant.katari.core.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Tests the Request Predicates.
 * @author rcunci
 */
public class RequestPredicateTest extends TestCase {

  /** predicate. */
  private RequestPredicate predicate;

  /**
   * {@inheritDoc}.
   */
  @Override
  protected void setUp() throws Exception {
    List<String> list = new ArrayList<String>();
    list.add(".*/module/module1/.*");
    list.add(".*/module/module2/.*");

    predicate = new RequestPredicate(list);
  }

  /**
   * Tests true predicates.
   */
  public void testEvaluate_true() {
    assertTrue(predicate.evaluate(createRequest("/module/module1/test.do")));
    assertTrue(predicate.evaluate(createRequest("/module/module2/test.do")));
  }

  /**
   * Tests false predicates.
   */
  public void testEvaluate_false() {
    assertFalse(predicate.evaluate(createRequest("/module/module11/test.do")));
    assertFalse(predicate.evaluate(createRequest("/module/module21/test.do")));
  }

  /**
   * Tests true and false predicates.
   */
  public void testEvaluate_mixed() {
    assertFalse(predicate.evaluate(createRequest("/module/module11/test.do")));
    assertTrue(predicate.evaluate(createRequest("/module/module1/test.do")));
    assertFalse(predicate.evaluate(createRequest("/module/module21/test.do")));
    assertTrue(predicate.evaluate(createRequest("/module/module2/test.do")));
  }
  /**
   * Creates a request with the given pathInfo.
   *
   * @param string
   * @return
   */
  private ServletRequest createRequest(final String pathInfo) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setContextPath("/bajocoste");
    request.setPathInfo(pathInfo);
    return request;
  }
}
