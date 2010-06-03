/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/** Tests the ListFactoryAdder with a spring application context.
 * @author gerardo.bercovich
 */
public class ListFactoryAdderTest {
  final String springBeans = "<?xml version='1.0' encoding='UTF-8'?>\n"
    + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
    + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
    + " xmlns:util='http://www.springframework.org/schema/util'\n"
    + " xsi:schemaLocation='\n"
    + " http://www.springframework.org/schema/beans "
    + " http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\n"
    + " http://www.springframework.org/schema/util "
    + " http://www.springframework.org/schema/util/spring-util-2.5.xsd'>\n"
    + " <util:list id='colorList'>\n"
    + "  <value>Yellow</value>\n"
    + " </util:list>\n"
    + " <bean id='monocromeColorAdder'"
    + "   class='com.globant.katari.core.web.ListFactoryAdder'>\n"
    + "   <constructor-arg index='0' value='colorList'/>\n"
    + "   <constructor-arg index='1'>\n"
    + "     <list>\n"
    + "       <value>Black</value>\n"
    + "       <value>White</value>\n"
    + "     </list>\n"
    + "   </constructor-arg>\n"
    + " </bean>\n"
    + " <bean id='rgbColorAdder'"
    + "   class='com.globant.katari.core.web.ListFactoryAdder'>\n"
    + "   <constructor-arg index='0' value='colorList'/>\n"
    + "   <constructor-arg index='1'>\n"
    + "     <list>\n"
    + "       <value>Red</value>\n"
    + "       <value>Green</value>\n"
    + "       <value>Blue</value>\n"
    + "     </list>\n"
    + "   </constructor-arg>\n"
    + " </bean>\n"
    + "</beans>\n";


  final String wrongTargetBeans =
    "<?xml version='1.0' encoding='UTF-8'?>\n"
    + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
    + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
    + " xmlns:util='http://www.springframework.org/schema/util'\n"
    + " xsi:schemaLocation='\n"
    + " http://www.springframework.org/schema/beans "
    + " http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\n"
    + " http://www.springframework.org/schema/util "
    + " http://www.springframework.org/schema/util/spring-util-2.5.xsd'>\n"
    + " <bean id='wrongTarget' "
    + "   class='com.globant.katari.core.web.ListFactoryAdderTest."
    + "WrongTarget'/>\n"
    + " <bean id='monocromeColorAdderWrongTarget'"
    + "   class='com.globant.katari.core.web.ListFactoryAdder'>\n"
    + "   <constructor-arg index='0' value='wrongTarget'/>\n"
    + "   <constructor-arg index='1'>\n"
    + "     <list>\n"
    + "       <value>Black</value>\n"
    + "       <value>White</value>\n"
    + "     </list>\n"
    + "   </constructor-arg>\n"
    + " </bean>\n"
    + "</beans>\n";

  final String optionalBeans =
    "<?xml version='1.0' encoding='UTF-8'?>\n"
    + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
    + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
    + " xmlns:util='http://www.springframework.org/schema/util'\n"
    + " xsi:schemaLocation='\n"
    + " http://www.springframework.org/schema/beans "
    + " http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\n"
    + " http://www.springframework.org/schema/util "
    + " http://www.springframework.org/schema/util/spring-util-2.5.xsd'>\n"
    + " <bean class='com.globant.katari.core.web.ListFactoryAdder'>\n"
    + "   <constructor-arg index='0' value='nonexistingBean'/>\n"
    + "   <constructor-arg index='1' value='true'/>\n"
    + "   <constructor-arg index='2'>\n"
    + "     <list>\n"
    + "       <value>Black</value>\n"
    + "       <value>White</value>\n"
    + "     </list>\n"
    + "   </constructor-arg>\n"
    + " </bean>\n"
    + "</beans>\n";

  /** Creates an application factory with the spring beans xml and verify the
   * the color list elements.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testAddColors() {
    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      @Override
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(springBeans.getBytes())};
      }
    };
    context.refresh();
    List<String> colorList = (List<String>) context.getBean("colorList");
    Iterator<String> colors = colorList.iterator();
    // Default
    assertThat(colors.next(), is("Yellow"));
    // MonoCrome
    assertThat(colors.next(), is("Black"));
    assertThat(colors.next(), is("White"));
    // RGB
    assertThat(colors.next(), is("Red"));
    assertThat(colors.next(), is("Green"));
    assertThat(colors.next(), is("Blue"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddColors_wrongTarget() {
    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      @Override
      protected Resource[] getConfigResources() {
        return new Resource[] {
          new ByteArrayResource(wrongTargetBeans.getBytes())
        };
      }
    };
    context.refresh();
  }

  @Test
  public void testAddColors_optional() {
    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      @Override
      protected Resource[] getConfigResources() {
        return new Resource[] {
          new ByteArrayResource(optionalBeans.getBytes())
        };
      }
    };
    context.refresh();
    // there is nothing to check.
  }

  /** A class used to verify the list target validation.
   * @author gerardo.bercovich
   */
  public static class WrongTarget {
    public void setUselessProperty(final String theUselessProperty) {
    }
  }
}

