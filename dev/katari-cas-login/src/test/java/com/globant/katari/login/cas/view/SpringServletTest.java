/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas.view;

import org.springframework.context.ApplicationContext;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.login.cas.SpringTestUtils;

import org.springframework.web.servlet.mvc.UrlFilenameViewController;

/* Tests the spring-servlet.xml.
 *
 * The test performed is very naive. Just verifies that the application context
 * can be created and the '*.do' bean is of the expected type.
 */
public class SpringServletTest {

  @Test
  public void testServletType() {
    Object controller = SpringTestUtils.getViewBean("/*.do");
    assertThat(controller, instanceOf(UrlFilenameViewController.class));
  }
}

