/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.globant.katari.core.web.MenuBar;

public class ConditionalImportParserTest {

  @Test
  public void testImportTrueCondition() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test.properties'/>\n" 
      + "  <katari:import properties-ref=\"katari.props\" resource='com.globant.katari.core.spring'\n" 
      + "    property-name='authentication.mode' property-value='CAS'/>"
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
    Object menubar = context.getBean("menubar");
    assertThat(menubar, is(MenuBar.class));

    context.close();
  }
  
  @Test
  public void testImport2Properties() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test.properties'/>\n" 
      + "  <katari:properties name='katari2.props' resource='classpath:/com/globant/katari/core/spring/test2.properties'/>\n" 
      + "  <katari:import properties-ref=\"katari2.props\" resource='com.globant.katari.core.spring'\n" 
      + "    property-name='authentication.mode' property-value='FB'/>"
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
    Object menubar = context.getBean("menubar");
    assertThat(menubar, is(MenuBar.class));

    context.close();
  }
  
  @Test
  public void testImport2PropertiesNoPropertiesRef() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test.properties'/>\n" 
      + "  <katari:properties name='katari2.props' resource='classpath:/com/globant/katari/core/spring/test2.properties'/>\n" 
      + "  <katari:import resource='com.globant.katari.core.spring'\n" 
      + "    property-name='authentication.mode' property-value='FB'/>"
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
    Object menubar = context.getBean("menubar");
    assertThat(menubar, is(MenuBar.class));

    context.close();
  }
  
  @Test(expected=RuntimeException.class)
  public void testImport2PropertiesNoPropertiesRefInvalidProperty() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test.properties'/>\n" 
      + "  <katari:properties name='katari2.props' resource='classpath:/com/globant/katari/core/spring/test2.properties'/>\n" 
      + "  <katari:import resource='com.globant.katari.core.spring'\n" 
      + "    property-name='invalid' property-value='CAS'/>"
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
 
  }
  
  @Test(expected=RuntimeException.class)
  public void testImportPropertiesRefInvalidProperty() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test.properties'/>\n" 
      + "  <katari:import resource='com.globant.katari.core.spring'\n" 
      + "    properties-ref=\"katari.props\" property-name='invalid' property-value='CAS'/>"
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
  }

  @Test(expected=RuntimeException.class)
  public void testImportWrongPropertiesRef() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test.properties'/>\n" 
      + "  <katari:import properties-ref=\"no.props\" resource='com.globant.katari.core.spring'\n" 
      + "    property-name='authentication.mode' property-value='CAS'/>"
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
    
  }

  @Test(expected=NoSuchBeanDefinitionException.class)
  public void testImportFalseCondition() {

    final String beans =
    "<?xml version='1.0' encoding='UTF-8'?>\n"
    + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
    + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
    + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
    + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
    + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
    + "    http://www.globant.com/schema/katari\n"
    + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
    + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test2.properties'/>\n" 
    + "  <katari:import resource='com.globant.katari.core.spring'\n" 
    + "    property-name='authentication.mode' property-value='CAS'/>"
    + "</beans>\n";
    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
    context.getBean("menubar");

    context.close();
  }
  
  @Test(expected=RuntimeException.class)
  public void testImportNoCondition() {

    final String beans =
    "<?xml version='1.0' encoding='UTF-8'?>\n"
    + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
    + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
    + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
    + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
    + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
    + "    http://www.globant.com/schema/katari\n"
    + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
    + "  <katari:import resource='classpath:/com/globant/katari/core/spring/module.xml'\n" 
    + "    property-name='authentication.mode' property-value='CAS'/>"
    + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();

  }
  
  @Test
  public void testImportCombination() {

    final String beans =
    "<?xml version='1.0' encoding='UTF-8'?>\n"
    + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
    + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
    + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
    + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
    + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
    + "    http://www.globant.com/schema/katari\n"
    + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
    + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test2.properties'/>\n" 
    + "  <katari:import resource='com.globant.katari.core.spring'\n" 
    + "    property-name='authentication.mode' property-value='CAS'/>"
    + "  <katari:import resource='com.globant.katari.core.spring2'\n" 
    + "    property-name='authentication.mode' property-value='FB'/>"
    + "</beans>\n";
    System.setProperty("authentication.mode", "CAS");
    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
    Object menubar = context.getBean("secured-menubar");
    assertThat(menubar, is(MenuBar.class));

    try {
      context.getBean("menubar");
      fail("Shouldn't reach this");
    } catch (NoSuchBeanDefinitionException e) {
    }
    
    context.close();
  }
  
  @Test(expected=RuntimeException.class)
  public void testImportAlternateCombination() {

    final String beans =
    "<?xml version='1.0' encoding='UTF-8'?>\n"
    + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
    + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
    + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
    + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
    + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
    + "    http://www.globant.com/schema/katari\n"
    + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
    + "  <katari:properties name='katari.props' resource='classpath:/com/globant/katari/core/spring/test2.properties'/>\n" 
    + "  <katari:import resource='com.globant.katari.core.spring'\n" 
    + "    property-name='authentication.mode' property-value='CAS'/>"
    + "  <katari:properties name='katari2.props' resource='classpath:/com/globant/katari/core/spring/test2.properties'/>\n" 
    + "  <katari:import resource='com.globant.katari.core.spring2'\n" 
    + "    property-name='authentication.mode' property-value='FB'/>"
    + "</beans>\n";
    System.setProperty("authentication.mode", "CAS");
    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
 
  }
}

