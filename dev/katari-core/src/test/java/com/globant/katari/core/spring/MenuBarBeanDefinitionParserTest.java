/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.globant.katari.core.web.MenuBar;
import com.globant.katari.core.web.MenuNode;

/* Tests a MenuBarBeanDefinitionParser.
 */
public class MenuBarBeanDefinitionParserTest extends TestCase {

  private static ApplicationContext beanFactory;

  /** This method returns a BeanFactory.
   *
  * @return a BeanFactory
  */
  public static synchronized ApplicationContext getBeanFactory() {
   if (beanFactory == null) {
     beanFactory = new FileSystemXmlApplicationContext(
         new String[] {
             "classpath:/com/globant/katari/core/spring/module.xml"});
   }
   return beanFactory;
 }

  public void testParse() {
    MenuBar menuBar = (MenuBar) getBeanFactory().getBean("menubar");
    assertEquals("root", menuBar.getName());

    assertEquals("menu-item-1", menuBar.getChildNodes().get(0).getName());
    assertEquals("menu-item-1",
        menuBar.getChildNodes().get(0).getDisplayName());
    assertEquals("menu-item-2", menuBar.getChildNodes().get(1).getName());
    assertEquals("menu-node-3", menuBar.getChildNodes().get(2).getName());

    MenuNode level2 = menuBar.getChildNodes().get(2);
    assertEquals("menu-node-3-1", level2.getChildNodes().get(0).getName());
    assertEquals("menu-item-3-2", level2.getChildNodes().get(1).getName());
    assertEquals("menu-node-3-3", level2.getChildNodes().get(2).getName());

    MenuNode level3 = level2.getChildNodes().get(2);
    assertEquals("menu-item-3-3-1", level3.getChildNodes().get(0).getName());

    // Check if the item is correctly loaded.
    MenuNode sampleItem = level2.getChildNodes().get(1);
    assertEquals("item-3-2", sampleItem.getDisplayName());
    assertEquals(2, sampleItem.getPosition());
    assertEquals("tooltip-3-2", sampleItem.getToolTip());
    assertEquals("link-3-2", sampleItem.getLinkPath());
  }
}

