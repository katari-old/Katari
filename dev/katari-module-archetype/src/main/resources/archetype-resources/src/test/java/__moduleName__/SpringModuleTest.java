#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.${moduleName};

import javax.servlet.ServletContext;

import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.mock.web.MockServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.core.web.ConfigurableModule;

/** Tests the module.xml.
 *
 * The test performed is very naive. Just verifies that the application context
 * can be created and the ${moduleName}.module bean is of the expected type.
 */
public class SpringModuleTest {

  private XmlWebApplicationContext appContext = null;

  @Before
  public void createContexts() throws Exception {

    ServletContext sc;
    sc = new MockServletContext(".", new FileSystemResourceLoader());
    appContext = new XmlWebApplicationContext();
    appContext.setServletContext(sc);
    appContext.setConfigLocations(new String[] {
      "classpath:${packageInPathFormat}/${moduleName}/module.xml" });
    appContext.refresh();
  }

  @Test
  public void testModuleType() {
    Object logout = appContext.getBean("${moduleName}.module");
    assertThat(logout, instanceOf(ConfigurableModule.class));
  }

  @After
  public void close() {
    appContext.close();
  }
}

