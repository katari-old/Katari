package com.globant.katari.core.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Implements the katari:properties spring extension. This class parses the
 * properties element from the katari namespace. The properties file to be
 * loaded is specified in the resource attribute in the same way as for any
 * other Spring resource (e.g.: classpath:/com/katari/ui/ui.properties).
 * 
 * These properties are used by the import element, also defined in the katari
 * namespace, for condition evaluation. Also, the resulting properties object
 * (instance of java.util.Properties) is stored as a bean in the Spring
 * application context and available for later use.
 * 
 * The bean's name is defined in the name attribute of the properties element.
 * 
 * An import element from the katari namespace can refer to one of this
 * properties instance by setting the bean name in the properties-ref attribute.
 */
public class PropertiesParser implements BeanDefinitionParser {

  /** Constant for the properties element. */
  private static final String PROPERTIES = "properties";

  /** Constant for the name attribute. */
  private static final String NAME = "name";

  /** Constant for the resource attribute. */
  private static final String RESOURCE = "resource";

  /** The logger. */
  private static Log log = LogFactory.getLog(PropertiesParser.class);

  /** The map holding all the katari:properties defined. */
  private Map<String, Properties> propertiesMap = new LinkedHashMap<String, Properties>();

  /** we should fail if an import is found after a katari:import definition */
  public boolean katariImportStarted = false;

  /**
   * {@inheritDoc}
   */
  public BeanDefinition parse(Element element, ParserContext parserContext) {
    if (DomUtils.nodeNameEquals(element, PROPERTIES)) {
      return parseAndRegisterBean(element, parserContext);
    }
    return null;
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
    String name = element.getAttribute(NAME);
    Validate.notEmpty(name, "The attribute name has no value");
    if (katariImportStarted) {
      throw new RuntimeException("katari:properties [" + name
          + "] found after a katari:import definition");
    }

    String location = element.getAttribute(RESOURCE);
    Resource resource = parserContext.getReaderContext().getResourceLoader()
        .getResource(location);
    // create the properties object
    PropertiesFactoryBean pfb = new PropertiesFactoryBean();
    pfb.setLocation(resource);
    try {
      pfb.afterPropertiesSet();
      log.trace("Katari properties loaded: bean name=" + name + "values ["
          + pfb.getObject() + "]");
      addProperties(name, (Properties) pfb.getObject());
      // create a bean definition to get these properties available in the
      // application context
      AbstractBeanDefinition def = new GenericBeanDefinition();
      def.setBeanClass(PropertiesFactoryBean.class);
      MutablePropertyValues values = new MutablePropertyValues();
      values.addPropertyValue("location", resource);
      def.setPropertyValues(values);
      parserContext.getRegistry().registerBeanDefinition(name, def);
    } catch (IOException e) {
      log.error("Error getting katari properties", e);
    }
    return null;
  }

  /**
   * Adds the properties instance to the map of all the properties instances.
   * 
   * @param resourceName
   *          the resource name
   * @param resource
   *          the resource
   */
  private void addProperties(final String resourceName,
      final Properties resource) {
    this.propertiesMap.put(resourceName, resource);
  }

  /**
   * Gets the property value from the katari:properties element.
   * 
   * @param refProperties
   *          the properties bean to be used, if null, all available properties
   *          are used
   * @param propertyName
   *          the propertyName value.It cannot be null
   * 
   * @return the property
   * @TODO analyze using default values for missing properties
   */
  public String getProperty(final String refProperties,
      final String propertyName) {
    Validate.notNull(propertyName, "The property name cannot be null.");
    // if a properties bean has been referenced, just look for the property in
    // it
    if (!StringUtils.isEmpty(refProperties)) {
      Properties properties = getProperties(refProperties);
      if (properties == null) {
        throw new RuntimeException("Properties bean [" + refProperties
            + "] couldn't be found");
      }
      String propertyValue = properties.getProperty(propertyName);
      if (StringUtils.isEmpty(propertyValue)) {
        throw new RuntimeException("Couldn't get value for [" + propertyName
            + "] in properties bean [" + refProperties + "]");
      }
      return propertyValue;
    }
    // no reference to a specific properties bean, use all available
    Collection<Properties> allProperties = getAllProperties();
    for (Properties properties : allProperties) {
      if (properties.containsKey(propertyName)) {
        return properties.getProperty(propertyName);
      }
    }
    throw new RuntimeException("Couldn't get value for [" + propertyName
        + "] in any of the properties beans");
  }

  /**
   * Gets the properties instance from the map of all the properties instances.
   * 
   * @param resourceName
   *          the resource name. It cannot be null
   * 
   * @return the properties bean instance with name resourceName or null if no
   *         instance registered for that name
   */
  private Properties getProperties(final String resourceName) {
    Validate.notNull(resourceName, "The resource name cannot be null.");
    return this.propertiesMap.get(resourceName);
  }

  /**
   * Gets the all properties instances registered.
   * 
   * @return the all properties
   */
  private Collection<Properties> getAllProperties() {
    List<Properties> values = new LinkedList<Properties>(propertiesMap.values());
    Collections.reverse(values);
    return values;
  }

  /**
   * Sets the katari import started flag.
   * 
   * @param katariImportStarted
   *          the new katari import started
   */
  public void katariImportStarted() {
    this.katariImportStarted = true;
  }
}
