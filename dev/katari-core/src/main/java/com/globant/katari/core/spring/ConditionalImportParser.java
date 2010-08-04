/*
http://robertmaldon.blogspot.com/2007/04/conditionally-defining-spring-beans.html
 */

/* vim: set ts=2 et sw=2 cindent fo=qroca: */
package com.globant.katari.core.spring;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Implements the conditional import spring extension.
 *
 * This class parses the import element from the katari namespace. That element
 * works like spring import tag, but specific for katari modules, and the import
 * can be skipped if certain conditions are met.
 *
 * To decide if the module is going to be imported, this class uses
 * property-name and property-value attributes: the module is imported only if
 * the value of the property named [property-name] is [property-value].
 *
 * The resource to be imported is always a katari module: a file named
 * module.xml. The parser searches in the 'resource' path in the classpath.
 *
 * This parser looks for property values in property files loaded by the
 * 'properties' element (also in the katari namespace). If the import specifies
 * a properties-ref attribute, the property value is looked for in the property
 * element with a matching name. Otherwise, it is looked for in all the declared
 * properties. If the same property is found in more that one property file,
 * then it considers the last one.
 *
 * All properties elements must go before the first import element. You must
 * specify at least one properties element.
 */
public class ConditionalImportParser implements BeanDefinitionParser {

  /** Constant for the katari:import element. */
  private static final String IMPORT = "import";

  /** Constant for the resource attribute. */
  private static final String MODULE = "module";

  /** Constant for the ref-properties attribute. */
  private static final String PROPERTIES_REF = "properties-ref";

  /** Constant for the property-value attribute. */
  private static final String PROPERTY_VALUE = "property-value";

  /** Constant for the property-name attribute. */
  private static final String PROPERTY_NAME = "property-name";

  /** Constant for the module name. */
  private static final String KATARI_MODULE_NAME = "module.xml";

  /** The prefix to add to the module name, so that spring loads it from the
   * classpath.
   */
  private static final String CLASSPATH_PREFIX = "classpath:";

  private PropertiesParser propertiesParser;

  public ConditionalImportParser(final PropertiesParser thePropertiesParser) {
    super();
    this.propertiesParser = thePropertiesParser;
  }

  /** Parses an import element.
   *
   * It must have a module attribute, and may have optional properties-ref,
   * property-name and property-value attributes.
   *
   * @return always returns null.
   *
   * {@inheritDoc}
   */
  public BeanDefinition parse(final Element element,
      final ParserContext parserContext) {
    if (DomUtils.nodeNameEquals(element, IMPORT)) {
      // notify the properties parser that import has begun and no more
      // <katari:properties> should be allowed
      propertiesParser.katariImportStarted();
      if (evalCondition(element)) {
        return parseAndRegisterBean(element, parserContext);
      }
    }
    return null;
  }

  /**
   * Evaluates the condition specified in the import element.
   *
   * If the condition evals to true, the module (specified by the resource
   * attribute) should be loaded
   *
   * @param condition the condition element. It may have a properties-ref,
   * property-name and property-value optional attributes.
   *
   * @return true if condition does not have an attribute named
   * [attribute-name] or the attribute named [attribute-name] is
   * [attribute-value].
   */
  private boolean evalCondition(final Element condition) {
    String refProperties = condition.getAttribute(PROPERTIES_REF);
    String propertyName = condition.getAttribute(PROPERTY_NAME);
    String propertyValue = condition.getAttribute(PROPERTY_VALUE);
    if (!condition.hasAttribute(PROPERTY_NAME)) {
      // property-name not specified, evaluates to true.
      return true;
    }
    String actualPropertyValue = propertiesParser.getProperty(refProperties,
        propertyName);
    if ((!StringUtils.isEmpty(actualPropertyValue))
        && (actualPropertyValue.equals(propertyValue))) {
      return true;
    }
    return false;
  }

  /**
   * Parses the and register bean.
   *
   * @param element
   *          the element
   * @param parserContext
   *          the parser context
   *
   * @return the bean definition
   */
  private BeanDefinition parseAndRegisterBean(final Element element,
      final ParserContext parserContext) {

    XmlBeanDefinitionReader beanReader;
    beanReader = new XmlBeanDefinitionReader(parserContext.getRegistry());

    // Configure the bean definition reader with this context's
    // resource loading environment.
    ResourceLoader resourceLoader;
    resourceLoader = parserContext.getReaderContext().getResourceLoader();
    beanReader.setResourceLoader(resourceLoader);
    beanReader.setEntityResolver(new ResourceEntityResolver(resourceLoader));

    String location = element.getAttribute(MODULE);
    location = location.replaceAll("\\.", "/");
    location = CLASSPATH_PREFIX.concat(location).concat("/").concat(
        KATARI_MODULE_NAME);
    beanReader.loadBeanDefinitions(location);

    return null;
  }
}

