/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig;

import org.springframework.context.ApplicationContext;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.shindig.testsupport.SpringTestUtils;

import com.globant.katari.core.web.ConfigurableModule;

/** Tests the module.xml.
 *
 * The test performed is very naive. Just verifies that the application context
 * can be created and the shindig.module bean is of the expected type.
 */
public class SpringModuleTest {

  @Test
  public void testModuleType() {
    ApplicationContext appContext = SpringTestUtils.get().getBeanFactory();
    Object module = appContext.getBean("shindig.module");
    assertThat(module, instanceOf(ConfigurableModule.class));
  }

}
