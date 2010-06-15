/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.globant.katari.core.web.MenuBar;

/** This class parses the xml fragment for the menu beans.
 */
public class MenuBarBeanDefinitionParser extends AbstractBeanDefinitionParser {

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(MenuBarBeanDefinitionParser.class);

  /** Parses the xml fragment.
   *
   * {@inheritDoc}
   */
  @Override
  protected AbstractBeanDefinition parseInternal(final Element element, final
      ParserContext parserContext) {

    if (log.isTraceEnabled()) {
      log.trace("Entering parseInternal('" + element.getTagName() + "')");
    }

    BeanDefinitionBuilder factory;
    factory = BeanDefinitionBuilder.rootBeanDefinition(MenuBarFactory.class);

    BeanDefinitionBuilder menubar;
    menubar = BeanDefinitionBuilder.rootBeanDefinition(MenuBar.class);
    factory.addPropertyValue("menuBar", menubar.getBeanDefinition());

    // Obtain all menuNode and menuItem children.
    List<Element> childElements = getMenuNodesOrItems(element);
    if (childElements != null && childElements.size() > 0) {
      parseChildren(childElements, factory);
    }
    AbstractBeanDefinition result = factory.getBeanDefinition();
    log.trace("Leaving parseInternal");
    return result;
  }

  /** Returns the list of child menu nodes (menuNode or menuItem) of the
   * specified dom element.
   *
   * @param element The dom element. It cannot be null.
   *
   * @return a list of dom elements. It never returns null.
   */
  private static List<Element> getMenuNodesOrItems(final Element element) {
    NodeList nodeList = element.getChildNodes();
    List<Element> children = new ArrayList<Element>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      boolean isMenuElement = node instanceof Element
        && (DomUtils.nodeNameEquals(node, "menuItem")
         || DomUtils.nodeNameEquals(node, "menuNode"));
      if (isMenuElement) {
        children.add((Element) node);
      }
    }
    return children;
  }

  /** Parses the list of children of a node.
   *
   * @param childElements The list of children. It cannot be null.
   *
   * @param parent the bean definition factory for the parent menu node. It
   * cannot be null.
   */
  @SuppressWarnings("unchecked")
  private static void parseChildren(final List<Element> childElements,
      final BeanDefinitionBuilder parent) {
    log.trace("Entering parseChildren");
    Validate.notNull(childElements, "The child elements cannot be null.");
    Validate.notNull(parent, "The parent cannot be null.");
    ManagedList children = new ManagedList(childElements.size());
    for (int i = 0; i < childElements.size(); ++i) {
      Element element = childElements.get(i);
      BeanDefinitionBuilder child;
      if (DomUtils.nodeNameEquals(element, "menuItem")) {
        child = parseMenuItem(element);
      } else {
        child = parseMenuNode(element);
        List<Element> nodeChildren = getMenuNodesOrItems(element);
        parseChildren(nodeChildren, child);
      }
      children.add(child.getBeanDefinition());
    }
    parent.addPropertyValue("children", children);
    log.trace("Leaving parseChildren");
  }

  /** Parses a menu item, that is, a leaf menu node.
   *
   * @param element The xml element to parse. It cannot be null.
   *
   * @return BeanDefinitionBuilder a bean definition factory that builds a
   * MenuNodeHolder. It never returns null.
   */
  private static BeanDefinitionBuilder parseMenuItem(final Element element) {
    log.trace("Entering parseMenuItem");
    Validate.notNull(element, "The element cannot be null.");
    BeanDefinitionBuilder node;
    node = BeanDefinitionBuilder.rootBeanDefinition(MenuNodeHolder.class);

    node.addConstructorArgValue(element.getAttribute("display"));
    node.addConstructorArgValue(element.getAttribute("name"));
    node.addConstructorArgValue(element.getAttribute("position"));
    node.addConstructorArgValue(element.getAttribute("tooltip"));
    node.addConstructorArgValue(element.getAttribute("link"));
    log.trace("Leaving parseMenuItem");
    return node;
  }

  /** Parses a menu node.
   *
   * @param element The xml element to parse. It cannot be null.
   *
   * @return BeanDefinitionBuilder a bean definition factory that builds a
   * MenuNode. It never returns null.
   */
  private static BeanDefinitionBuilder parseMenuNode(final Element element) {
    log.trace("Entering parseMenuNode");
    Validate.notNull(element, "The element cannot be null.");
    BeanDefinitionBuilder node;
    node = BeanDefinitionBuilder.rootBeanDefinition(MenuNodeHolder.class);

    node.addConstructorArgValue(element.getAttribute("display"));
    node.addConstructorArgValue(element.getAttribute("name"));
    node.addConstructorArgValue(element.getAttribute("position"));
    node.addConstructorArgValue(element.getAttribute("tooltip"));
    log.trace("Leaving parseMenuNode");
    return node;
  }
}
